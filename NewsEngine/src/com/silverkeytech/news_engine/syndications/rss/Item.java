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

import android.util.Log;
import com.silverkeytech.news_engine.DateHelper;
import com.silverkeytech.news_engine.syndications.ParsedDateFormat;
import com.silverkeytech.news_engine.syndications.RssDate;

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
        if (pubDate == null)
            return new RssDate(ParsedDateFormat.MISSING, null);

        if (pubDate.endsWith("Z")){
            try{
                //Z means whatever time zone so we put it at GMT
                return new RssDate(ParsedDateFormat.ISO8601_NOMS, DateHelper.parseISO8601NoMilliseconds(pubDate.replaceAll("Z$", "+0000")));
            }catch(Exception e){
                Log.d("RSSItem", "Error parsing " + pubDate + " in ISO8601_NOMS Modified Z");
            }
        }

        try {
            return new RssDate(ParsedDateFormat.RFC822, DateHelper.parseRFC822(pubDate));
        } catch (Exception e) {
            Log.d("RSSItem", "Error parsing " + pubDate + " in RFC822");
        }

        try {
            return new RssDate(ParsedDateFormat.ISO8601_NOMS, DateHelper.parseISO8601NoMilliseconds(pubDate));
        } catch (Exception e){
            Log.d("RSSItem", "Error parsing " + pubDate + " in ISO8601_NOMS");
        }

        try{
            return new RssDate(ParsedDateFormat.NO_SPACES, DateHelper.parse(DateHelper.NO_SPACES, pubDate));
        } catch (Exception e){
            Log.d("RSSItem", "Error parsing " + pubDate + " in NO_SPACES");
        }

        return new RssDate(ParsedDateFormat.UNKNOWN, null);
    }

    public Date getPubDateInFormat(ParsedDateFormat status){
        try{
            if (status == ParsedDateFormat.RFC822)
                return DateHelper.parseRFC822(pubDate);
            else
            if (status == ParsedDateFormat.ISO8601_NOMS){
                if (pubDate.endsWith("Z"))
                    return DateHelper.parseISO8601NoMilliseconds(pubDate.replaceAll("Z$", "+0000"));
                else
                    return DateHelper.parseISO8601NoMilliseconds(pubDate);
            }
            else if (status == ParsedDateFormat.NO_SPACES)
                return DateHelper.parse(DateHelper.NO_SPACES, pubDate);
            else
                return null;
        }
        catch (Exception e) {
            return null;
        }
    }
}
