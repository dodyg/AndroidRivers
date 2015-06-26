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

package com.silverkeytech.news_engine.syndications

import com.silverkeytech.news_engine.syndications.atom.ContentElement
import com.silverkeytech.news_engine.syndications.atom.Feed
import com.silverkeytech.news_engine.syndications.rss.Rss
import java.util.ArrayList
import com.silverkeytech.news_engine.log
import com.silverkeytech.news_engine.syndications.rss_rdf.Rdf

public data class SyndicationFeed(public val rss: Rss?, public val atom: Feed?, public val rdf: Rdf?, val filter: SyndicationFilter? = null){
    companion object{
        public val TAG: String = javaClass<SyndicationFeed>().getSimpleName()
    }

    public var title: String = ""
    public var language: String = ""
    public var link: String = ""
    public var feedType: SyndicationFeedType = SyndicationFeedType.NONE
    public var items: ArrayList<SyndicationFeedItem> = ArrayList<SyndicationFeedItem>()
    public var isDateParseable: Boolean = false

    public fun hasLink(): Boolean {
        return !link.isNullOrBlank()
    }

    public fun transform() {
        transformRss()
        transformAtom()
        transformRdf()
    }

    fun transformRss()
    {
        if (rss != null){
            val (parseable, dateFormat) = verifyRssFeedForDateFitness(rss!!)
            isDateParseable = parseable

            log(TAG, "isDateParseable is $isDateParseable with date format $dateFormat")
            val channel = rss!!.channel
            if (channel != null){
                title = if (channel.title.isNullOrEmpty()) "" else channel.title!!
                language = if (channel.language.isNullOrEmpty()) "" else channel.language!!

                if (!channel.link.isNullOrEmpty())
                    link = channel.link!!

                feedType = SyndicationFeedType.RSS

                var itemCounter = 0
                val maxSize = filter?.maximumSize
                val oldestDate = filter?.oldestDate


                for(i in channel.item!!){
                    itemCounter++

                    //stop processing if there's a limit on how many items to be processed
                    if (maxSize != null && itemCounter > maxSize){
                        log(TAG, "Max size of limit reached at $itemCounter")
                        break
                    }

                    val fi = SyndicationFeedItem()
                    fi.title = i.title
                    fi.description = i.description
                    //the date parsing is exception heavy. Don't do it over a loop. Better verify it first.
                    if (isDateParseable)                                                  {
                        fi.pubDate = i.getPubDateInFormat(dateFormat)
                        if (oldestDate != null && fi.pubDate!!.compareTo(oldestDate) == -1){
                            log(TAG, "Oldest item limit reached at ${fi.pubDate}")
                            break
                        }
                    }

                    fi.link = i.link

                    if (i.enclosure != null){
                        val e = i.enclosure!!
                        val enclosure = SyndicationFeedEnclosure(
                                e.url!!,
                                if (e.length != null) e.length!! else 0,
                                if (!e.`type`.isNullOrEmpty()) e.`type`!! else ""
                        )
                        fi.enclosure = enclosure
                    }

                    if (i.extensions != null)
                        fi.extensions = i.extensions!!

                    items.add(fi)
                }
            }
        }
    }

    fun transformAtom() {
        if (atom != null){
            isDateParseable = verifyAtomFeedForDateFitness(atom!!)
            log(TAG, "isDateParseable is $isDateParseable")
            log(TAG, "Title is ${atom!!.title}}")
            title = if (atom!!.title.isNullOrEmpty()) "" else atom!!.title!!

            //look for rel='alternate' which contains the url to itself
            if (atom!!.link != null && atom!!.link!!.count() > 0){
                val l = atom!!.link!!.filter { it.rel == "alternate" }

                if (l.size() == 1 && !l.get(0).href.isNullOrEmpty())
                    link = l.get(0).href!!
            }

            feedType = SyndicationFeedType.ATOM

            var itemCounter = 0
            val maxSize = filter?.maximumSize

            for(i in atom!!.entry!!){
                itemCounter++

                //stop processing if there's a limit on how many items to be processed
                if (maxSize != null && itemCounter > maxSize){
                    log(TAG, "Max size of limit reached at $itemCounter")
                    break
                }

                val fi = SyndicationFeedItem()
                fi.title = i.title

                if (i.link!!.count() > 0){
                    val alternateLinks = i.link!!.filter { x -> !x.rel.isNullOrEmpty() && x.rel == "alternate" }
                    if (alternateLinks.count() > 0)
                        fi.link = alternateLinks.first().href

                    //enclosure
                    if (alternateLinks.count() > 0){
                        val altLink = alternateLinks.first()

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

                fun processDescription(text: ContentElement) {
                    if (text.`type` == null){
                        fi.description = text.value
                    }
                    else if (text.`type` != null){
                        if (text.`type` == "text"){
                            fi.description = text.value
                        } else {
                            fi.description = text.value
                        }
                    }
                }

                if (i.summary != null){
                    processDescription(i.summary!!)
                }
                else if (i.content != null){
                    processDescription(i.content!!)
                }

                //date parsing is exception heavy. Better take a sample in the beginning first than blowing up in exceptions
                //in a loop
                if (isDateParseable)
                    fi.pubDate = i.getUpdated()

                items.add(fi)
            }
        }
    }

    public fun transformRdf() {
        if (rdf != null){
            val (parseable, dateFormat) = verifyRdfFeedForDateFitness(rdf!!)
            isDateParseable = parseable

            title = if (rdf!!.channel.title.isNullOrBlank()) "" else rdf!!.channel.title!!
            link = if (rdf!!.channel.link.isNullOrBlank()) "" else rdf!!.channel.link!!
            feedType = SyndicationFeedType.RDF

            var itemCounter = 0
            val maxSize = filter?.maximumSize
            val oldestDate = filter?.oldestDate

            for(i in rdf!!.item){
                itemCounter++

                //stop processing if there's a limit on how many items to be processed
                if (maxSize != null && itemCounter > maxSize){
                    log(TAG, "Max size of limit reached at $itemCounter")
                    break
                }

                val fi = SyndicationFeedItem()
                fi.title = i.title
                fi.link = i.link
                fi.description = i.description

                if (isDateParseable)                                                  {
                    fi.pubDate = i.dc.geDateInFormat(dateFormat!!)
                    if (oldestDate != null && fi.pubDate!!.compareTo(oldestDate) == -1){
                        log(TAG, "Oldest item limit reached at ${fi.pubDate}")
                        break
                    }
                }

                items.add(fi)
            }
        }
    }
}
