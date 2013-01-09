package com.silverkeytech.android_rivers.syndications

import android.util.Log
import com.github.kevinsawicki.http.HttpRequest
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import com.silverkeytech.android_rivers.Result
import com.silverkeytech.android_rivers.outliner.transformXmlToAtom
import com.silverkeytech.android_rivers.outliner.transformXmlToRss
import com.silverkeytech.android_rivers.syndications.SyndicationFeed
import com.silverkeytech.android_rivers.syndications.atom.Feed
import com.silverkeytech.android_rivers.syndications.rss.Rss
import com.silverkeytech.android_rivers.httpGet

fun downloadSingleFeed(url: String, filter : SyndicationFilter? = null): Result<SyndicationFeed> {
    val TAG = "downloadFeed"
    try{
        var downloadedContent: String?
        var mimeType: String?
        try{
            val request = httpGet(url)
            mimeType = request?.contentType()

            downloadedContent = request?.body()
        }
        catch(e: HttpRequestException){
            var ex = e.getCause()
            return Result.wrong(ex)
        }

        Log.d(TAG, "Syndication XML is downloaded at ${url}")

        fun isAtomFeed(): Boolean {
            return mimeType!!.contains("atom") || downloadedContent!!.contains("<feed xmlns=\"http://www.w3.org/2005/Atom\">")
        }

        if (isAtomFeed()){
            var feed = transformXmlToAtom(downloadedContent)

            Log.d(TAG, "Transforming XML to ATOM")

            if (feed.isTrue()){
                var f = SyndicationFeed(null, feed.value, filter)
                f.transformAtom()
                return Result.right(f)
            } else{
                return Result.wrong(feed.exception)
            }
        } else {
            var feed = transformXmlToRss(downloadedContent)

            Log.d(TAG, "Transforming XML to RSS")

            if (feed.isTrue()){
                var f = SyndicationFeed(feed.value, null, filter)
                f.transformRss()
                return Result.right(f)
            } else{
                return Result.wrong(feed.exception)
            }
        }
    }
    catch (e: Exception){
        Log.d(TAG, "Problem when processing the feed ${e.getMessage()}")
        return Result.wrong(e)
    }
}

//verify that this atom feed date are parseable. This is necessary for merging syndication date
public fun verifyAtomFeedForDateFitness(f : Feed) : Boolean{
    try
    {
        if (f.getUpdated() == null)
            return false

        if (f.entry == null || f.entry!!.size() == 0)
            return false

        val e = f.entry!!.get(0)

        if (e.getUpdated() == null)
            return false

        return true
    }
    catch (e : Exception){
        return false
    }
}

//verify that this rss feed ate are parseable. Thsi is necessary for merging syndication date
public fun verifyRssFeedForDateFitness(r : Rss) : Boolean {
    try
    {
        if (r.channel?.item == null || r.channel!!.item!!.size() == 0)
            return false

        val i = r.channel!!.item!!.get(0)

        if (i.getPubDate() == null)
            return false

        return true
    }
    catch (e : Exception){
        return false
    }
}