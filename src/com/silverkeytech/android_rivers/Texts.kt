package com.silverkeytech.android_rivers

import java.util.UUID


fun scrubJsonP(text : String) : String{
    var rep = text.replace("onGetRiverStream (","")
    rep = rep.trimTrailing(")")
    return rep
}

fun scrubHtml(text : String?) : String{
    if (text.isNullOrEmpty())
        return ""
    else
        return android.text.Html.fromHtml(text!!).toString()
}

fun String?.isNullOrEmpty() : Boolean{
    var res = this == null || this.trim().length() == 0
    return res
}

fun getFileNameFromUri(url : String) : String?{
    try{
        var fileName = url.substring( url.lastIndexOf('/')+1, url.length() );
        return fileName
    }catch(e : Exception){
        return null
    }
}

fun generateThrowawayName() : String{
    var name = UUID.randomUUID().toString()
    return name.substring(0, 6)
}
