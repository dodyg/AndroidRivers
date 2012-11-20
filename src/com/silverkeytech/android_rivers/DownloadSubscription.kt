package com.silverkeytech.android_rivers

import android.content.Context
import android.app.ProgressDialog
import android.app.Activity
import android.os.AsyncTask
import com.github.kevinsawicki.http.HttpRequest
import android.util.Log
import android.widget.TextView

public class DownloadSubscription(it : Context?) : AsyncTask<String, Int, String>(){
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
        publishProgress(100)
        return req
    }

    protected override fun onProgressUpdate(vararg progress : Int?){
        Log.d("DD", "Progressing...")
    }

    protected override fun onPostExecute(res : String?){
        dialog.dismiss()
        var msg = context.findView<TextView>(R.id.main_message_tv)
        msg.setText("${res?.length} of text downloaded ... initial data ${res?.substring(0, 10)}")
    }
}
