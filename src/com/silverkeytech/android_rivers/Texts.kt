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
    val rep = text.replace("onGetRiverStream(\\s*)\\(".toRegex(), "").removeSuffix(")")
    Log.d("scrubJsonP", rep)
    return rep
}

fun scrubHtml(text: String?): String {
    if (text.isNullOrBlank())
        return ""
    else {
        val txt = text!!.trim().replace("(<br>|<br/>)".toRegex(), "\n")
        val spanned = android.text.Html.fromHtml(txt) as SpannableStringBuilder
        val spannedObjects = spanned.getSpans(0, spanned.length(), javaClass<Any>())!!

        for(i in 0..(spannedObjects.size() - 1)){
            if (spannedObjects[i] is ImageSpan){
                val img = spannedObjects[i] as ImageSpan
                spanned.replace(spanned.getSpanStart(img), spanned.getSpanEnd(img), "")
            }
        }

        return spanned.toString()
    }
}


fun String?.limitText(maxSize: Int): String {
    if (this.isNullOrBlank())
        return ""
    else if (this!!.length().toInt() <= maxSize)
        return this
    else
    {
        var cut = TextLimiter.ellipsize(this, maxSize)!!
        return cut
    }
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

var arabicFont: Typeface? = null
fun handleForeignTextStyle(context: Activity, language: String, text: TextView, textSize: Float) {
    when(language.toLowerCase()){
        "ar", "ar-eg" -> {
            if (arabicFont == null)
                arabicFont = Typeface.createFromAsset(context.getAssets(), "DroidKufi-Regular.ttf")
            text.setTypeface(arabicFont)
            text.setGravity(Gravity.RIGHT)
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize - 4.toFloat())
        }
        "iw", "he" -> {
            //hebrew
            text.setGravity(Gravity.RIGHT)
        }
        else -> {
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
        }
    }
}

fun handleForeignText(language: String, text: TextView, content: String) {
    when(language.toLowerCase()){
        "ar", "ar-eg" -> {
            text.setText(ArabicReshape.reshape(content))
        }
        else -> {
            val processed = content.trim()
                    .replace("\n","<br/>")
                    .replace("(<br/><br/><br/><br/>|<br/><br/><br/>)".toRegex(), "<br/>")
            val spannable = android.text.Html.fromHtml(processed)
            text.setText(spannable)
        }
    }
}

fun handleFontResize(text: TextView, content: String, textSize: Float) {
    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
    text.setText(content)
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
        val id = url.substring(LOCAL_URL.length()).toString()
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
