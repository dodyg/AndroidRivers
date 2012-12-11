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
            if (outline.outlineType == "include" && !outline.url.isNullOrEmpty()){
                o.putAttribute("type", "include")
                o.putAttribute("url", outline.url!!)
            }
            else if (outline.outlineType == "link" && !outline.url.isNullOrEmpty()){
                o.putAttribute("type", "link")
                o.putAttribute("url", outline.url!!)
            }
            else if (outline.outlineType == "blogpost" && !outline.url.isNullOrEmpty()){
                o.putAttribute("type", "blogpost")
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