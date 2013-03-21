package com.silverkeytech.android_rivers.syndications

import android.util.Log
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import com.silverkeytech.android_rivers.Result
import com.silverkeytech.android_rivers.httpGet
import com.silverkeytech.android_rivers.outliner.transformXmlToAtom
import com.silverkeytech.android_rivers.outliner.transformXmlToRss
import com.silverkeytech.android_rivers.syndications.atom.Feed
import com.silverkeytech.android_rivers.syndications.rss.Rss
import java.util.Date
import com.github.kevinsawicki.http.HttpRequest


fun downloadSingleFeed(url: String, filter: SyndicationFilter? = null): Result<SyndicationFeed> {
    val TAG = "downloadFeed"
    try{
        var downloadedContent: String?
        var mimeType: String?
        try{
            val request = httpGet(url)
            mimeType = request.contentType()
            downloadedContent = request.body()!!
        }
        catch(e: HttpRequestException){
            var ex = e.getCause()
            return Result.wrong(ex)
        }

        fun isAtomFeed(): Boolean {
            return mimeType!!.contains("atom") || downloadedContent!!.contains("<feed ")//the space is important so it doesn't confuse with feedburner tag
        }

        if (isAtomFeed()){
            val before = System.currentTimeMillis()
            var feed = transformXmlToAtom(downloadedContent)
            val after = System.currentTimeMillis()
            Log.d(TAG, "Time to parse XML to ATOM  is ${after - before} with feed ${feed.value?.entry?.size()} items")
            Log.d(TAG, "$url is an ATOM Feed")
            if (feed.isTrue()){
                val beforeTransform = System.currentTimeMillis()
                var f = SyndicationFeed(null, feed.value, filter)
                f.transformAtom()
                val afterTransform = System.currentTimeMillis()
                Log.d(TAG, "Time to transform ATOM Raw is ${afterTransform - beforeTransform}")
                Log.d(TAG, "ATOM parsing with ${f.items.size()} items at url ${f.link}")
                return Result.right(f)
            } else{
                return Result.wrong(feed.exception)
            }
        } else {
            val before = System.currentTimeMillis()
            var feed = transformXmlToRss(downloadedContent)
            val after = System.currentTimeMillis()
            Log.d(TAG, "Time to parse XML to RSS  is ${after - before} with ${feed.value?.channel?.item?.size()} items")
            Log.d(TAG, "$url is a RSS Feed")

            if (feed.isTrue()){
                val beforeTransform = System.currentTimeMillis()
                var f = SyndicationFeed(feed.value, null, filter)
                f.transformRss()
                val afterTransform = System.currentTimeMillis()
                Log.d(TAG, "Time to transform RSS Raw is ${afterTransform - beforeTransform}")
                Log.d(TAG, "RSS parsing with ${f.items.size()} items at url ${f.link}")
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
public fun verifyAtomFeedForDateFitness(f: Feed): Boolean {
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
    catch (e: Exception){
        return false
    }
}

public enum class ParsedDateFormat{
    RFC822
    UNKNOWN
    MISSING
    ISO8601_NOMS
    NO_SPACES
}

public data class RssDate(public val status : ParsedDateFormat, public val date : Date?){
    public val isValid : Boolean
        get() = status != ParsedDateFormat.UNKNOWN && status != ParsedDateFormat.MISSING
}


//verify that this rss feed ate are parseable. Thsi is necessary for merging syndication date
public fun verifyRssFeedForDateFitness(r: Rss): Pair<Boolean, ParsedDateFormat?> {
    try
    {
        if (r.channel?.item == null || r.channel!!.item!!.size() == 0)
            return Pair(false, null)

        val i = r.channel!!.item!!.get(0)

        val pubDate = i.getPubDate()!!

        if (pubDate.isValid)
            return Pair(true, pubDate.status)
        else
            return Pair(false, null)
    }
    catch (e: Exception){
        return Pair(false, null)
    }
}