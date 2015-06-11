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
import android.content.Intent
import android.net.Uri
import android.os.Messenger
import com.silverkeytech.news_engine.outliner.OutlineContent
import java.util.ArrayList
import com.silverkeytech.android_rivers.meta_weblog.BlogPostService
import java.util.HashMap
import com.silverkeytech.android_rivers.creators.GoogleNewsSearchActivity
import com.silverkeytech.android_rivers.creators.KayakFlightDealsActivity
import com.silverkeytech.android_rivers.creators.CraigslistListingActivity
import com.silverkeytech.android_rivers.activities.RiverSourcesActivity
import com.silverkeytech.android_rivers.activities.FeedActivity
import com.silverkeytech.android_rivers.activities.RiverActivity
import com.silverkeytech.android_rivers.activities.OutlinerActivity
import com.silverkeytech.android_rivers.activities.TryOutActivity
import com.silverkeytech.android_rivers.activities.BookmarkCollectionActivity
import com.silverkeytech.android_rivers.services.DownloadService
import com.silverkeytech.android_rivers.services.ImportOpmlSubscriptionListService
import com.silverkeytech.android_rivers.services.DownloadAllRiversService

public fun startCraigslistListingActivity(context: Context) {
    val i = Intent(context, javaClass<CraigslistListingActivity>())
    context.startActivity(i)
}

public fun startKayakFlightDealsActivity(context: Context) {
    val i = Intent(context, javaClass<KayakFlightDealsActivity>())
    context.startActivity(i)
}

public fun startGoogleNewsSearchActivity(context: Context) {
    val i = Intent(context, javaClass<GoogleNewsSearchActivity>())
    context.startActivity(i)
}

public fun startRiverSourcesActivity(context: Context, riverTitle: String, riverUri: String, sourcesTitles: ArrayList<String>, sourcesUris: ArrayList<String>) {
    val i = Intent(context, javaClass<RiverSourcesActivity>())

    i.putExtra(Params.RIVER_SOURCES_RIVER_TITLE, riverTitle)
    i.putExtra(Params.RIVER_SOURCES_RIVER_URI, riverUri)
    i.putStringArrayListExtra(Params.RIVER_SOURCES_TITLES, sourcesTitles)
    i.putStringArrayListExtra(Params.RIVER_SOURCES_URIS, sourcesUris)

    context.startActivity(i)
}

public fun startBlogPostingService(context: Context, config: HashMap<String, String>, post: HashMap<String, String>) {
    val i = Intent(context, javaClass<BlogPostService>())
    i.putExtra(Params.BLOG_CONFIGURATION, config)
    i.putExtra(Params.BLOG_PAYLOAD, post)
    context.startService(i)
}

public fun startDownloadService(context: Context, title: String, url: String, sourceTitle: String, sourceUrl: String, description: String, messenger: Messenger) {
    val i = Intent(context, javaClass<DownloadService>())
    i.putExtra(Params.DOWNLOAD_TITLE, title)
    i.putExtra(Params.DOWNLOAD_URL, url)
    i.putExtra(Params.DOWNLOAD_SOURCE_TITLE, sourceTitle)
    i.putExtra(Params.DOWNLOAD_SOURCE_URL, sourceUrl)
    i.putExtra(Params.DOWNLOAD_DESCRIPTION, description)
    i.putExtra(Params.MESSENGER, messenger)
    context.startService(i)
}

public fun startImportOpmlSubscriptionService(context: Context, url: String) {
    val i = Intent(context, javaClass<ImportOpmlSubscriptionListService>())
    i.putExtra(Params.OPML_SUBSCRIPTION_LIST_URI, url)
    context.startService(i)
}

public fun startFeedActivity(context: Context, url: String, text: String, lang: String) {
    val i = Intent(context, javaClass<FeedActivity>())
    i.putExtra(Params.FEED_URL, url)
    i.putExtra(Params.FEED_NAME, text)
    i.putExtra(Params.FEED_LANGUAGE, lang)

    context.startActivity(i)
}

public fun startTryoutActivity(context: Context) {
    val i = Intent(context, javaClass<TryOutActivity>())
    context.startActivity(i)
}

public fun startRiverActivity(context: Context, url: String, text: String, lang: String) {
    val i = Intent(context, javaClass<RiverActivity>())
    i.putExtra(Params.RIVER_URL, url)
    i.putExtra(Params.RIVER_NAME, text)
    i.putExtra(Params.RIVER_LANGUAGE, lang)

    context.startActivity(i)
}

public fun startDownloadAllRiverService(context: Context, titleList: ArrayList<String>, urlList: ArrayList<String>) {
    val i = Intent(context, javaClass<DownloadAllRiversService>())

    i.putStringArrayListExtra(Params.RIVERS_DOWNLOAD_TITLE, titleList)
    i.putStringArrayListExtra(Params.RIVERS_DOWNLOAD_URLS, urlList)

    context.startService(i)
}

public fun startCollectionActivity(context: Context, id: Int, title: String) {
    val i = Intent(context, javaClass<BookmarkCollectionActivity>())
    i.putExtra(Params.COLLECTION_ID, id)
    i.putExtra(Params.COLLECTION_TITLE, title)

    context.startActivity(i)
}

fun startOutlinerActivity(context: Context, outlines: ArrayList<OutlineContent>, title: String, url: String?, expandAll: Boolean) {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.setClass(context, javaClass<OutlinerActivity>())
    intent.putExtra(Params.OUTLINES_DATA, outlines)
    intent.putExtra(Params.OUTLINES_TITLE, title)
    intent.putExtra(Params.OUTLINES_URL, url)
    intent.putExtra(Params.OUTLINES_EXPAND_ALL, expandAll)
    context.startActivity(intent)
}

/*   http://stackoverflow.com/questions/11752081/is-there-any-way-to-query-an-specific-type-of-intent-filter-capable-apps
           http://stackoverflow.com/questions/3935009/how-to-open-gmail-compose-when-a-button-is-clicked-in-android-app
*/

public fun shareActionIntent(title: String, url: String): Intent {
    var i = Intent(Intent.ACTION_SEND)
    i.setType("text/plain")
    i.putExtra(Intent.EXTRA_TEXT, "${title.limitText(PreferenceDefaults.LINK_SHARE_TITLE_MAX_LENGTH)} $url")
    return i
}

public fun startOpenBrowserActivity(context: Context, url: String) {
    val i = Intent("android.intent.action.VIEW", Uri.parse(url))
    context.startActivity(i)

    /*
    val intent = Intent(Intent.ACTION_MAIN)
    intent.setClass(context, javaClass<WebViewActivity>())
    intent.putExtra(Params.BUILT_IN_BROWSER_URI, url)
    context.startActivity(intent)
    */
}

public fun startOpenEmailActivity(context: Context, email: String, subject: String, body: String) {
    val i = Intent(Intent.ACTION_SEND)

    i.setType("message/rfc822")
    i.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf(email))
    i.putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
    i.putExtra(android.content.Intent.EXTRA_TEXT, body)

    context.startActivity(Intent.createChooser(i, "Send mail..."))
}