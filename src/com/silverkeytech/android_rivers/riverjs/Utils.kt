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

package com.silverkeytech.android_rivers.riverjs

import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import com.google.gson.Gson
import com.silverkeytech.android_rivers.DateHelper
import com.silverkeytech.android_rivers.Result
import com.silverkeytech.android_rivers.httpGet
import com.silverkeytech.android_rivers.scrubJsonP
import com.silverkeytech.news_engine.syndications.SyndicationFeed
import java.util.Vector
import android.util.Log
import com.silverkeytech.news_engine.riverjs.RiverItemMeta
import com.silverkeytech.news_engine.riverjs.RiverEnclosure
import com.silverkeytech.news_engine.riverjs.RiverItemSource
import com.silverkeytech.news_engine.riverjs.River
import com.silverkeytech.news_engine.riverjs.RiverItem

fun downloadSingleRiver(url: String): Result<River> {
    var req: String?
    try{
        req = httpGet(url).body()
    }
    catch(e: HttpRequestException){
        val ex = e.getCause()
        return Result.wrong(ex)
    }

    try{
        val scrubbed = scrubJsonP(req!!)
        val feeds = Gson().fromJson(scrubbed, javaClass<River>())!!

        return Result.right(feeds)
    }
    catch(e: Exception)
    {
        Log.d("downloadSingleRiver", "${e.getMessage()} ${e.getCause()?.getMessage()}")
        return Result.wrong(e)
    }
}