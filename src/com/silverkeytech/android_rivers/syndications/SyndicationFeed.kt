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

package com.silverkeytech.android_rivers.syndication

import com.silverkeytech.android_rivers.syndications.rss.Rss
import com.silverkeytech.android_rivers.syndications.rss.Item
import java.util.ArrayList
import com.silverkeytech.android_rivers.scrubHtml
import android.util.Log
import com.silverkeytech.android_rivers.syndications.atom.Feed
import com.silverkeytech.android_rivers.isNullOrEmpty
import com.silverkeytech.android_rivers.syndications.SyndicationFeedType
import com.silverkeytech.android_rivers.syndications.SyndicationFeedEnclosure
import com.silverkeytech.android_rivers.syndications.atom.TextElement
import com.silverkeytech.android_rivers.syndications.atom.ContentElement

public data class SyndicationFeed(public val rss : Rss?, public val atom : Feed?){

    public var title : String = ""
    public var language : String = ""
    public var feedType : SyndicationFeedType = SyndicationFeedType.NONE
    public var items : ArrayList<SyndicationFeedItem> = ArrayList<SyndicationFeedItem>()


    public fun transform(){
        transformRss()
        transformAtom()
    }

    fun transformRss()
    {
        if (rss != null){
            val channel = rss!!.channel
            if (channel != null){
                title = if (channel.title.isNullOrEmpty()) "" else channel.title!!
                language = if (channel.language.isNullOrEmpty()) "" else channel.language!!

                feedType = SyndicationFeedType.RSS

                for(val i in channel.item!!.iterator()){
                    val fi = SyndicationFeedItem()
                    fi.title = i.title
                    fi.description = scrubHtml(i.description)
                    fi.pubDate = i.getPubDate()
                    fi.link = i.link

                    if (i.enclosure != null){
                        val enclosure = SyndicationFeedEnclosure(
                            i.enclosure!!.url!!,
                            i.enclosure!!.length!!,
                            i.enclosure!!.`type`!!
                        )
                        fi.enclosure = enclosure
                    }
                    items.add(fi)
                }
            }
        }
    }

    fun transformAtom(){
        if (atom != null){
            title = if (atom!!.title.isNullOrEmpty()) "" else atom!!.title!!

            feedType = SyndicationFeedType.ATOM

            for(val i in atom!!.entry!!.iterator()){
                var fi = SyndicationFeedItem()
                fi.title = i.title
                //link type "alternate"
                Log.d("transformAtom", "Links available ${i.link?.count()}")

                if (i.link!!.count() > 0){
                    var alternateLinks = i.link!!.filter { x -> !x.rel.isNullOrEmpty() && x.rel == "alternate" }
                    if (alternateLinks.count() > 0)
                        fi.link = alternateLinks.first().href

                    //enclosure
                    var enclosureLinks =  i.link!!.filter { x -> !x.rel.isNullOrEmpty() && x.rel == "enclosure" }

                    if (alternateLinks.count() > 0){
                        var altLink = alternateLinks.first()

                        if (altLink.length != null && altLink.length!! > 0
                            && !altLink.`type`.isNullOrEmpty()){
                            val enclosure = SyndicationFeedEnclosure(
                                    altLink.href!!,
                                    altLink.length!!,
                                    altLink.`type`!!
                            )
                            fi.enclosure = enclosure
                        }
                    }
                }

                fun processDescription(text : ContentElement){
                    if (text.`type` == null){
                        fi.description = text.value
                    }
                    else if (text.`type` != null){
                        if (text.`type` == "text"){
                            fi.description = text.value
                        } else {
                            fi.description = scrubHtml(text.value)
                        }
                    }
                }

                if (i.summary != null){
                    processDescription(i.summary!!)
                }
                else if (i.content != null){
                    processDescription(i.content!!)
                }

                fi.pubDate = i.getUpdated()

                items.add(fi)
            }
        }
    }
}