
package com.silverkeytech.news_engine_tests

import com.silverkeytech.news_engine.syndications.SyndicationFilter
import com.silverkeytech.news_engine.syndications.SyndicationFeed
import com.silverkeytech.news_engine_tests.HttpRequest.HttpRequestException
import com.silverkeytech.news_engine.syndications.rss.Rss
import com.silverkeytech.news_engine.syndications.rss.RssBuilder
import java.io.ByteArrayInputStream
import com.silverkeytech.news_engine.syndications.rss.RssParser
import com.silverkeytech.news_engine.syndications.atom.Feed

fun plog(msg : String){
    System.out.println(msg)
}

fun downloadSingleFeed(url: String, filter: SyndicationFilter? = null): Result<SyndicationFeed> {
    val TAG = "downloadFeed"
    try{
        var downloadedContent: String?
        var mimeType: String?
        try{
            val request = HttpRequest
                    .get(url)!!
                    .acceptGzipEncoding()!!
                    .uncompress(true)!!
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
            var feed = transformXmlToAtom(downloadedContent)
            if (feed.isTrue()){
                var f = SyndicationFeed(null, feed.value, filter)
                f.transformAtom()
                return Result.right(f)
            } else{
                return Result.wrong(feed.exception)
            }
        } else {
            var feed = transformXmlToRss(downloadedContent)

            if (feed.isTrue()){
                var f = SyndicationFeed(feed.value, null, filter)
                f.transformRss()
                val afterTransform = System.currentTimeMillis()
                return Result.right(f)
            } else{
                return Result.wrong(feed.exception)
            }
        }
    }
    catch (e: Exception){
        return Result.wrong(e)
    }
}

fun transformXmlToRss(xml: String?): Result<Rss> {

    try{
        val builder = RssBuilder()
        val reader = ByteArrayInputStream(xml!!.getBytes())
        RssParser().parse(reader, builder)
        val rss = builder.build()
        reader.close()
        return Result.right(rss)
    }
    catch (e: Exception){
        return Result.wrong(e)
    }
}

fun transformXmlToAtom(xml: String?): Result<Feed> {

    try{
        val feed: Feed? = XmlComponent.serial.read(javaClass<Feed>(), xml, false)
        return Result.right(feed)
    }
    catch (e: Exception){
        return Result.wrong(e)
    }
}