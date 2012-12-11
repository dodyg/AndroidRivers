package com.silverkeytech.android_rivers

import android.os.AsyncTask
import android.content.Context
import com.silverkeytech.android_rivers.outlines.Opml
import android.app.ProgressDialog
import android.app.Activity
import com.silverkeytech.android_rivers.outliner.transformXmlToOpml
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import com.github.kevinsawicki.http.HttpRequest
import android.content.DialogInterface
import com.silverkeytech.android_rivers.outliner.OutlineContent
import com.silverkeytech.android_rivers.outliner.traverse
import java.util.ArrayList
import android.util.Log
import com.silverkeytech.android_rivers.outlines.Outline


public class DownloadOpml(it : Context?) : AsyncTask<String, Int, Result<Opml>>(){
    class object {
        public val TAG : String = javaClass<DownloadOpml>().getSimpleName()
    }

    var dialog: ProgressDialog = ProgressDialog(it)
    var context: Activity = it!! as Activity

    protected override fun onPreExecute() {
        dialog.setMessage("Please wait while downloading opml list")
        dialog.setIndeterminate(true)
        dialog.setCancelable(false)
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", object : DialogInterface.OnClickListener{

            public override fun onClick(p0: DialogInterface?, p1: Int) {
                p0!!.dismiss()
                this@DownloadOpml.cancel(true)
            }
        })
        dialog.show()
    }

    protected override fun doInBackground(vararg url: String?): Result<Opml>? {
        var req: String?
        try{
            req = HttpRequest.get(url[0])?.body()

            val opml = transformXmlToOpml(req?.replace("<?xml version=\"1.0\" encoding=\"utf-8\" ?>",""))
            return opml
        }
        catch(e: HttpRequestException){
            var ex = e.getCause()
            return Result.wrong(ex)
        }
    }

    var rawCallback : ((Result<Opml>) -> Unit)? = null
    var processedCallBack : ((Result<ArrayList<OutlineContent>>) -> Unit)? = null
    var processingFilter : ((Outline) -> Boolean)? = null

    protected override fun onPostExecute(result: Result<Opml>?) {
        dialog.dismiss()

        if (result != null){
            if (rawCallback != null)
                rawCallback!!(result)

            if (processedCallBack != null){
                if (result.isTrue()){
                    try{
                        val opml = result.value!!
                        val processed = opml.traverse(processingFilter)
                        Log.d(TAG, "Length of opml outlines ${opml.body?.outline?.get(0)?.outline?.size} compared to processed outlines ${processed.size}")
                        val res = Result.right(processed)
                        processedCallBack!!(res)
                    }catch (e : Exception){
                        val res = Result.wrong<ArrayList<OutlineContent>>(e)
                        processedCallBack!!(res)
                    }
                }else
                    processedCallBack!!(Result.wrong<ArrayList<OutlineContent>>(result.exception))
            }
        }
    }

    //Set up function to call when download is done
    public fun setCompletionCallback(action : ((Result<Opml>) -> Unit)? ){
        rawCallback = action
    }

    //set up function to call when download is done, include optional processing filter
    public fun setProcessedCompletedCallback(action : ((Result<ArrayList<OutlineContent>>) -> Unit)?,
                                             filter : ((Outline) -> Boolean)? = null){
        processedCallBack = action
        processingFilter = filter
    }
}