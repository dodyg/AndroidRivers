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

package com.silverkeytech.news_engine.syndications.rss

import com.thebuzzmedia.sjxp.XMLParser
import java.io.InputStream
import com.silverkeytech.news_engine.xml.textRule
import com.silverkeytech.news_engine.xml.attributeRule
import com.silverkeytech.news_engine.xml.tagRule

public class RssParser{
    public fun parse(input: InputStream, rss: RssBuilder) {
        var parser = XMLParser<RssBuilder>(channelTitle, channelLink, channelDescription, channelLanguage, channelPubDate,
                channelLastBuildDate, channelDocs, channelGenerator, channelManagingEditor, channelWebMaster,
                channelTtl, channelCloud,
                itemTag, itemTitle, itemLink, itemDescription, itemAuthor, itemGuid, itemIsPermaLink, itemPubDate,
                itemEnclosure, itemMobileAppVersion
        )
        parser.parse(input, "UTF-8", rss)
    }
}


val channelTitle = textRule<RssBuilder>("/rss/channel/title", { text, rss ->
    rss.channel.setTitle(text)
})

val channelLink = textRule<RssBuilder>("/rss/channel/link", { text, rss ->
    rss.channel.setLink(text)
})

val channelDescription = textRule<RssBuilder>("/rss/channel/description", { text, rss ->
    rss.channel.setDescription(text)
})

val channelLanguage = textRule<RssBuilder>("/rss/channel/language", { text, rss ->
    rss.channel.setLanguage(text)
})

val channelPubDate = textRule<RssBuilder>("/rss/channel/pubDate", { text, rss ->
    rss.channel.setPubDate(text)
})

val channelLastBuildDate = textRule<RssBuilder>("/rss/channel/lastBuildDate", { text, rss ->
    rss.channel.setLastBuildDate(text)
})

val channelDocs = textRule<RssBuilder>("/rss/channel/docs", { text, rss ->
    rss.channel.setDocs(text)
})

val channelGenerator = textRule<RssBuilder>("/rss/channel/generator", { text, rss ->
    rss.channel.setGenerator(text)
})

val channelManagingEditor = textRule<RssBuilder>("/rss/channel/managingEditor", { text, rss ->
    rss.channel.setManagingDirector(text)
})

val channelWebMaster = textRule<RssBuilder>("/rss/channel/webMaster", { text, rss ->
    rss.channel.setWebMaster(text)
})

val channelTtl = textRule<RssBuilder>("/rss/channel/ttl", { text, rss ->
    rss.channel.setTitle(text.toInt())
})

val channelCloud = attributeRule<RssBuilder>("/rss/channel/cloud", { attrName, attrValue, rss ->
    val cloud = rss.channel.getCloud()
    when(attrName){
        "domain" -> cloud.domain = attrValue
        "port" -> cloud.port = attrValue.toInt()
        "path" -> cloud.path = attrValue
        "registerProcedure" -> cloud.registerProcedure
        "protocol" -> cloud.protocol
        else -> {
        }
    }

}, "domain", "port", "path", "registerProcedure", "protocol")

val itemTag = tagRule<RssBuilder>("/rss/channel/item", { isStartTag, rss ->
    if (isStartTag)
        rss.channel.startItem()
    else
        rss.channel.endItem()
})

val itemTitle = textRule<RssBuilder>("/rss/channel/item/title", { text, rss ->
    rss.channel.item.setTitle(text)
})

val itemLink = textRule<RssBuilder>("/rss/channel/item/link", { text, rss ->
    rss.channel.item.setLink(text)
})

val itemDescription = textRule<RssBuilder>("/rss/channel/item/description", { text, rss ->
    rss.channel.item.setDescription(text)
})

val itemAuthor = textRule<RssBuilder>("/rss/channel/item/author", { text, rss ->
    rss.channel.item.setAuthor(text)
})

val itemGuid = textRule<RssBuilder>("/rss/channel/item/guid", { text, rss ->
    rss.channel.item.setGuid(text)
})

val itemIsPermaLink = attributeRule<RssBuilder>("/rss/channel/item/guid", { attrName, attrValue, rss ->
    if (attrName == "isPermaLink")
        rss.channel.item.setIsPermaLink(attrValue)
}, "isPermaLink")

val itemPubDate = textRule<RssBuilder>("/rss/channel/item/pubDate", { text, rss ->
    rss.channel.item.setPubDate(text)
})

val itemEnclosure = attributeRule<RssBuilder>("/rss/channel/item/enclosure", { attrName, attrValue, rss ->
    val enclosure = rss.channel.item.getEnclosure()
    when(attrName){
        "url" -> enclosure.url = attrValue
        "length" -> enclosure.length = attrValue.toInt()
        "type" -> enclosure.`type` = attrValue
        else -> {
        }
    }
}, "url", "length", "type")

val itemMobileAppVersion = textRule<RssBuilder>("/rss/channel/item/[http://rivers.silverkeytech.com/en/mobile-app-updates]version", { text, rss ->
    rss.channel.item.setExtension("mobileapp:version", text)
})
