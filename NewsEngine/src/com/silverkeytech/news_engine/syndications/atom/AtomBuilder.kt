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

package com.silverkeytech.news_engine.syndications.atom


public class AtomBuilder (){
    val feed = Feed()

    public fun build() : Feed {
        return feed
    }

    public fun setId(id : String){
        feed.id = id
    }

    public fun setTitle(title : String){
        feed.title = title
    }

    public fun setUpdated(updated : String){
        feed.updated = updated
    }

    public fun setIcon(icon : String){
        feed.icon = icon
    }

    public fun setLogo(logo : String){
        feed.logo = logo
    }

    public fun setSubtitle(subTitle : String){
        feed.subtitle = subTitle
    }
}