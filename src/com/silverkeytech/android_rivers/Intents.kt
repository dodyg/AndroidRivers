package com.silverkeytech.android_rivers

import android.content.Context
import android.content.Intent
import com.silverkeytech.android_rivers.outliner.OutlineContent
import java.util.ArrayList

public fun startPodcastActivity(context : Context, downloadLocationPath : String? = null){
    val i = Intent(context, javaClass<PodcastManagerActivity>())
    if (!downloadLocationPath.isNullOrEmpty())
        i.putExtra(Params.DOWNLOAD_LOCATION_PATH, downloadLocationPath!!)

    context.startActivity(i)
}

public fun startFeedActivityIntent(context: Context, url: String, text: String, lang: String) {
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

/*
public fun startDownloadAllRiverService(context: Context, titleList: ArrayList<String>, urlList: ArrayList<String>) {
    val i = Intent(context, javaClass<DownloadAllRiversService>())

    i.putStringArrayListExtra(Params.RIVERS_DOWNLOAD_TITLE, titleList)
    i.putStringArrayListExtra(Params.RIVERS_DOWNLOAD_URLS, urlList)

    context.startActivity(i)
}*/

public fun startCollectionActivityIntent(context: Context, id: Int, title: String) {
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