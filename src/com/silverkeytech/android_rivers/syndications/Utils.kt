package com.silverkeytech.android_rivers.syndications

import com.silverkeytech.android_rivers.outliner.transformXmlToAtom
import com.silverkeytech.android_rivers.Result
import com.silverkeytech.android_rivers.syndication.SyndicationFeed
import com.github.kevinsawicki.http.HttpRequest
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import android.util.Log
import com.silverkeytech.android_rivers.outliner.transformXmlToRss


fun downloadSingleFeed(url : String) : Result<SyndicationFeed>{
    val TAG  = "downloadFeed"
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