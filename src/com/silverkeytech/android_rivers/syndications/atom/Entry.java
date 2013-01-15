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

package com.silverkeytech.android_rivers.syndications.atom;

import org.apache.abdera.model.AtomDate;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.Date;

//Source http://www.atomenabled.org/developers/syndication/

public class Entry {
    //required elements
    @Element
    public String id;

    @Element
    public String title;

    @Element
    public String updated;

    //recommended elements

    @ElementList(inline = true, required = false, entry = "author")
    public ArrayList<PersonElement> author = new ArrayList<PersonElement>();

    @Element(required = false)
    public ContentElement content;

    @ElementList(inline = true, required = false, entry = "link")
    public ArrayList<LinkElement> link = new ArrayList<LinkElement>();

    @Element(required = false)
    public ContentElement summary;

    //the rest of the elements
    @ElementList(inline = true, required = false, entry = "category")
    public ArrayList<CategoryElement> category = new ArrayList<CategoryElement>();

    @ElementList(inline = true, required = false, entry = "contributor")
    public ArrayList<PersonElement> contributor = new ArrayList<PersonElement>();

    @Element(required = false)
    public String published;

    @Element(required = false)
    public Source source;

    @Element(required = false)
    public TextElement rights;

    public Date getUpdated() {
        if (updated == null)
            return null;

        try {
            AtomDate date = new AtomDate(updated);
            return date.getDate();
        } catch (Exception e) {
            return null;
        }
    }

    public Date getPublished() {
        if (published == null)
            return null;

        try {
            AtomDate date = new AtomDate(published);
            return date.getDate();
        } catch (Exception e) {
            return null;
        }
    }
}


/*
<?xml version="1.0" encoding="utf-8"?>
<feed xmlns="http://www.w3.org/2005/Atom">

  <entry>
    <title>Atom-Powered Robots Run Amok</title>
    <link href="http://example.org/2003/12/13/atom03"/>
    <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>
    <updated>2003-12-13T18:30:02Z</updated>
    <summary>Some text.</summary>
  </entry>

</feed>
 */
