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

package com.silverkeytech.android_rivers.outliner

import android.util.Log
import com.silverkeytech.android_rivers.Result
import com.silverkeytech.android_rivers.XmlComponent
import com.silverkeytech.android_rivers.isNullOrEmpty
import com.silverkeytech.news_engine.outlines.Body
import com.silverkeytech.news_engine.outlines.Head
import com.silverkeytech.news_engine.outlines.Opml
import com.silverkeytech.news_engine.outlines.Outline
import com.silverkeytech.news_engine.riverjs.RiverOpml
import com.silverkeytech.news_engine.riverjs.RiverOpmlOutline
import com.silverkeytech.android_rivers.scrubHtml
import com.silverkeytech.news_engine.syndications.atom.Feed
import com.silverkeytech.news_engine.syndications.rss.Rss
import com.silverkeytech.news_engine.syndications.rss.RssBuilder
import com.silverkeytech.news_engine.syndications.rss.RssParser
import go.goyalla.dict.arabicDictionary.file.ArabicReshape
import java.io.ByteArrayInputStream
import java.util.ArrayList
import com.silverkeytech.news_engine.outliner.OutlineContent
import com.silverkeytech.news_engine.syndications.rss_rdf.RdfRssBuilder
import com.silverkeytech.news_engine.syndications.rss_rdf.RdfRssParser
import com.silverkeytech.news_engine.syndications.rss_rdf.Rdf
import com.silverkeytech.news_engine.outlines.OpmlBuilder
import com.silverkeytech.news_engine.outlines.OpmlParser
import com.silverkeytech.android_rivers.isLanguageRTL

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


fun transformXmlToOpml(xml: String?): Result<Opml>{
    try{
        val builder = OpmlBuilder()
        val reader = ByteArrayInputStream(xml!!.getBytes())
        OpmlParser().parse(reader, builder)
        val opml = builder.build()
        reader.close()
        return Result.right(opml)
    }
    catch (e: Exception){
        return Result.wrong(e)
    }
}


fun transformFeedOpmlToOpml(feedOpml: RiverOpml): Result<Opml> {
    fun traverseFeedOpml(outline: Outline, feedOutline: RiverOpmlOutline) {
        outline.text = feedOutline.text
        outline.url = feedOutline.url
        outline.xmlUrl = feedOutline.xmlUrl
        outline.htmlUrl = feedOutline.htmlUrl
        outline.language = feedOutline.language
        outline.outlineType = feedOutline.outlineType

        for(fo in feedOutline.outline!!.iterator()){
            var outl = Outline()
            traverseFeedOpml(outl, fo)
            outline.outline!!.add(outl)
        }
    }

    try
    {
        var opml = Opml()

        if (feedOpml.head != null){
            var head = Head()
            head.title = feedOpml.head!!.title
            head.ownerName = feedOpml.head!!.ownerName
            head.dateCreated = feedOpml.head!!.dateCreated
            head.dateModified = feedOpml.head!!.dateModified
            opml.head = head
        }

        if (feedOpml.body != null){
            var body = Body()
            for(fo in feedOpml.body!!.outline!!.iterator()){
                var outline = Outline()
                traverseFeedOpml(outline, fo)
                body.outline!!.add(outline)
            }

            opml.body = body
        }

        return Result.right(opml)
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
        Log.d("Rss Transform", "Exception ${e.getMessage()}")
        return Result.wrong(e)
    }
}

fun transformXmlToAtom(xml: String?): Result<Feed> {

    try{
        val feed: Feed? = XmlComponent.serial.read(javaClass<Feed>(), xml, false)
        return Result.right(feed)
    }
    catch (e: Exception){
        Log.d("Atom Transform", "Exception ${e.getMessage()}")
        return Result.wrong(e)
    }
}

fun transformXmlToRdfRss(xml: String?): Result<Rdf> {
    try{
        val builder = RdfRssBuilder()
        val reader = ByteArrayInputStream(xml!!.getBytes())
        RdfRssParser().parse(reader, builder)
        val rss = builder.build()
        reader.close()
        return Result.right(rss)
    }
    catch (e: Exception){
        return Result.wrong(e)
    }
}