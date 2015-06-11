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

package com.silverkeytech.news_engine

import com.silverkeytech.news_engine.outlines.Opml
import com.silverkeytech.news_engine.outlines.OpmlBuilder
import java.io.ByteArrayInputStream
import com.silverkeytech.news_engine.outlines.OpmlParser
import com.silverkeytech.news_engine.syndications.rss_rdf.Rdf
import com.silverkeytech.news_engine.syndications.rss_rdf.RdfRssBuilder
import com.silverkeytech.news_engine.syndications.rss_rdf.RdfRssParser
import com.silverkeytech.news_engine.syndications.rss.Rss
import com.silverkeytech.news_engine.syndications.rss.RssBuilder
import com.silverkeytech.news_engine.syndications.rss.RssParser
import com.silverkeytech.news_engine.outlines.Outline
import com.silverkeytech.news_engine.riverjs.RiverOpml
import com.silverkeytech.news_engine.riverjs.RiverOpmlOutline
import com.silverkeytech.news_engine.outlines.Head
import com.silverkeytech.news_engine.outlines.Body
import com.silverkeytech.news_engine.syndications.atom.AtomParser
import com.silverkeytech.news_engine.syndications.atom.AtomBuilder
import com.silverkeytech.news_engine.syndications.atom.Feed

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

fun transformXmlToOpml(xml: String?): Result<Opml>{
    try{
        val builder = OpmlBuilder()
        val reader = ByteArrayInputStream(xml!!.toByteArray())
        OpmlParser().parse(reader, builder)
        val opml = builder.build()
        reader.close()
        return Result.right(opml)
    }
    catch (e: Exception){
        return Result.wrong(e)
    }
}

fun transformXmlToRdfRss(xml: String?): Result<Rdf> {

    try{
        val builder = RdfRssBuilder()
        val reader = ByteArrayInputStream(xml!!.toByteArray())
        RdfRssParser().parse(reader, builder)
        val rss = builder.build()
        reader.close()
        return Result.right(rss)
    }
    catch (e: Exception){
        return Result.wrong(e)
    }
}

fun transformXmlToRss(xml: String?): Result<Rss> {
    try{
        val builder = RssBuilder()
        val reader = ByteArrayInputStream(xml!!.toByteArray())
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
        val builder = AtomBuilder()
        val reader = ByteArrayInputStream(xml!!.toByteArray())
        AtomParser().parse(reader, builder)
        val atom = builder.build()
        reader.close()

        return Result.right(atom)
    }
    catch (e: Exception){
        return Result.wrong(e)
    }
}
