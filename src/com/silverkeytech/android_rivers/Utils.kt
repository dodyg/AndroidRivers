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

import android.net.ConnectivityManager
import android.view.View
import android.view.View.OnClickListener
import com.actionbarsherlock.view.MenuItem
import com.github.kevinsawicki.http.HttpRequest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import android.os.Build
import android.content.Context
import android.text.ClipboardManager
import android.support.v4.app.Fragment
import android.app.Activity
import android.webkit.URLUtil
import android.app.Dialog
import de.greenrobot.event.EventBus

object Bus {
    fun register(obj : Any){
        EventBus.getDefault()!!.register(obj)
    }

    fun unregister(obj : Any){
        EventBus.getDefault()!!.unregister(obj)
    }

    fun post (obj : Any){
        EventBus.getDefault()!!.post(obj)
    }
}


fun isLanguageRTL(language: String): Boolean {
    return when(language){
        "ar" -> true
        else -> false
    }
}

fun Fragment.tryGetUriFromClipboard(): Pair<Boolean, String?> {
    val res = this.getActivity()!!.tryGetUriFromClipboard()
    return res
}

fun Activity.tryGetUriFromClipboard(): Pair<Boolean, String?> {
    val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    if (clipboard.hasText()){
        val text = clipboard.getText().toString()
        if (URLUtil.isValidUrl(text))
            return Pair(true, text.toString())
    }
    return Pair(false, null)
}


public fun MenuItem?.andHide(): MenuItem {
    this!!.setVisible(false)
    return this
}

public fun onClickListener(action: (View) -> Unit): OnClickListener {
    return object : OnClickListener {
        public override fun onClick(p0: View) {
            action(p0)
        }
    }
}

public fun View.setOnClickListener(action: (View?) -> Unit): Unit {
    setOnClickListener(onClickListener(action))
}

public fun parseRFC3339DateFormat(dt: String): java.util.Date? {
    try {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        val dts = dt.replace("([\\+\\-]\\d\\d):(\\d\\d)".toRegex(), "$1$2")
        return formatter.parse(dts)
    } catch (e: Exception) {
        return null;
    }
}

fun imageMimeTypeToFileExtension (mimeType: String): String {
    return when(mimeType) {
        "image/gif" -> ".gif"
        "image/jpeg" -> ".jpg"
        "image/pjpeg" -> ".jpg"
        "image/png" -> ".png"
        "image/tiff" -> ".tiff"
        else -> ""
    }
}

fun isSupportedImageMime(mimeType: String): Boolean {
    return when (mimeType) {
        "image/gif" -> true
        "image/jpeg" -> true
        "image/pjpeg" -> true
        "image/png" -> true
        else -> false
    }
}

fun inMegaByte(mb: Int): Int = mb * 1024 * 1024

fun Int.toHoursInMinutes() = this * 60

public fun hoursBeforeNow(hours: Int): Date {
    val now = Calendar.getInstance()
    now.add(Calendar.HOUR, -hours)
    return now.getTime()
}

public fun daysBeforeNow(days: Int): Date {
    val hours = days * 24
    val now = Calendar.getInstance()
    now.add(Calendar.HOUR, -hours)
    return now.getTime()
}

public fun <T>T.with(operations: T.() -> Unit): T {
    this.operations()
    return this
}

fun futureTimeFromNowInMilies(seconds: Int): Long {
    return System.currentTimeMillis() + (seconds.toLong() * 1000.toLong())
}

//unsafe HTTPGet (trust all certificates and hosts)
fun httpGet(url: String): HttpRequest {
    return HttpRequest.get(url)!!
            .trustAllHosts()!!
            .trustAllCerts()!!
            .acceptGzipEncoding()!!
            .uncompress(true)!!
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.getActiveNetworkInfo()
    return activeNetworkInfo != null
}

fun isModernAndroid(): Boolean {
    return Build.VERSION.SDK_INT >= 14 //Build.VERSION_CODES.ICE_CREAM_SANDWICH
}

fun Dialog?.findView<T : View>(id : Int) : T{
    return this!!.findViewById(id) as T
}

fun View?.findView<T : View>(id : Int) : T{
    return this!!.findViewById(id) as T
}