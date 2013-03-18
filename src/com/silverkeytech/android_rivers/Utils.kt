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

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.view.View.OnClickListener
import com.actionbarsherlock.view.MenuItem
import com.github.kevinsawicki.http.HttpRequest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import org.holoeverywhere.app.Fragment
import android.os.Build


public fun MenuItem?.andHide(): MenuItem {
    this!!.setVisible(false)
    return this
}

public fun OnClickListener(action: (View?) -> Unit): OnClickListener {
    return object : OnClickListener {
        public override fun onClick(p0: View?) {
            action(p0)
        }
    }
}

public fun View.setOnClickListener(action: (View?) -> Unit): Unit {
    setOnClickListener(OnClickListener(action))
}

public fun parseRFC3339DateFormat(dt: String): java.util.Date? {
    try {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        val dts = dt.replaceAll("([\\+\\-]\\d\\d):(\\d\\d)", "$1$2")
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

fun httpGet(url: String): HttpRequest {
    return HttpRequest.get(url)!!
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

/*
public Intent findTwitterClient() {
final String[] twitterApps = {
// package // name - nb installs (thousands)
"com.twitter.android", // official - 10 000
"com.twidroid", // twidroid - 5 000
"com.handmark.tweetcaster", // Tweecaster - 5 000
"com.thedeck.android" }; // TweetDeck - 5 000 };
Intent tweetIntent = new Intent();
tweetIntent.setType("text/plain");
final PackageManager packageManager = getPackageManager();
List<ResolveInfo> list = packageManager.queryIntentActivities(
        tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

for (int i = 0; i < twitterApps.length; i++) {
for (ResolveInfo resolveInfo : list) {
String p = resolveInfo.activityInfo.packageName;
if (p != null && p.startsWith(twitterApps[i])) {
tweetIntent.setPackage(p);
return tweetIntent;
}
}
}
return null;

}
*/