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

package com.silverkeytech.android_rivers

import java.util.UUID

fun scrubJsonP(text: String): String {
    val rep = text.replace("onGetRiverStream (", "").trimTrailing(")")
    return rep
}

fun scrubHtml(text: String?): String {
    if (text.isNullOrEmpty())
        return ""
    else
        return android.text.Html.fromHtml(text!!).toString()
}

fun String?.isNullOrEmpty(): Boolean {
    val res = this == null || this.trim().length() == 0
    return res
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
