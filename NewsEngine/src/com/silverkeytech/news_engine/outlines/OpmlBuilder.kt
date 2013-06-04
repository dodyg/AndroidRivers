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
import java.util.Deque
import java.util.LinkedList

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
        public fun setOwnerEmail(email : String) : Unit = opml.head!!.ownerEmail = email
    }

    public class BodyBuilder (val opml : Opml){
        var currentLevel  = 0
        var currentOutline = Outline()
        var parentOutline : Outline? = null
        var rootOutlines : ArrayList<Outline>? = null
        var parents : Deque<Outline>? =  null

        {
            parents = LinkedList<Outline>()

            opml.body = Body()
            rootOutlines = opml.body!!.outline
        }

        public fun startLevel(level : Int){
            if (level == 0){
                currentLevel = 0
                currentOutline = Outline()
                rootOutlines!!.add(currentOutline)
                parentOutline = null
                while(parents!!.notEmpty())
                    parents!!.pop()
            }
            else if (level > currentLevel){
                parentOutline = currentOutline
                parents!!.push(parentOutline!!)
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
            if (level < currentLevel){
                val diff = currentLevel - level
                for (i in 0..diff){
                    if (parents!!.notEmpty()){
                        parentOutline = parents!!.pop()
                    }
                }
            }
        }

        public fun setText(text : String){
            currentOutline.text = text
        }
    }

    public fun build() : Opml{
        return opml
    }
}