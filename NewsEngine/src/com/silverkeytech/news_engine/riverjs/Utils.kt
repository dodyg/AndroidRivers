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

package com.silverkeytech.news_engine.riverjs

import java.util.Vector
import com.silverkeytech.news_engine.DateHelper
import com.silverkeytech.news_engine.syndications.SyndicationFeed
import com.silverkeytech.news_engine.log
import java.util.ArrayList

fun accumulateList(list: Vector<RiverItemMeta>, feed: SyndicationFeed) {
    for(f in feed.items.iterator()){
        val item = RiverItem()

        item.title = f.title
        item.body = f.description
        item.pubDate = DateHelper.formatRFC822(f.pubDate!!)

        if (f.hasLink())
            item.link = f.link

        if (f.hasEnclosure()){
            val enc = RiverEnclosure()
            enc.url = f.enclosure!!.url
            enc.`type` = f.enclosure!!.mimeType
            enc.length = f.enclosure!!.length
            item.enclosure = arrayListOf(enc)
        }

        val feedSourceLink = if (feed.hasLink())
            feed.link else ""

        val meta = RiverItemMeta(item, RiverItemSource(feed.title, feedSourceLink))

        list.add(meta)
    }
}

fun sortRiverItemMeta(newsItems: List<RiverItemMeta>): List<RiverItemMeta> {
    var sortedNewsItems = newsItems.filter { x -> x.item.isPublicationDate()!! }.sortedWith(
            comparator { p1: RiverItemMeta, p2: RiverItemMeta ->
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

//This eliminated requires sorted news item otherwise it would not work
//It performs elimination based on url or headline
fun eliminateDuplicates(newsItems: List<RiverItemMeta>): List<RiverItemMeta> {
    if (newsItems.size() < 2)
        return newsItems

    val list = ArrayList<RiverItemMeta>()

    var comparisonItem = newsItems.get(0)

    for(i in 1..(newsItems.size() - 1)){
        val currentItem = newsItems.get(i)
        if (comparisonItem.item.title != currentItem.item.title ||
            comparisonItem.item.pubDate != currentItem.item.pubDate){
            list.add(comparisonItem)
        }

        comparisonItem = currentItem
    }

    //add the last item
    list.add(comparisonItem)

    return list
}

//Take all the news items from several different feeds and combine them into one.
fun River.getSortedNewsItems(): List<RiverItemMeta> {
    val newsItems = ArrayList<RiverItemMeta>()

    val river = this
    for(f : RiverSite? in river.updatedFeeds?.updatedFeed?.iterator()){
        if (f != null){
            f.item?.forEach{
                newsItems.add(RiverItemMeta(it, RiverItemSource(f.feedTitle, f.feedUrl)))
            }
        }
    }

    val sortedNewsItems = sortRiverItemMeta(newsItems)
    log("GetSortedNews", "Sorted News Items Orig ${sortedNewsItems.size()}")
    val withoutDuplicates = eliminateDuplicates(sortedNewsItems)
    log("GetSortedNews", "Sorted News Items After Duplication Elimination ${withoutDuplicates.size()}")
    return withoutDuplicates
}