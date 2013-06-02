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

package com.silverkeytech.news_engine.outlines


public class OpmlBuilder{
    public val opml : Opml = Opml()
    public val body : BodyBuilder = BodyBuilder(opml)
    public val head : HeadBuilder = HeadBuilder(opml)

    public class HeadBuilder(val opml : Opml){
        {
            opml.head = Head()
        }
        public fun setTitle(title : String) : Unit = opml.head!!.title = title
        public fun setDateCreated(date : String) : Unit = opml.head!!.dateCreated = date
        public fun setDateModified(date : String) : Unit =  opml.head!!.dateModified = date
        public fun setOwnerName(owner: String) : Unit = opml.head!!.ownerName = owner
    }

    public class BodyBuilder (val opml : Opml){
        {
            opml.body = Body()
        }

    }
}