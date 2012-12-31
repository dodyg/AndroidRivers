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

public class Parser{
    public fun parseRss(input : InputStream, rss : RssBuilder){
        var parser = XMLParser<RssBuilder>(channelTitle, channelLink, channelDescription, channelPubTitle,
                itemTag, itemTitle, itemLink, itemDescription
                )
        parser.parse(input, rss)
    }
}

val channelTitle = textRule("/rss/channel/title", { (text, rss) ->
    rss.channel.setTitle(text)
})

val channelLink = textRule("/rss/channel/link", { (text, rss) ->
    rss.channel.setLink(text)
})

val channelDescription = textRule("/rss/channel/description", { (text, rss) ->
    rss.channel.setDescription(text)
})

val channelPubTitle = textRule("/rss/channel/pubDate", { (text, rss) ->
    rss.channel.setPubDate(text)
})

val itemTag = tagRule("/rss/channel/item", { (isStartTag, rss) ->
    if (isStartTag)
        rss.channel.startItem()
    else
        rss.channel.endItem()
})

val itemTitle = textRule("/rss/channel/item/title", { (text, rss) ->
    rss.channel.item.setTitle(text)
})

val itemDescription = textRule("/rss/channel/item/description", { (text, rss) ->
    rss.channel.item.setDescription(text)
})

val itemLink = textRule("/rss/channel/item/link", { (text, rss) ->
    rss.channel.item.setLink(text)
})
