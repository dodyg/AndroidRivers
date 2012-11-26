package com.silverkeytech.android_rivers


fun scrubJsonP(text : String) : String{
    var rep = text.replace("onGetRiverStream (","")
    rep = rep.trimTrailing(")")
    return rep
}

fun scrubHtml(text : String) : String{
    return android.text.Html.fromHtml(text).toString()
}

fun String?.isNullOrEmpty() : Boolean{
    var res = this == null || this.trim().length() == 0
    return res
}
