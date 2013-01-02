package com.silverkeytech.android_rivers

import android.content.Intent
import android.content.Context


public fun startFeedActivityIntent(context : Context, url : String, text : String, lang : String) : Intent{
    val i = Intent(context, javaClass<FeedActivity>())
    i.putExtra(Params.FEED_URL, url)
    i.putExtra(Params.FEED_NAME, text)
    i.putExtra(Params.FEED_LANGUAGE, lang)

    return i
}

public fun startRiverActivityIntent(context: Context, url : String, text : String, lang : String) : Intent{
    val i = Intent(context, javaClass<RiverActivity>())
    i.putExtra(Params.RIVER_URL, url)
    i.putExtra(Params.RIVER_NAME, text)
    i.putExtra(Params.RIVER_LANGUAGE, lang)
    return i
}

public fun startCollectionActivityIntent(context : Context) : Intent{
    val i = Intent(context, javaClass<BookmarkCollectionActivity>())
    return i
}

public fun shareActionIntent(url : String) : Intent{
    var i = Intent(Intent.ACTION_SEND)
    i.setType("text/plain")
    i.putExtra(Intent.EXTRA_TEXT, url)
    return i
}