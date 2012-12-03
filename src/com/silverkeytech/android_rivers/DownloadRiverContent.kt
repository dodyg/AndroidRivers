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
import android.os.Messenger
import android.os.Handler
import android.os.Message
import android.app.Notification
import android.content.ContentProviderOperation.Builder
import android.app.NotificationManager
import android.support.v4.app.NotificationCompat
import android.util.TypedValue

//Responsible for handling a river js downloading and display in asynchronous way
public class DownloadRiverContent(it : Context?) : AsyncTask<String, Int, Result<FeedsRiver>>(){
    class object {
        public val TAG: String = javaClass<DownloadRiverContent>().getSimpleName()!!
    }

    var dialog : ProgressDialog = ProgressDialog(it)
    var context : Activity = it!! as Activity

    val STANDARD_NEWS_COLOR  = android.graphics.Color.GRAY
    val STANDARD_NEWS_IMAGE = android.graphics.Color.CYAN
    val STANDARD_NEWS_PODCAST = android.graphics.Color.MAGENTA

    protected override fun onPreExecute() {
        dialog.setMessage(context.getString(R.string.please_wait_while_loading))
        dialog.setIndeterminate(true)
        dialog.setCancelable(false)
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", object : DialogInterface.OnClickListener{

            public override fun onClick(p0: DialogInterface?, p1: Int) {
                p0!!.dismiss()
                this@DownloadRiverContent.cancel(true)
            }
        })

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

    public data class ViewHolder (var news : TextView, val indicator : TextView)

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

        var sortedNewsItems = newsItems.filter { x -> x.isPublicationDate()!! }.sortBy { x -> x.getPublicationDate()!! }.reverse()

        var list = context.findView<ListView>(android.R.id.list)
        var adapter = object : ArrayAdapter<FeedItem>(context, R.layout.news_item , sortedNewsItems) {

            public override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                var vw  = convertView
                var holder : ViewHolder?

                var currentNewsItem = sortedNewsItems[position]
                var news = currentNewsItem.toString()?.trim()

                if (news == null)
                    news = ""

                fun showIndicator(){
                    if (currentNewsItem.containsEnclosure()!!){
                        var enclosure = currentNewsItem.enclosure!!.get(0)!!
                        if (isSupportedImageMime(enclosure.`type`!!)){
                            holder!!.indicator.setBackgroundColor(STANDARD_NEWS_IMAGE)
                        }
                        else{
                            holder!!.indicator.setBackgroundColor(STANDARD_NEWS_PODCAST)
                        }
                    }else{
                        holder!!.indicator.setBackgroundColor(STANDARD_NEWS_COLOR)

                    }
                }

                if (vw == null){
                    var inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    vw = inflater.inflate(R.layout.news_item, parent, false)

                    holder = ViewHolder(vw!!.findViewById(R.id.news_item_text_tv) as TextView,
                            vw!!.findViewById(R.id.news_item_indicator_tv) as TextView)

                    holder!!.news.setText(news)
                    holder!!.news.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24.toFloat())
                    showIndicator()

                    vw!!.setTag(holder)
                }else{
                    holder = vw!!.getTag() as ViewHolder
                    holder!!.news.setText(news)
                    holder!!.indicator.setBackgroundColor(android.graphics.Color.RED)
                    showIndicator()
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

                fun linkAvailable() : Boolean = !currentNews.link.isNullOrEmpty() && currentNews.link!!.indexOf("http") > -1

                //check for go link
                if (linkAvailable()){
                    Log.d(TAG, "There's a link ${currentNews.link}")
                    dialog.setNeutralButton("Go", object : DialogInterface.OnClickListener{

                        public override fun onClick(p0: DialogInterface?, p1: Int) {
                            var i = Intent("android.intent.action.VIEW", Uri.parse(currentNews.link))
                            context.startActivity(i)

                            p0?.dismiss()
                        }
                    })
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
                        //assume podcast
                    else {
                        dialog.setNegativeButton("Podcast", object : DialogInterface.OnClickListener{
                            public override fun onClick(p0: DialogInterface?, p1: Int) {
                                var messenger = Messenger(object : Handler(){
                                    public override fun handleMessage(msg: Message?) {
                                        var path = msg!!.obj as String

                                        if (msg.arg1 == Activity.RESULT_OK && !path.isNullOrEmpty()){
                                            context.toastee("File is successfully downloaded at $path", Duration.LONG)
                                            MediaScannerWrapper.scanPodcasts(context, path)
                                        }else{
                                            context.toastee("Download failed", Duration.LONG)
                                        }
                                    }
                                })

                                var intent = Intent(context, javaClass<DownloadService>())
                                intent.putExtra(DownloadService.PARAM_DOWNLOAD_URL, enclosure.url)
                                intent.putExtra(DownloadService.PARAM_DOWNLOAD_TITLE, currentNews.title)
                                intent.putExtra(Params.MESSENGER, messenger)
                                context.startService(intent)

                            }
                        })

                    }
                }else{
                    if (linkAvailable()) {
                        dialog.setNegativeButton("Share", object : DialogInterface.OnClickListener{
                            public override fun onClick(p0: DialogInterface?, p1: Int) {
                                var intent = Intent(Intent.ACTION_SEND)
                                intent.setType("text/plain")
                                intent.putExtra(Intent.EXTRA_TEXT, currentNews.link!!)
                                context.startActivity(Intent.createChooser(intent, "Share page"))
                            }
                        })
                    }
                }

                dialog.create()!!.show()

            }
        })

        list.setAdapter(adapter)
    }

}