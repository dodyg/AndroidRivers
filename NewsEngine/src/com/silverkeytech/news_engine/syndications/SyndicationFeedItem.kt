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

import java.util.Date
import java.util.HashMap

public data class SyndicationFeedItem(){
    public var title: String? = null
    public var description: String? = null
    public var link: String? = null
    public var pubDate: Date? = null
    public var enclosure: SyndicationFeedEnclosure? = null
    public var extensions: HashMap<String, String> = HashMap<String, String>()

    fun hasTitle(): Boolean {
        return !title.isNullOrBlank()
    }

    fun hasDescription(): Boolean {
        return !description.isNullOrBlank()
    }

    fun hasLink(): Boolean {
        return !link.isNullOrBlank() && link!!.contains("http://")
    }

    fun hasEnclosure(): Boolean {
        return enclosure != null
    }

    fun isPodcast(): Boolean {
        return enclosure != null && enclosure!!.mimeType == "audio/mpeg"
    }

    fun addExtension(name: String, content: String) {
        if (!extensions.containsKey(name))
            extensions.put(name, content)
    }

    override fun toString(): String {
        if (hasTitle())
            return title!!
        else if (hasDescription())
            return description!!
        else
            return ""
    }
}
