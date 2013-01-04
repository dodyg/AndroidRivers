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
import android.app.Activity
import android.widget.TextView
import android.graphics.Typeface
import android.util.Log
import go.goyalla.dict.arabicDictionary.file.ArabicReshape
import android.view.Gravity
import android.util.TypedValue

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

fun handleForeignTextFont(context: Activity, language : String, text: TextView, content: String, textSize: Float) {
    val TAG = "handleText"

    val arabicFont = Typeface.createFromAsset(context.getAssets(), "DroidKufi-Regular.ttf")

    when(language){
        "ar" -> {
            Log.d(TAG, "Switching to Arabic Font")
            text.setTypeface(arabicFont)
            text.setText(ArabicReshape.reshape(content))
            text.setGravity(Gravity.RIGHT)
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize - 3.toFloat())
        }
        else -> {
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            text.setText(content);
        }
    }
}

fun handleTextColorBasedOnTheme(context: Activity, text: TextView) {
    var theme = context.getVisualPref().getTheme()
    if (theme == R.style.Theme_Sherlock_Light_DarkActionBar)
        text.setTextColor(android.graphics.Color.BLACK)
    else if (theme == R.style.Theme_Sherlock)
        text.setTextColor(android.graphics.Color.WHITE)
}