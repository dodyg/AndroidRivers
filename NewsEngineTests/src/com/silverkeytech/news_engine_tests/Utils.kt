
package com.silverkeytech.news_engine_tests

import com.silverkeytech.news_engine.syndications.SyndicationFilter
import com.silverkeytech.news_engine.syndications.SyndicationFeed
import com.silverkeytech.news_engine_tests.HttpRequest.HttpRequestException
import com.silverkeytech.news_engine.syndications.atom.Feed
import com.silverkeytech.news_engine.Result
import com.silverkeytech.news_engine.transformXmlToRdfRss
import com.silverkeytech.news_engine.transformXmlToRss
import com.silverkeytech.news_engine.transformXmlToAtom

fun plog(msg : String){
    System.out.println(msg)
}

fun downloadRawFeed(url : String) : String{
    val request = HttpRequest
            .get(url)!!
            .acceptGzipEncoding()!!
            .uncompress(true)!!

    return request.body()!!
}


fun downloadAtomFeed(url: String): Result<Feed>{
    com.silverkeytech.news_engine.log = { tag, str -> plog(str) }
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

        if (downloadedContent!!.isEmpty())
            throw Exception("Content is empty")

        val feed = transformXmlToAtom(downloadedContent)
        if (feed.isTrue())
            return Result.right(feed.value!!)
        else
            throw feed.exception!!
    }
    catch (e : Exception){
        return Result.wrong(e)
    }
}

fun downloadSingleFeed(url: String, filter: SyndicationFilter? = null): Result<SyndicationFeed> {
    com.silverkeytech.news_engine.log = { tag, str -> plog(str) }
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

        fun isRdfFeed(): Boolean{
            return downloadedContent!!.contains("<rdf:RDF")
        }

        if (isAtomFeed()){
            var feed = transformXmlToAtom(downloadedContent)
            if (feed.isTrue()){
                var f = SyndicationFeed(null, feed.value, null, filter)
                f.transformAtom()
                return Result.right(f)
            } else{
                return Result.wrong(feed.exception)
            }
        }
        else if (isRdfFeed()){
            val feed = transformXmlToRdfRss(downloadedContent)
            if (feed.isTrue()){
                var f = SyndicationFeed(null, null, feed.value, filter)
                f.transformRdf()
                return Result.right(f)
            }
            else
                return Result.wrong(feed.exception)
        }
        else {
            var feed = transformXmlToRss(downloadedContent)

            if (feed.isTrue()){
                var f = SyndicationFeed(feed.value, null, null, filter)
                f.transformRss()
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
