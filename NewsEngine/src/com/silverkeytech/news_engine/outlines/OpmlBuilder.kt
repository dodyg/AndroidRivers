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

import java.util.ArrayList
import android.util.Log

public class OpmlBuilder{
    class object {
        public val TAG: String = javaClass<OpmlBuilder>().getSimpleName()
    }

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
        public fun setOwnerEmail(email : String) : Unit = opml.head!!.ownerEmail = email
    }

    public class BodyBuilder (val opml : Opml){
        var currentLevel  = 0
        var currentOutline = Outline()
        var parentOutline : Outline? = null
        var parents : ArrayList<Outline> =  ArrayList<Outline>()
        var rootOutlines : ArrayList<Outline>? = null
        {
            opml.body = Body()
            rootOutlines = opml.body!!.outline
        }

        public fun startLevel(level : Int){
            if (level == 0){
                currentLevel = 0
                currentOutline = Outline()
                rootOutlines!!.add(currentOutline)
                parentOutline = null
                parents.clear()
            }
            else if (level > currentLevel){
                parentOutline = currentOutline
                parents.add(parentOutline!!)
                currentOutline = Outline()
                parentOutline!!.outline!!.add(currentOutline)
                currentLevel = level
            }
            else if (level == currentLevel){
                currentOutline = Outline()
                if (parentOutline != null)
                    parentOutline!!.outline!!.add(currentOutline)
            } else {
                currentLevel = level
                currentOutline = Outline()

                if (parentOutline != null)
                    parentOutline!!.outline!!.add(currentOutline)
            }
        }

        public fun endLevel(level : Int){
            if (level < currentLevel && level > 0){
                parentOutline = parents.get(level - 1)
                currentLevel = level
            }
            else if (level == 0){
                currentLevel = 0
            }

        }

        public fun setText(text : String) : Unit = currentOutline.text = text
        public fun setUrl(url : String) : Unit = currentOutline.url = url
        public fun setXmlUrl(xmlUrl : String) : Unit = currentOutline.xmlUrl = xmlUrl
        public fun setHtmlUrl(htmlUrl : String) : Unit = currentOutline.htmlUrl = htmlUrl
        public fun setOpmlUrl(opmlUrl : String) : Unit = currentOutline.opmlUrl = opmlUrl
        public fun setType(tp : String) : Unit = currentOutline.outlineType = tp
        public fun setLanguage(lang : String) : Unit = currentOutline.language = lang
        public fun setName(name : String) : Unit = currentOutline.name = name
    }

    public fun build() : Opml{
        return opml
    }
}