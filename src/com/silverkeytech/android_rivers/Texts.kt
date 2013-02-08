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

import android.app.Activity
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import go.goyalla.dict.arabicDictionary.file.ArabicReshape
import java.net.URL
import java.util.UUID

fun scrubJsonP(text: String): String {
    val rep = text.replace("onGetRiverStream (", "").trimTrailing(")")
    return rep
}

fun scrubHtml(text: String?): String {
    if (text.isNullOrEmpty())
        return ""
    else {
        val spanned = android.text.Html.fromHtml(text!!.trim()) as SpannableStringBuilder
        val spannedObjects = spanned.getSpans(0, spanned.length(), javaClass<Any>())!!

        for(val i in 0..(spannedObjects.size - 1)){
            if (spannedObjects[i] is ImageSpan){
                val img = spannedObjects[i] as ImageSpan
                if (img != null){
                    spanned.replace(spanned.getSpanStart(img), spanned.getSpanEnd(img), "")
                }
            }
        }

        return spanned.toString()
    }
}


fun String?.isNullOrEmpty(): Boolean {
    val res = this == null || this.trim().length() == 0
    return res
}

fun String?.limitText(maxSize: Int): String {
    if (this.isNullOrEmpty())
        return ""
    else if (this!!.size.toInt() <= maxSize)
        return this
    else
    {
        var cut = TextLimiter.ellipsize(this, maxSize)!!
        return cut
    }
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

fun handleForeignTextFont(context: Activity, language: String, text: TextView, content: String, textSize: Float) {
    val TAG = "handleText"

    val arabicFont = Typeface.createFromAsset(context.getAssets(), "DroidKufi-Regular.ttf")

    when(language.toLowerCase()){
        "ar", "ar-eg" -> {
            Log.d(TAG, "Switching to Arabic Font")
            text.setTypeface(arabicFont)
            text.setText(ArabicReshape.reshape(content))
            text.setGravity(Gravity.RIGHT)
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize - 4.toFloat())
        }
        else -> {
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            text.setText(content);
        }
    }
}

fun handleTextColorBasedOnTheme(context: Activity, text: TextView) {
    /*var theme = context.getVisualPref().getTheme()
    if (theme == R.style.Theme_Sherlock_Light_DarkActionBar) */
    text.setTextColor(android.graphics.Color.BLACK)

    /*else if (theme == R.style.Theme_Sherlock)
        text.setTextColor(android.graphics.Color.WHITE)*/
}

val LOCAL_URL: String = "http://www.localhost/"
public fun makeLocalUrl(id: Int): String {
    val url = LOCAL_URL + id.toString()
    return url
}

public fun isLocalUrl(url: String): Boolean = url.contains(LOCAL_URL)

public fun extractIdFromLocalUrl(url: String): Int? {
    try{
        val id = url.substring(LOCAL_URL.size).toString()
        if (id.isNullOrEmpty())
            return null
        else
            return id.toInt()
    }catch(e: Exception){
        return null
    }
}

fun safeUrlConvert(url: String?): Result<URL> {
    try
    {
        val u = URL(url)
        return Result.right(u)
    }
    catch(ex: Exception){
        return Result.wrong<URL>(ex)
    }
}


