/*
Android Rivers is an app to read and discover news using RiverJs, RSS and OPML format.
Copyright (C) 2012 Dody Gunawinata (dodyg@silverkeytech.com)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package com.silverkeytech.android_rivers

import android.util.Log
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import com.silverkeytech.news_engine.transformXmlToRss
import com.silverkeytech.news_engine.syndications.SyndicationFilter
import com.silverkeytech.news_engine.syndications.SyndicationFeed
import com.silverkeytech.news_engine.transformXmlToRdfRss
import com.google.gson.Gson
import com.silverkeytech.news_engine.outlines.Opml
import com.silverkeytech.news_engine.outlines.Outline
import com.silverkeytech.news_engine.riverjs.River
import go.goyalla.dict.arabicDictionary.file.ArabicReshape
import java.util.ArrayList
import com.silverkeytech.news_engine.outliner.OutlineContent
import com.silverkeytech.news_engine.transformXmlToAtom

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


fun downloadSingleRiver(url: String): Result<River> {
    var req: String?
    try{
        req = httpGet(url).body()
    }
    catch(e: HttpRequestException){
        val ex = e.getCause()
        return Result.wrong(ex)
    }

    try{
        val scrubbed = scrubJsonP(req!!)
        val feeds = Gson().fromJson(scrubbed, javaClass<River>())!!

        return Result.right(feeds)
    }
    catch(e: Exception)
    {
        Log.d("downloadSingleRiver", "${e.getMessage()} ${e.getCause()?.getMessage()}")
        return Result.wrong(e)
    }
}


//do an in order traversal so we can flatten it up to be used by outliner
fun Opml.traverse (filter: ((Outline) -> Boolean)? = null, depthLimit: Int = 12): ArrayList<OutlineContent> {
    var list = ArrayList<OutlineContent>()

    var level = 0
    for (o in this.body?.outline?.iterator())    {
        traverseOutline(level, o, list, filter, depthLimit)
    }
    return list
}

private fun traverseOutline(level: Int, outline: Outline?, list: ArrayList<OutlineContent>, filter: ((Outline) -> Boolean)?, depthLimit: Int) {
    if (outline != null){
        val proceed = level < depthLimit && (filter == null || filter(outline))

        if (proceed){

            var o = OutlineContent(level, scrubHtml(outline.text!!))
            if (!outline.outlineType.isNullOrEmpty() && !outline.url.isNullOrEmpty()){
                o.putAttribute("type", outline.outlineType!!)
                o.putAttribute("url", outline.url!!)
            }
            else if (!outline.outlineType.isNullOrEmpty() && !outline.xmlUrl.isNullOrEmpty()){
                o.putAttribute("type", outline.outlineType!!)
                o.putAttribute("url", outline.xmlUrl!!)
            }

            if (!outline.language.isNullOrEmpty()){
                o.putAttribute("language", outline.language!!)

                if (isLanguageRTL(outline.language!!))                {
                    Log.d("traverseOutline", "Reshaping Arabic")
                    o.text = "\u200F" + ArabicReshape.reshape(o.text) //at RTL marker
                }

            } else {
                o.putAttribute("language", "en")
            }

            list.add(o)

            var lvl = level
            lvl++

            for(ox in outline.outline?.iterator()){
                traverseOutline(lvl, ox, list, filter, depthLimit)
            }
        }
    }
}

