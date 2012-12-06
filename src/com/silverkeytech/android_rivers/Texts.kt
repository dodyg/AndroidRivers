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
