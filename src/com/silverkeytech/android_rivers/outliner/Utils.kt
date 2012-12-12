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

package com.silverkeytech.android_rivers.outliner

import com.silverkeytech.android_rivers.outlines.Opml
import java.util.ArrayList
import com.silverkeytech.android_rivers.outlines.Outline
import com.silverkeytech.android_rivers.Result
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.Serializer
import android.util.Log
import com.silverkeytech.android_rivers.scrubHtml
import com.silverkeytech.android_rivers.isNullOrEmpty

//do an in order traversal so we can flatten it up to be used by outliner
fun Opml.traverse (filter : ((Outline) -> Boolean)? = null, depthLimit : Int = 12) : ArrayList<OutlineContent>{
    var list = ArrayList<OutlineContent>()

    var level = 0
    for (val o in this.body?.outline?.iterator())    {
        traverseOutline(level, o, list, filter, depthLimit)
    }
    return list
}

private fun traverseOutline(level : Int, outline : Outline?, list : ArrayList<OutlineContent>, filter : ((Outline) -> Boolean)?, depthLimit : Int){
    if (outline != null){
        val proceed = level < depthLimit && (filter == null || filter(outline))

        if (proceed){

            var o = OutlineContent(level, scrubHtml(outline.text!!))
            if (!outline.outlineType.isNullOrEmpty() && !outline.url.isNullOrEmpty()){
                o.putAttribute("type", outline.outlineType!!)
                o.putAttribute("url", outline.url!!)
            }

            list.add(o)

            var lvl = level
            lvl++

            for(val o in outline.outline?.iterator()){
                traverseOutline(lvl, o, list, filter, depthLimit)
            }
        }
    }
}

fun transformXmlToOpml(xml: String?): Result<Opml> {
    var serial: Serializer = Persister()

    try{
        val opml: Opml? = serial.read(javaClass<Opml>(), xml, false)
        Log.d("OPML Transform", "OPML ${opml?.head?.title} created on ${opml?.head?.getDateCreated()} and modified on ${opml?.head?.getDateModified()}")
        return Result.right(opml)
    }
    catch (e: Exception){
        Log.d("OPML Transform", "Exception ${e.getMessage()}")
        return Result.wrong(e)
    }
}