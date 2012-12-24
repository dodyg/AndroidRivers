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

import org.simpleframework.xml.*;

import java.util.ArrayList;

@NamespaceList({
        @Namespace(reference="http://www.w3.org/2005/Atom", prefix="atom")
})
public class Channel {
    @Element
    public String title;

    @Element(required = false, name = "link") //this is wrong by spec but it won't process for times of india
    @Namespace
    public String link;  //http://timesofindia.feedsportal.com/c/33039/f/533965/index.rss

    @Element(required = false)
    public String description;

    @Element(required = false)
    public String language;

    @Element(required = false)
    public String pubDate;

    @Element(required = false)
    public String lastBuildDate;

    @Element(required = false)
    public String docs;

    @Element(required = false)
    public String generator;

    @Element(required = false)
    public String managingEditor;

    @Element(required = false)
    public String webMaster;

    @Element(required = false)
    public Integer ttl;

    @Element(required = false)
    public Cloud cloud;

    @ElementList(inline = true)
    public ArrayList<Item> item = new ArrayList<Item>();
}
