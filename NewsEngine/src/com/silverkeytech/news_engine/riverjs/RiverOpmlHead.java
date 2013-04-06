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

import java.util.Date;

public class RiverOpmlHead {
    public String title;
    public String dateCreated;       //RFC822 time
    public String dateModified;      //RFC822 time
    public String ownerName;

    public Date getDateCreated() {
        if (dateCreated == null)
            return null;

        try {
            Date date = DateHelper.parseRFC822(dateCreated);
            return date;

        } catch (Exception e) {
            return null;
        }
    }

    public Date getDateModified() {
        if (dateModified == null)
            return null;

        try {
            Date date = DateHelper.parseRFC822(dateModified);
            return date;
        } catch (Exception e) {
            return null;
        }
    }
}
