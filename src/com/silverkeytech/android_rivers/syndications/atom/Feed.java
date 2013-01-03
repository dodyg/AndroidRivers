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

import com.silverkeytech.android_rivers.DateHelper;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.Date;

/* follows http://www.atomenabled.org/developers/syndication/ */

public class Feed {
    //required elements
    @Element
    public String id;

    @Element
    public String title;

    @Element
    public String updated;

    //recommended optional elements

    @ElementList(inline = true, required = false, entry = "author")
    public ArrayList<PersonElement> author = new ArrayList<PersonElement>();

    @ElementList(inline = true, required = false, entry = "link")
    public ArrayList<LinkElement> link = new ArrayList<LinkElement>();

    //the rest
    @ElementList(inline = true, required = false, entry = "category")
    public ArrayList<CategoryElement> category = new ArrayList<CategoryElement>();

    @ElementList(inline = true, required = false, entry = "contributor")
    public ArrayList<PersonElement> contributor = new ArrayList<PersonElement>();

    @Element(required = false)
    public String icon;

    @Element(required = false)
    public String logo;

    @Element(required = false)
    public TextElement rights;

    @Element(required = false)
    public String subtitle;

    @ElementList(inline = true)
    public ArrayList<Entry> entry = new ArrayList<Entry>();

    public Date getUpdated() {
        if (updated == null)
            return null;

        try {
            return DateHelper.parseRFC822(updated);
        } catch (Exception e) {
            return null;
        }
    }
}
