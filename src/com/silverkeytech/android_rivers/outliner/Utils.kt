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

import android.util.Log
import com.silverkeytech.android_rivers.Result
import com.silverkeytech.android_rivers.isNullOrEmpty
import com.silverkeytech.android_rivers.outlines.Opml
import com.silverkeytech.android_rivers.outlines.Outline
import com.silverkeytech.android_rivers.scrubHtml
import go.goyalla.dict.arabicDictionary.file.ArabicReshape
import java.util.ArrayList
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import android.content.Context
import android.content.Intent
import com.silverkeytech.android_rivers.OutlinerActivity

//do an in order traversal so we can flatten it up to be used by outliner
fun Opml.traverse (filter: ((Outline) -> Boolean)? = null, depthLimit: Int = 12): ArrayList<OutlineContent> {
    var list = ArrayList<OutlineContent>()

    var level = 0
    for (val o in this.body?.outline?.iterator())    {
        traverseOutline(level, o, list, filter, depthLimit)
    }
    return list
}

private fun traverseOutline(level: Int, outline: Outline?, list: ArrayList<OutlineContent>, filter: ((Outline) -> Boolean)?, depthLimit: Int) {
    if (outline != null){
        val proceed = level < depthLimit && (filter == null || filter(outline))

        if (proceed){

            var o = OutlineContent(level, scrubHtml(outline.text!!))
            if (!outline.outlineType.isNullOrEmpty() && !outline.url.isNullOrEmpty()){
                o.putAttribute("type", outline.outlineType!!)
                o.putAttribute("url", outline.url!!)
            }

            if (!outline.language.isNullOrEmpty()){
                o.putAttribute("language", outline.language!!)

                if (isLanguageRTL(outline.language!!))                {
                    Log.d("traverseOutline", "Reshaping Arabic")
                    o.text = "\u200F" + ArabicReshape.reshape(o.text) //at RTL marker
                }

            } else {
                o.putAttribute("language", "en")
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

fun isLanguageRTL(language: String): Boolean {
    return when(language){
        "ar" -> true
        else -> false
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

fun startOutlinerActivity(context : Context, outlines : ArrayList<OutlineContent>, title : String, url : String?) {
    var intent = Intent(Intent.ACTION_MAIN)
    intent.setClass(context, javaClass<OutlinerActivity>())
    intent.putExtra(OutlinerActivity.OUTLINES_DATA, outlines)
    intent.putExtra(OutlinerActivity.OUTLINES_TITLE, title)
    intent.putExtra(OutlinerActivity.OUTLINES_URL, url)
    context.startActivity(intent)
}