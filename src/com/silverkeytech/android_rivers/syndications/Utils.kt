package com.silverkeytech.android_rivers.syndications

import android.util.Log
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import com.silverkeytech.android_rivers.Result
import com.silverkeytech.android_rivers.httpGet
import com.silverkeytech.android_rivers.outliner.transformXmlToAtom
import com.silverkeytech.android_rivers.outliner.transformXmlToRss
import com.silverkeytech.news_engine.syndications.SyndicationFilter
import com.silverkeytech.news_engine.syndications.SyndicationFeed
import com.silverkeytech.android_rivers.outliner.transformXmlToRdfRss

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

        fun isRdfFeed(): Boolean {
            return downloadedContent!!.contains("<rdf:RDF")
        }

        if (isAtomFeed()){
            val before = System.currentTimeMillis()
            var feed = transformXmlToAtom(downloadedContent)
            val after = System.currentTimeMillis()
            Log.d(TAG, "Time to parse XML to ATOM  is ${after - before} with feed ${feed.value?.entry?.size()} items")
            Log.d(TAG, "$url is an ATOM Feed")
            if (feed.isTrue()){
                val beforeTransform = System.currentTimeMillis()
                var f = SyndicationFeed(null, feed.value, null, filter)
                f.transformAtom()
                val afterTransform = System.currentTimeMillis()
                Log.d(TAG, "Time to transform ATOM Raw is ${afterTransform - beforeTransform}")
                Log.d(TAG, "ATOM parsing with ${f.items.size()} items at url ${f.link}")
                return Result.right(f)
            } else{
                return Result.wrong(feed.exception)
            }
        }
        else if (isRdfFeed()){
            val feed = transformXmlToRdfRss(downloadedContent)
            Log.d(TAG, "$url Feed is a RDF RSS")
            if (feed.isTrue()){
                var f = SyndicationFeed(null, null, feed.value, filter)
                f.transformRdf()
                Log.d(TAG, "RDF transformation")
                return Result.right(f)
            }
            else
                return Result.wrong(feed.exception)
        }
        else {
            val before = System.currentTimeMillis()
            var feed = transformXmlToRss(downloadedContent)
            val after = System.currentTimeMillis()
            Log.d(TAG, "Time to parse XML to RSS  is ${after - before} with ${feed.value?.channel?.item?.size()} items")
            Log.d(TAG, "$url is a RSS Feed because isRdfFeed is ${isRdfFeed()}")

            if (feed.isTrue()){
                val beforeTransform = System.currentTimeMillis()
                var f = SyndicationFeed(feed.value, null, null, filter)
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
