package com.silverkeytech.android_rivers

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.github.kevinsawicki.http.HttpRequest
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import com.google.gson.Gson
import com.silverkeytech.android_rivers.riverjs.FeedItem
import com.silverkeytech.android_rivers.riverjs.FeedSite
import com.silverkeytech.android_rivers.riverjs.FeedsRiver
import java.net.SocketException
import java.net.UnknownHostException
import java.util.ArrayList
import org.apache.http.conn.ConnectTimeoutException
import com.silverkeytech.android_rivers.riverjs.FeedEnclosure

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

        try{
            var gson = Gson()
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

                    var error = ConnectivityErrorMessage(
                            timeoutException = "Sorry, we cannot download this feed. The feed site might be down.",
                            socketException = "Sorry, we cannot download this feed. Please check your Internet connection, it might be down.",
                            otherException = "Sorry, we cannot download this feed for the following technical reason : ${result.exception.toString()}"
                    )

                    context.handleConnectivityError(result.exception, error)
                }else{
                    handleNewsListing(result.value!!)
                }
            }
    }

    public data class ViewHolder (var news : TextView)

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
        var adapter = object : ArrayAdapter<FeedItem>(context, android.R.layout.simple_list_item_1, sortedNewsItems) {

            public override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                var vw  = convertView
                var holder : ViewHolder?

                var news = sortedNewsItems[position].toString()?.trim()

                if (news == null)
                    news = ""

                if (vw == null){
                    var inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    vw = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)

                    holder = ViewHolder(vw!!.findViewById(android.R.id.text1) as TextView)
                    holder!!.news.setText(news)
                    vw!!.setTag(holder)
                }else{
                    holder = vw!!.getTag() as ViewHolder
                    holder!!.news.setText(news)
                    Log.d(TAG, "List View reused")
                }

                if (news.isNullOrEmpty()){
                    vw?.setVisibility(View.GONE)
                }   else{
                    vw?.setVisibility(View.VISIBLE)
                }

                return vw
            }
        }

        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long) {
                //(p1 as TextView).setTextColor(android.graphics.Color.rgb(204,255,153))

                var currentNews = sortedNewsItems.get(p2);

                var dialog = AlertDialog.Builder(context)

                if (currentNews.body.isNullOrEmpty() && !currentNews.title.isNullOrEmpty())
                    dialog.setMessage(scrubHtml(currentNews.title!!))
                else
                    dialog.setMessage(scrubHtml(currentNews.body!!))


                //Check dismiss button
                dialog.setPositiveButton(android.R.string.ok, object : DialogInterface.OnClickListener{
                    public override fun onClick(p0: DialogInterface?, p1: Int) {
                        p0?.dismiss()
                    }
                })

                //check for go link
                if (!currentNews.link.isNullOrEmpty() && currentNews.link!!.indexOf("http") > -1){
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


                //check for image enclsoure
                if (currentNews.containsEnclosure()!! ){
                    var enclosure = currentNews.enclosure!!.get(0)!!
                    if (isSupportedImageMime(enclosure.`type`!!)){
                        dialog.setNegativeButton("Image", object : DialogInterface.OnClickListener{
                            public override fun onClick(p0: DialogInterface?, p1: Int) {
                                Log.d(TAG, "I am downloading a ${enclosure.url} with type ${enclosure.`type`}")
                                DownloadImage(context).execute(enclosure.url)
                            }
                        })
                    }
                }

                /*
                            var service = DownloadService()

                            var intent = Intent(context, javaClass<DownloadService>())
                            intent.putExtra("downloadUrl", enclosure.url)
                            intent.putExtra("filename", "first.mp3")
                            intent.putExtra("contentLength", enclosure.length!!.toInt())

                            context.startService(intent)
                            */

                dialog.create()!!.show()

            }
        })

        list.setAdapter(adapter)
    }

}