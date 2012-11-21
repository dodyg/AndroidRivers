package com.silverkeytech.android_rivers

import android.content.Context
import android.app.ProgressDialog
import android.app.Activity
import android.os.AsyncTask
import com.github.kevinsawicki.http.HttpRequest
import android.util.Log
import android.widget.TextView
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import com.silverkeytech.android_rivers.outlines.Opml
import com.silverkeytech.android_rivers.outlines.Body

public class DownloadSubscription(it : Context?) : AsyncTask<String, Int, String>(){
    class object {
        public val TAG: String = javaClass<DownloadSubscription>().getSimpleName()!!
    }

    var dialog : ProgressDialog = ProgressDialog(it)
    var context : Activity = it!! as Activity

    protected override fun onPreExecute(){
        dialog.setMessage(context.getString(R.string.please_wait_while_loading))
        dialog.setIndeterminate(true)
        dialog.setCancelable(false)
        dialog.show()
    }

    protected override fun doInBackground(vararg url : String?): String?{
        var req = HttpRequest.get(url[0])?.body()
        var opml = transformFromXml(req)

        if(opml != null){
            var ct = opml?.body?.outline?.count()

            if (ct != null && ct!! > 0){
                var cnt : Float = 1.toFloat();
                var divisor = ct!!.toFloat()

                while(cnt < ct!!){
                    var progress  = Math.round((cnt / divisor) * 100.toFloat());
                    publishProgress(progress);
                    Thread.sleep(200)
                    cnt++
                }
            }
        }else{
            publishProgress(100)
        }

        return req
    }

    fun transformFromXml(xml : String?) : Opml?{
        var serial : Serializer = Persister()

        try{
            val opml : Opml? = serial.read(javaClass<Opml>(),xml)
            Log.d(TAG, "OPML ${opml?.head?.title} created on ${opml?.head?.getDateCreated()} and modified on ${opml?.head?.getDateModified()}")
            return opml
        }
        catch (e: Exception){
            Log.d(TAG, "Exception ${e.getMessage()}")
            return null
        }
    }

    protected override fun onProgressUpdate(vararg progress : Int?){
        Log.d("DD", "Progressing...${progress[0]}")
    }

    protected override fun onPostExecute(result : String?){
        dialog.dismiss()
        var msg = context.findView<TextView>(R.id.main_message_tv)
        msg.setText("${result?.length} of text downloaded ... initial data ${result?.substring(0, 10)}")
    }
}
