package com.silverkeytech.android_rivers

import android.content.Intent
import android.content.Context


public fun startFeedActivity(context : Context, url : String, text : String, lang : String) : Intent{
    val i = Intent(context, javaClass<FeedActivity>())
    i.putExtra(Params.FEED_URL, url)
    i.putExtra(Params.FEED_NAME, text)
    i.putExtra(Params.FEED_LANGUAGE, lang)

    return i
}

public fun startRiverActivity(context: Context, url : String, text : String, lang : String) : Intent{
    val i = Intent(context, javaClass<RiverActivity>())
    i.putExtra(Params.RIVER_URL, url)
    i.putExtra(Params.RIVER_NAME, text)
    i.putExtra(Params.RIVER_LANGUAGE, lang)

    return i
}