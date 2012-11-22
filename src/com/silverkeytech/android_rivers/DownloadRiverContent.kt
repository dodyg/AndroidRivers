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
import android.widget.Adapter
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView
import android.view.Gravity
import android.view.View
import android.widget.Toast
import android.widget.ArrayAdapter
import android.widget.ListView
import com.silverkeytech.android_rivers.outlines.Outline
import android.content.Intent
import com.google.gson.Gson
import com.silverkeytech.android_rivers.riverjs.FeedsRiver

public class DownloadRiverContent(it : Context?) : AsyncTask<String, Int, String>(){
    class object {
        public val TAG: String = javaClass<DownloadRiverContent>().getSimpleName()!!
    }

    var dialog : ProgressDialog = ProgressDialog(it)
    var context : Activity = it!! as Activity


    protected override fun onPreExecute() {
        dialog.setMessage(context.getString(R.string.please_wait_while_loading))
        dialog.setIndeterminate(true)
        dialog.setCancelable(false)
        dialog.show()
    }

    protected override fun doInBackground(vararg p0: String?): String? {
        var req = HttpRequest.get(p0[0])?.body()

        var gson = Gson()
        try{
            var scrubbed = scrubJsonP(req!!)

            var feeds = gson.fromJson(scrubbed, javaClass<FeedsRiver>())!!

            this.publishProgress(100);

            var info = "gson elements count ${feeds.updatedFeeds?.updatedFeed?.count()}"
            return info
        }
        catch(e : Exception)
                {
                    return e.getMessage()
                }
    }

    protected override fun onPostExecute(result: String?) {
        dialog.dismiss();
        context.toastee("This is result of download $result")
    }
}