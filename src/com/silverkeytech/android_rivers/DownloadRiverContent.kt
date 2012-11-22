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
import android.app.LauncherActivity.ListItem
import com.silverkeytech.android_rivers.riverjs.FeedItem
import com.silverkeytech.android_rivers.riverjs.FeedSite

public class DownloadRiverContent(it : Context?) : AsyncTask<String, Int, FeedsRiver>(){
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

    protected override fun doInBackground(vararg p0: String?): FeedsRiver? {
        var req = HttpRequest.get(p0[0])?.body()

        var gson = Gson()
        try{
            var scrubbed = scrubJsonP(req!!)

            var feeds = gson.fromJson(scrubbed, javaClass<FeedsRiver>())!!

            this.publishProgress(100);

            return feeds
        }
        catch(e : Exception)
                {
                    return null
                }
    }

    protected override fun onPostExecute(result: FeedsRiver?) {
        dialog.dismiss();
        if (result == null)
            context.toastee("Sorry, we cannot process this feed")

        handleNewsListing(result!!)
    }

    fun handleNewsListing(river : FeedsRiver){

        var vals = ImmutableArrayListBuilder<FeedItem>()

        for(var f : FeedSite? in river.updatedFeeds?.updatedFeed?.iterator()){
            if (f != null){
                f!!.item?.forEach {
                   if (it != null)
                       vals.add(it)
                }
            }
        }

        var list = context.findView<ListView>(android.R.id.list)
        var adapter = ArrayAdapter<FeedItem>(context, android.R.layout.simple_list_item_1, android.R.id.text1, vals.build())
        list.setAdapter(adapter)
    }

}