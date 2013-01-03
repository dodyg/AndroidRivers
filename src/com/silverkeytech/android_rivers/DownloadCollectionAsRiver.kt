package com.silverkeytech.android_rivers


import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.util.Log
import com.github.kevinsawicki.http.HttpRequest
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import com.google.gson.Gson
import com.silverkeytech.android_rivers.riverjs.RiverItemMeta
import com.silverkeytech.android_rivers.riverjs.RiverSite
import com.silverkeytech.android_rivers.riverjs.River
import java.util.ArrayList
import com.silverkeytech.android_rivers.outliner.transformXmlToRss
import com.silverkeytech.android_rivers.syndication.SyndicationFeed
import com.silverkeytech.android_rivers.outliner.transformXmlToAtom
import com.silverkeytech.android_rivers.riverjs.RiverItem


public class DownloadCollectionAsRiver(it: Context?, private val collectionId : Int): AsyncTask<String, Int, Result<List<RiverItemMeta>>>(){
    class object {
        public val TAG: String = javaClass<DownloadCollectionAsRiver>().getSimpleName()
    }

    var dialog: ProgressDialog = ProgressDialog(it)
    var context: Activity = it!! as Activity

    protected override fun onPreExecute() {
        dialog.setMessage(context.getString(R.string.please_wait_while_loading))
        dialog.setIndeterminate(true)
        dialog.setCancelable(false)
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(android.R.string.cancel), object : DialogInterface.OnClickListener{
            public override fun onClick(p0: DialogInterface?, p1: Int) {
                p0!!.dismiss()
                this@DownloadCollectionAsRiver.cancel(true)
            }
        })

        dialog.show()
    }

    protected override fun doInBackground(vararg p0: String?): Result<List<RiverItemMeta>>? {
        var list = arrayListOf<RiverItemMeta>()
        for(val url in p0){
            val res = downloadFeed(url!!)

            if (res.isFalse())
                Log.d(TAG, "Value at ${res.exception?.getMessage()}")
            else
            {
                Log.d(TAG, "Download for $url is successful")
                accumulateList(list, res.value!!)
            }
        }

        return Result.right(list)
    }

    fun accumulateList(list : ArrayList<RiverItemMeta>, feed : SyndicationFeed){
        for(val f in feed.items.iterator()){
            val item = RiverItem()

            item.title = f.title
            item.body = f.description
            if (f.hasLink())
                item.link = f.link

            val link = if (feed.hasLink())
                    feed.link else ""

            val meta = RiverItemMeta(item, feed.title!!, link)

            list.add(meta)
        }
    }


    fun downloadFeed(url : String) : Result<SyndicationFeed>{
        try{
            var downloadedContent: String?
            var mimeType: String?
            try{
                val request = HttpRequest.get(url)
                mimeType = request?.contentType()

                downloadedContent = request?.body()
            }
            catch(e: HttpRequestException){
                var ex = e.getCause()
                return Result.wrong(ex)
            }

            Log.d(TAG, "Syndication XML is downloaded at ${url}")

            fun isAtomFeed() : Boolean {
                return mimeType!!.contains("atom") || downloadedContent!!.contains("<feed xmlns=\"http://www.w3.org/2005/Atom\">")
            }

            if (isAtomFeed()){
                var feed = transformXmlToAtom(downloadedContent)

                Log.d(TAG, "Transforming XML to ATOM")

                if (feed.isTrue()){
                    var f = SyndicationFeed(null, feed.value)
                    f.transformAtom()
                    return Result.right(f)
                } else{
                    return Result.wrong(feed.exception)
                }
            } else {
                var feed = transformXmlToRss(downloadedContent)

                Log.d(TAG, "Transforming XML to RSS")

                if (feed.isTrue()){
                    var f = SyndicationFeed(feed.value, null)
                    f.transformRss()
                    return Result.right(f)
                } else{
                    return Result.wrong(feed.exception)
                }
            }
        }
        catch (e : Exception){
            Log.d(TAG, "Problem when processing the feed ${e.getMessage()}")
            return Result.wrong(e)
        }
    }

    var callback : ((String, Result<List<RiverItemMeta>>) -> Unit)? = null

    //Set up function to call when download is done
    public fun executeOnCompletion(action: ((String, Result<List<RiverItemMeta>>) -> Unit)?) : DownloadCollectionAsRiver {
        callback = action
        return this
    }

    protected override fun onPostExecute(result: Result<List<RiverItemMeta>>?) {
        dialog.dismiss()
        val url = "http://www.localhost/" + collectionId.toString()
        if (result !=null){
            if (result.isTrue()){
                val sortedNewsItems = result.value!!
                context.getApplication().getMain().setRiverCache(url, sortedNewsItems, 3.toHoursInMinutes())
                if (callback != null)
                    callback!!(url, Result.right(sortedNewsItems))
            }
            else
                {
                    if (callback != null)
                        callback!!(url, Result.wrong<List<RiverItemMeta>>(result.exception))
                }
        }
    }
}