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

import java.util.Date
import com.silverkeytech.news_engine.syndications.RssDate
import com.silverkeytech.news_engine.syndications.ParsedDateFormat
import com.silverkeytech.news_engine.syndications.parseDate
import com.silverkeytech.news_engine.syndications.getDateInFormat

public class DublinCore(){
    public var date: String? = null
    public var language: String? = null
    public var rights: String? = null
    public var source: String? = null
    public var title: String? = null
    public var publisher: String? = null
    public var creator: String? = null
    public var subject: String? = null

    public fun getDate(): RssDate {
        return parseDate(date)
    }
    public fun geDateInFormat(status: ParsedDateFormat): Date? {
        return getDateInFormat(status, date!!)
    }
}