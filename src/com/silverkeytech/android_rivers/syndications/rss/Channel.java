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

import java.util.ArrayList;

public class Channel {
    public String title;

    public String link;  //http://timesofindia.feedsportal.com/c/33039/f/533965/index.rss

    public String description;

    public String language;

    public String pubDate;

    public String lastBuildDate;

    public String docs;

    public String generator;

    public String managingEditor;

    public String webMaster;

    public Integer ttl;

    public Cloud cloud;

    public ArrayList<Item> item = new ArrayList<Item>();
}
