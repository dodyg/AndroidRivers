/*
Android Rivers is an app to read and discover news using RiverJs, RSS and OPML format.
Copyright (C) 2012 Dody Gunawinata (dodyg@silverkeytech.com)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package com.silverkeytech.android_rivers

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.util.Log
import com.github.kevinsawicki.http.HttpRequest
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import com.silverkeytech.android_rivers.outliner.OutlineContent
import com.silverkeytech.android_rivers.outliner.transformXmlToOpml
import com.silverkeytech.android_rivers.outliner.traverse
import com.silverkeytech.android_rivers.outlines.Opml
import com.silverkeytech.android_rivers.outlines.Outline
import java.util.ArrayList

public class DownloadOpml(it: Context?): AsyncTask<String, Int, Pair<String, Result<Opml>>>(){
    class object {
        public val TAG: String = javaClass<DownloadOpml>().getSimpleName()
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

    protected override fun doInBackground(vararg url: String?): Pair<String, Result<Opml>>? {
        var link = url[0]!!
        var req: String?
        try{
            req = HttpRequest.get(link)?.body()

            val opml = transformXmlToOpml(req?.replace("<?xml version=\"1.0\" encoding=\"utf-8\" ?>", ""))
            return Pair(link, opml)
        }
        catch(e: HttpRequestException){
            var ex = e.getCause()
            return Pair(link, Result.wrong(ex))
        }
    }

    var rawCallback: ((Result<Opml>) -> Unit)? = null
    var processedCallBack: ((Result<ArrayList<OutlineContent>>) -> Unit)? = null
    var processingFilter: ((Outline) -> Boolean)? = null

    protected override fun onPostExecute(result: Pair<String, Result<Opml>>?) {
        dialog.dismiss()

        if (result != null){
            if (rawCallback != null)
                rawCallback!!(result.second)

            if (processedCallBack != null){
                if (result.second.isTrue()){
                    try{
                        val opml = result.second.value!!
                        val processed = opml.traverse(processingFilter)
                        Log.d(TAG, "Length of opml outlines ${opml.body?.outline?.get(0)?.outline?.size} compared to processed outlines ${processed.size}")

                        context.getApplication().getMain().setOpmlCache(result.first, processed)
                        val res = Result.right(processed)
                        processedCallBack!!(res)
                    }catch (e: Exception){
                        val res = Result.wrong<ArrayList<OutlineContent>>(e)
                        processedCallBack!!(res)
                    }
                }else
                    processedCallBack!!(Result.wrong<ArrayList<OutlineContent>>(result.second.exception))
            }
        }
    }

    //Set up function to call when download is done
    public fun setCompletionCallback(action: ((Result<Opml>) -> Unit)?) {
        rawCallback = action
    }

    //set up function to call when download is done, include optional processing filter
    public fun setProcessedCompletedCallback(action: ((Result<ArrayList<OutlineContent>>) -> Unit)?,
                                             filter: ((Outline) -> Boolean)? = null) {
        processedCallBack = action
        processingFilter = filter
    }
}