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

public class RssBuilder(){
    private val rss: Rss = Rss()
    public val channel: ChannelBuilder = ChannelBuilder(rss)

    public fun build(): Rss {
        return rss
    }

    public class ChannelBuilder(private val rss: Rss){
        init {
            rss.channel = Channel()
        }

        public var item: ItemBuilder = ItemBuilder(Item())
            get() = $item
            set(value) {
                $item = value
            }

        public fun setTitle(title: String) {
            rss.channel!!.title = title;
        }

        public fun setLink(link: String) {
            rss.channel!!.link = link
        }

        public fun setDescription(description: String) {
            rss.channel!!.description = description
        }

        public fun setLanguage(lang: String) {
            rss.channel!!.language = lang
        }

        public fun setPubDate(pubDate: String) {
            rss.channel!!.pubDate = pubDate
        }

        public fun setLastBuildDate(lastBuildDate: String) {
            rss.channel!!.lastBuildDate = lastBuildDate
        }

        public fun setDocs(docs: String) {
            rss.channel!!.docs = docs
        }

        public fun setGenerator(generator: String) {
            rss.channel!!.generator = generator
        }

        public fun setManagingDirector(managing: String) {
            rss.channel!!.managingEditor = managing
        }

        public fun setWebMaster(master: String) {
            rss.channel!!.webMaster = master
        }

        public fun setTitle(ttl: Int) {
            rss.channel!!.ttl = ttl
        }

        public fun getCloud(): Cloud {
            if (rss.channel!!.cloud == null)
                rss.channel!!.cloud = Cloud()

            return rss.channel!!.cloud!!
        }

        public fun startItem() {
            item = ItemBuilder(Item())
        }

        public fun endItem() {
            rss.channel!!.item!!.add(item.data)
        }

        public class ItemBuilder(public val data: Item){
            public fun setTitle(title: String) {
                data.title = title
            }

            public fun setLink(link: String) {
                data.link = link
            }

            public fun setDescription(desc: String) {
                data.description = desc
            }

            public fun setAuthor(author: String) {
                data.author = author
            }

            public fun setGuid(guid: String) {
                data.guid = guid
            }

            public fun setIsPermaLink(isPermaLink: String) {
                data.isPermalink = isPermaLink
            }

            public fun setPubDate(pubDate: String) {
                data.pubDate = pubDate
            }

            public fun setExtension(name: String, content: String) {
                if (!data.extensions!!.containsKey(name))
                    data.extensions!!.put(name, content);
            }

            public fun getEnclosure(): Enclosure {
                if (data.enclosure == null)
                    data.enclosure = Enclosure()

                return data.enclosure!!
            }
        }
    }
}

