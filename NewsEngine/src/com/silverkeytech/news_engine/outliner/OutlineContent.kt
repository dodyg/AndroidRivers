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

package com.silverkeytech.news_engine.outliner

import java.io.Serializable
import java.util.HashMap

public enum class OutlineType{
    NONE,
    INCLUDE,
    LINK,
    BLOGPOST,
    RIVER,
    RSS
}

public data class OutlineContent (var level: Int, var text: String): Serializable
{
    private var bag: HashMap<String, String> = HashMap<String, String>()

    public fun putAttribute(key: String, obj: String) {
        bag.put(key, obj)
    }

    public fun getAttribute(key: String): String? = bag.get(key)

    public fun containsKey(key: String): Boolean {
        return bag.containsKey(key)
    }

    public fun copyAttributes(outline: OutlineContent) {
        bag = outline.bag
    }

    public fun getType(): OutlineType {
        if (containsKey("type")){
            val tp = getAttribute("type")
            return when(tp){
                "include" -> OutlineType.INCLUDE
                "link" -> OutlineType.LINK
                "blogpost" -> OutlineType.BLOGPOST
                "river" -> OutlineType.RIVER
                "rss" -> OutlineType.RSS
                else -> OutlineType.NONE
            }
        }
        else
            return OutlineType.NONE
    }
}