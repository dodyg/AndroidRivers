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


public class DownloadCollectionAsRiver(it: Context?): AsyncTask<String, Int, Result<River>>(){
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

    protected override fun doInBackground(vararg p0: String?): Result<River>? {
        for(val url in p0){
            val res = downloadFeed(url!!)

            if (res.isFalse())
                Log.d(TAG, "Value at ${res.exception?.getMessage()}")
            else
                Log.d(TAG, "Download for $url is successful")
        }

        return null
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

    protected override fun onPostExecute(result: Result<River>?) {
        dialog.dismiss()
    }
}