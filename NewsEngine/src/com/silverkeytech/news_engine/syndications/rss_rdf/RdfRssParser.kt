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

package com.silverkeytech.news_engine.syndications.rss_rdf

import java.io.InputStream
import com.thebuzzmedia.sjxp.XMLParser
import com.silverkeytech.news_engine.xml.textRule
import com.silverkeytech.news_engine.xml.tagRule

public class RdfRssParser{
    public fun parse(input: InputStream, rss: RdfRssBuilder) {
        XMLParser.DEBUG = false
        var parser = XMLParser<RdfRssBuilder>(channelTitle, channelLink, channelDescription, channelDcLanguage,
                channelDcRights, channelDcPublisher, channelDcCreator, channelDcSource, channelDcTitle,
                itemTag, itemTitle, itemLink, itemDescription,
                itemDcDate, itemDcLanguage, itemDcRights, itemDcSource, itemDcTitle)
        parser.parse(input, "UTF-8", rss)
    }
}

val rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
val ns = "http://purl.org/rss/1.0/"
val dc = "http://purl.org/dc/elements/1.1/"

val channelTitle = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]channel/[$ns]title", { text, rss ->
    rss.channel.setTitle(text)
})

val channelLink = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]channel/[$ns]link", { text, rss ->
    rss.channel.setLink(text)
})

val channelDescription = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]channel/[$ns]description", { text, rss ->
    rss.channel.setDescription(text)
})

val channelDcLanguage = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]channel/[$dc]language", { text, rss ->
    rss.channel.setDcLanguage(text)
})

val channelDcRights = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]channel/[$dc]rights", { text, rss ->
    rss.channel.setDcRights(text)
})

val channelDcPublisher = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]channel/[$dc]publisher", { text, rss ->
    rss.channel.setDcPublisher(text)
})

val channelDcCreator = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]channel/[$dc]creator", { text, rss ->
    rss.channel.setDcCreator(text)
})

val channelDcSource = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]channel/[$dc]source", { text, rss ->
    rss.channel.setDcSource(text)
})

val channelDcTitle = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]channel/[$dc]title", { text, rss ->
    rss.channel.setDcTitle(text)
})

val itemTag = tagRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]item", { isStartTag, rss ->
    if (isStartTag)
        rss.channel.startItem()
    else
        rss.channel.endItem()
})

val itemTitle = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]item/[$ns]title", { text, rss ->
    rss.channel.item.setTitle(text)
})

val itemLink = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]item/[$ns]link", { text, rss ->
    rss.channel.item.setLink(text)
})

val itemDescription = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]item/[$ns]description", { text, rss ->
    rss.channel.item.setDescription(text)
})

val itemDcDate = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]item/[$dc]date", { text, rss ->
    rss.channel.item.setDcDate(text)
})

val itemDcLanguage = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]item/[$dc]language", { text, rss ->
    rss.channel.item.setDcLanguage(text)
})

val itemDcRights = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]item/[$dc]rights", { text, rss ->
    rss.channel.item.setDcRights(text)
})

val itemDcSource = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]item/[$dc]source", { text, rss ->
    rss.channel.item.setDcSource(text)
})

val itemDcTitle = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]item/[$dc]title", { text, rss ->
    rss.channel.item.setDcTitle(text)
})

