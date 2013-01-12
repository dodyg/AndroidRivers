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

package com.silverkeytech.android_rivers.riverjs

import com.silverkeytech.android_rivers.DateHelper
import com.silverkeytech.android_rivers.syndications.SyndicationFeed
import java.util.ArrayList


fun accumulateList(list: ArrayList<RiverItemMeta>, feed: SyndicationFeed) {
    for(val f in feed.items.iterator()){
        val item = RiverItem()

        item.title = f.title
        item.body = f.description
        item.pubDate = DateHelper.formatRFC822(f.pubDate!!)

        if (f.hasLink())
            item.link = f.link

        val link = if (feed.hasLink())
            feed.link else ""

        val meta = RiverItemMeta(item, feed.title, link)

        list.add(meta)
    }
}

fun sortRiverItemMeta(newsItems : List<RiverItemMeta>) : List<RiverItemMeta>{
    var sortedNewsItems = newsItems.filter { x -> x.item.isPublicationDate()!! }.sort(
            comparator {(p1: RiverItemMeta, p2: RiverItemMeta) ->
                val date1 = p1.item.getPublicationDate()
                val date2 = p2.item.getPublicationDate()

                if (date1 != null && date2 != null) {
                    date1.compareTo(date2) * -1
                } else if (date1 == null && date2 == null) {
                    0
                } else if (date1 == null) {
                    -1
                } else {
                    1
                }
            })

    return sortedNewsItems
}
