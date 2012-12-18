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

package com.silverkeytech.android_rivers.riverjs;

import com.silverkeytech.android_rivers.DateHelper;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class FeedItem {
    public String id;
    public String title;
    public String body;
    public String permaLink;
    public String pubDate;
    public String link;
    public String comments;
    public ArrayList<FeedImage> thumbnail;
    public ArrayList<FeedEnclosure> enclosure;
    public ArrayList<FeedSource> source;

    @NotNull
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

    @NotNull
    public Boolean containsEnclosure() {
        return enclosure != null && !enclosure.isEmpty();
    }

    @NotNull
    public Boolean containsSource(){
        return source != null && source.size() > 0;
    }

    @Override
    @NotNull
    public String toString() {
        if (title != null && title.length() > 0)
            return android.text.Html.fromHtml(title).toString();
        else
            return android.text.Html.fromHtml(body).toString();
    }
}
