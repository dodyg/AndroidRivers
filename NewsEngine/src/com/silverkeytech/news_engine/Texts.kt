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

package com.silverkeytech.news_engine

import android.util.Log
import java.util.UUID

fun scrubJsonP(text: String): String {
    val rep = text.replace("onGetRiverStream(\\s*)\\(".toRegex(), "").removeSuffix(")")
    Log.d("scrubJsonP", rep)
    return rep
}


fun rightPadding(text: String, sizeTarget: Int): String {
    if (text.length() < sizeTarget){
        var spaces = StringBuffer()
        val diff = sizeTarget - text.length()

        for(i in 0..diff)
            spaces.append(' ')

        return text + spaces
    }else
        return text
}


fun getFileNameFromUri(url: String): String? {
    try{
        val fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
        return fileName
    }catch(e: Exception){
        return null
    }
}

fun generateThrowawayName(): String {
    val name = UUID.randomUUID().toString()
    return name.substring(0, 6)
}

val LOCAL_URL: String = "http://www.localhost/"
public fun makeLocalUrl(id: Int): String {
    val url = LOCAL_URL + id.toString()
    return url
}

public fun isLocalUrl(url: String): Boolean = url.contains(LOCAL_URL)

public fun extractIdFromLocalUrl(url: String): Int? {
    try{
        val id = url.substring(LOCAL_URL.length()).toString()
        if (id.isNullOrEmpty())
            return null
        else
            return id.toInt()
    }catch(e: Exception){
        return null
    }
}
