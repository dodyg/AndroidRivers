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

package com.silverkeytech.android_rivers.syndications.rss;

import com.silverkeytech.android_rivers.DateHelper;
import org.simpleframework.xml.Element;

import java.util.Date;

public class Item {
    @Element(required = false)
    public String title;

    @Element(required = false)
    public String link;

    @Element(required = false)
    public String description;

    @Element(required = false)
    public String author;

    @Element(required = false)
    public String guid;

    @Element(required = false)
    public String isPermalink;

    @Element(required = false)
    public String pubDate;

    @Element(required = false)
    public Enclosure enclosure;

    public Date getPubDate() {
        if (pubDate == null)
            return null;

        try {
            return DateHelper.parseRFC822(pubDate);
        } catch (Exception e) {
            return null;
        }
    }
}
