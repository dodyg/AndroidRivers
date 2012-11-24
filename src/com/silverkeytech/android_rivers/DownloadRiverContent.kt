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
import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import java.util.ArrayList
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import org.apache.http.conn.ConnectTimeoutException
import java.net.UnknownHostException

//Responsible for handling a river js downloading and display in asynchronous way
public class DownloadRiverContent(it : Context?) : AsyncTask<String, Int, Result<FeedsRiver>>(){
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

    protected override fun doInBackground(vararg p0: String?): Result<FeedsRiver>? {
        var req :String?
        try{
            req = HttpRequest.get(p0[0])?.body()
        }
        catch(e : HttpRequestException){
            var ex = e.getCause()
            return Result.wrong(ex)
        }

        var gson = Gson()
        try{
            var scrubbed = scrubJsonP(req!!)

            var feeds = gson.fromJson(scrubbed, javaClass<FeedsRiver>())!!

            this.publishProgress(100);

            return Result.right(feeds)
        }
        catch(e : Exception)
        {
            return Result.wrong(e)
        }
    }

    protected override fun onPostExecute(result: Result<FeedsRiver>?) {
        dialog.dismiss();
        if (result == null)
            context.toastee("Sorry, we cannot process this feed because the operation is cancelled", Duration.AVERAGE)
        else
            {
                if (result.isFalse()){
                    if (result.exception is ConnectTimeoutException)
                        context.toastee("Sorry, we cannot download this feed. The feed site might be down", Duration.AVERAGE)
                    else if (result.exception is UnknownHostException)
                        context.toastee("Sorry, we cannot download this feed. Please check your Internet connection, it might be down", Duration.AVERAGE)
                    else
                        context.toastee("Sorry, we cannot download this feed for the following technical reason : ${result.exception.toString()}", Duration.AVERAGE)
                }else{
                    handleNewsListing(result.value!!)
                }
            }
    }

    fun handleNewsListing(river : FeedsRiver){
        var newsItems = ArrayList<FeedItem>()

        for(var f : FeedSite? in river.updatedFeeds?.updatedFeed?.iterator()){
            if (f != null){
                f!!.item?.forEach {
                   if (it != null)
                       newsItems.add(it)
                }
            }
        }

        //now sort it so people always have the latest news first

        var sortedNewsItems = newsItems.filter { x -> x.isPublicationDate()!! }.sort(comparator { (p1, p2) ->
            var date1 = p1.getPublicationDate()
            var date2 = p2.getPublicationDate()
            //reverse. The latest date comes first
            var result : Int
            if (date1 != null && date2 != null){
                if (date1!! > date2)
                    result = -1
                else if(date1 == date2)
                    result = 0
                else
                    result = 1
            }   else
                {
                    result = -1
                }

            result
        })

        var list = context.findView<ListView>(android.R.id.list)
        var adapter = ArrayAdapter<FeedItem>(context, android.R.layout.simple_list_item_1, sortedNewsItems)

        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long) {
                //(p1 as TextView).setTextColor(android.graphics.Color.rgb(204,255,153))

                var currentNews = sortedNewsItems.get(p2);

                var dialog = AlertDialog.Builder(context)

                dialog.setMessage(scrubHtml(currentNews.body!!))


                dialog.setPositiveButton(android.R.string.ok, object : DialogInterface.OnClickListener{
                    public override fun onClick(p0: DialogInterface?, p1: Int) {
                        p0?.dismiss()
                    }
                })

                if (currentNews.link != "" && currentNews.link?.indexOf("http") !!> -1){
                    Log.d(TAG, "There's a link ${currentNews.link}")
                    dialog.setNeutralButton("Go", object : DialogInterface.OnClickListener{

                        public override fun onClick(p0: DialogInterface?, p1: Int) {
                            var i = Intent("android.intent.action.VIEW", Uri.parse(currentNews.link))
                            context.startActivity(i)

                            p0?.dismiss()
                        }
                    })
                }else{
                    Log.d(TAG, "There is no link for ${currentNews.title}")
                }

                dialog.create()!!.show()

            }
        })

        list.setAdapter(adapter)
    }

}