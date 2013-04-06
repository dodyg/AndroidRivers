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

package com.silverkeytech.news_engine.syndications.rss;

import com.silverkeytech.news_engine.syndications.ParsedDateFormat;
import com.silverkeytech.news_engine.syndications.RssDate;
import com.silverkeytech.news_engine.syndications.SyndicationsPackage;

import java.util.Date;
import java.util.HashMap;

public class Item {
    public String title;

    public String link;

    public String description;

    public String author;

    public String guid;

    public String isPermalink;

    public String pubDate;

    public Enclosure enclosure;

    public HashMap<String, String> extensions = new HashMap<String, String>();

    public RssDate getPubDate() {
        return SyndicationsPackage.parseDate(pubDate);
    }

    public Date getPubDateInFormat(ParsedDateFormat status) {
        return SyndicationsPackage.getDateInFormat(status, pubDate);
    }
}
