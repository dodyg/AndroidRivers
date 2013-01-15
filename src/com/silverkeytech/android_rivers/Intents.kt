package com.silverkeytech.android_rivers

import android.content.Context
import android.content.Intent
import com.silverkeytech.android_rivers.outliner.OutlineContent
import java.util.ArrayList
import android.os.Messenger


public fun startRiverSourcesActivity(context: Context, riverTitle : String, riverUri : String, sourcesTitles: ArrayList<String>, sourcesUris: ArrayList<String>) {
    val i = Intent(context, javaClass<RiverSourcesActivity>())

    i.putExtra(Params.RIVER_SOURCES_RIVER_TITLE, riverTitle)
    i.putExtra(Params.RIVER_SOURCES_RIVER_URI, riverUri)
    i.putStringArrayListExtra(Params.RIVER_SOURCES_TITLES, sourcesTitles)
    i.putStringArrayListExtra(Params.RIVER_SOURCES_URIS, sourcesUris)

    context.startActivity(i)
}

public fun startDownloadService(context : Context, title : String, url : String, sourceTitle : String, sourceUrl : String, description : String, messenger : Messenger){
    var i = Intent(context, javaClass<DownloadService>())
    i.putExtra(Params.DOWNLOAD_TITLE, title)
    i.putExtra(Params.DOWNLOAD_URL, url)
    i.putExtra(Params.DOWNLOAD_SOURCE_TITLE, sourceTitle)
    i.putExtra(Params.DOWNLOAD_SOURCE_URL, sourceUrl)
    i.putExtra(Params.DOWNLOAD_DESCRIPTION, description)
    i.putExtra(Params.MESSENGER, messenger)
    context.startService(i)
}

public fun startPodcastActivity(context : Context, downloadLocationPath : String? = null){
    val i = Intent(context, javaClass<PodcastManagerActivity>())
    if (!downloadLocationPath.isNullOrEmpty())
        i.putExtra(Params.DOWNLOAD_LOCATION_PATH, downloadLocationPath!!)

    context.startActivity(i)
}

public fun startFeedActivity(context: Context, url: String, text: String, lang: String) {
    val i = Intent(context, javaClass<FeedActivity>())
    i.putExtra(Params.FEED_URL, url)
    i.putExtra(Params.FEED_NAME, text)
    i.putExtra(Params.FEED_LANGUAGE, lang)

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
    var intent = Intent(Intent.ACTION_MAIN)
    intent.setClass(context, javaClass<OutlinerActivity>())
    intent.putExtra(Params.OUTLINES_DATA, outlines)
    intent.putExtra(Params.OUTLINES_TITLE, title)
    intent.putExtra(Params.OUTLINES_URL, url)
    intent.putExtra(Params.OUTLINES_EXPAND_ALL, expandAll)
    context.startActivity(intent)
}

public fun shareActionIntent(url: String): Intent {
    var i = Intent(Intent.ACTION_SEND)
    i.setType("text/plain")
    i.putExtra(Intent.EXTRA_TEXT, url)
    return i
}