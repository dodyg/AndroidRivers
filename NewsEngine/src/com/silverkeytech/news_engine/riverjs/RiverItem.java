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

package com.silverkeytech.news_engine.riverjs;

import com.silverkeytech.news_engine.DateHelper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class RiverItem {
    public Integer index;
    public String id;
    public String title;
    public String body;
    public String permaLink;
    public String pubDate;
    public String link;
    public String comments;
    public ArrayList<RiverImage> thumbnail;
    public ArrayList<RiverEnclosure> enclosure;
    public ArrayList<RiverSource> source;

    public Boolean isPublicationDate() {
        return pubDate != null && pubDate != "";
    }

    public Date getPublicationDate() {
        try {
            return DateHelper.parseRFC822(pubDate);
        } catch (ParseException e) {
            return null;
        }
    }

    public Boolean hasEnclosure() {
        return enclosure != null && !enclosure.isEmpty();
    }

    public Boolean hasSource() {
        return source != null && source.size() > 0;
    }

    @Override
    public String toString() {
        if (title != null && title.length() > 0)
            return android.text.Html.fromHtml(title).toString();
        else
            return android.text.Html.fromHtml(body).toString();
    }
}
