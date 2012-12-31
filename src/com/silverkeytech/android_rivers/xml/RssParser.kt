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

package com.silverkeytech.android_rivers.xml

import com.thebuzzmedia.sjxp.XMLParser
import com.thebuzzmedia.sjxp.rule.DefaultRule
import com.silverkeytech.android_rivers.syndications.rss.RssBuilder
import java.io.InputStream
import com.thebuzzmedia.sjxp.rule.ParsingMode
import com.silverkeytech.android_rivers.isNullOrEmpty

public class RssParser{
    public fun parse(input : InputStream, rss : RssBuilder){
        var parser = XMLParser<RssBuilder>(channelTitle, channelLink, channelDescription, channelPubDate,
                channelLastBuildDate, channelDocs, channelGenerator, channelManagingEditor, channelWebMaster,
                channelTtl,
                itemTag, itemTitle, itemLink, itemDescription
                )
        parser.parse(input, rss)
    }
}

val channelTitle = textRule<RssBuilder>("/rss/channel/title", { (text, rss) ->
    rss.channel.setTitle(text)
})

val channelLink = textRule<RssBuilder>("/rss/channel/link", { (text, rss) ->
    rss.channel.setLink(text)
})

val channelDescription = textRule<RssBuilder>("/rss/channel/description", { (text, rss) ->
    rss.channel.setDescription(text)
})

val channelPubDate = textRule<RssBuilder>("/rss/channel/pubDate", { (text, rss) ->
    rss.channel.setPubDate(text)
})

val channelLastBuildDate = textRule<RssBuilder>("/rss/channel/lastBuildDate", { (text, rss) ->
    rss.channel.setLastBuildDate(text)
})

val channelDocs = textRule<RssBuilder>("/rss/channel/docs", { (text, rss) ->
    rss.channel.setDocs(text)
})

val channelGenerator = textRule<RssBuilder>("/rss/channel/generator", { (text, rss) ->
    rss.channel.setGenerator(text)
})

val channelManagingEditor = textRule<RssBuilder>("/rss/channel/managingEditor", { (text, rss) ->
    rss.channel.setManagingDirector(text)
})

val channelWebMaster = textRule<RssBuilder>("/rss/channel/webMaster", { (text, rss) ->
    rss.channel.setWebMaster(text)
})

val channelTtl = textRule<RssBuilder>("/rss/channel/ttl", { (text, rss) ->
    rss.channel.setTitle(text.toInt())
})

val itemTag = tagRule<RssBuilder>("/rss/channel/item", { (isStartTag, rss) ->
    if (isStartTag)
        rss.channel.startItem()
    else
        rss.channel.endItem()
})

val itemTitle = textRule<RssBuilder>("/rss/channel/item/title", { (text, rss) ->
    rss.channel.item.setTitle(text)
})

val itemDescription = textRule<RssBuilder>("/rss/channel/item/description", { (text, rss) ->
    rss.channel.item.setDescription(text)
})

val itemLink = textRule<RssBuilder>("/rss/channel/item/link", { (text, rss) ->
    rss.channel.item.setLink(text)
})
