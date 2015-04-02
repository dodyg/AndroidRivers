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

public class RdfRssBuilder(){
    private val rdf: Rdf = Rdf()
    public val channel: ChannelBuilder = ChannelBuilder(rdf)

    public fun build(): Rdf {
        return rdf
    }

    public class ChannelBuilder(private val rdf: Rdf){
        init {
            rdf.channel = Channel()
        }

        public var item: ItemBuilder = ItemBuilder(Item())
            get() = $item
            set(value) {
                $item = value
            }

        public fun setTitle(title: String) {
            rdf.channel.title = title
        }

        public fun setDescription(description: String) {
            rdf.channel.description = description
        }

        public fun setLink(link: String) {
            rdf.channel.link = link
        }

        public fun setDcPublisher(publisher: String) {
            rdf.channel.dc.publisher = publisher
        }

        public fun setDcLanguage(lang: String) {
            rdf.channel.dc.language = lang
        }

        public fun setDcRights(rights: String) {
            rdf.channel.dc.rights = rights
        }

        public fun setDcTitle(title: String) {
            rdf.channel.dc.title = title
        }

        public fun setDcCreator(creator: String) {
            rdf.channel.dc.creator = creator
        }

        public fun setDcSource(source: String) {
            rdf.channel.dc.source = source
        }

        public fun startItem() {
            item = ItemBuilder(Item())
        }

        public fun endItem() {
            rdf.item.add(item.data)
        }
    }

    public class ItemBuilder(public val data: Item){
        public fun setTitle(title: String) {
            data.title = title
        }

        public fun setLink(link: String) {
            data.link = link
        }

        public fun setDescription(description: String) {
            data.description = description
        }

        public fun setAbout(about: String) {
            data.about = about
        }

        public fun setDcDate(date: String) {
            data.dc.date = date
        }

        public fun setDcLanguage(lang: String) {
            data.dc.language = lang
        }

        public fun setDcRights(rights: String) {
            data.dc.rights = rights
        }

        public fun setDcSource(source: String) {
            data.dc.source = source
        }

        public fun setDcTitle(title: String) {
            data.dc.title = title
        }
    }
}