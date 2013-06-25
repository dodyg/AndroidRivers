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

package com.silverkeytech.android_rivers.asyncs

import java.util.ArrayList
import java.util.Vector
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import com.silverkeytech.android_rivers.downloadSingleFeed
import com.silverkeytech.news_engine.riverjs.RiverItemMeta
import com.silverkeytech.news_engine.riverjs.accumulateList
import com.silverkeytech.android_rivers.Result
import java.util.Date
import com.silverkeytech.news_engine.syndications.SyndicationFilter
import android.util.Log

public fun concurrentFeedsDownload(threadPoolNumber : Int, latestDateFilter : Date, maximumItemFilter : Int,
                                   vararg urls: String?): Result<List<RiverItemMeta>>{

    val TAG = "concurrentFeedsDownload"

    var list = Vector<RiverItemMeta>()
    var executor = Executors.newFixedThreadPool(threadPoolNumber)

    for(url in urls){
        executor.execute(
                runnable {
                    if (url != null){
                        val res = downloadSingleFeed(url, SyndicationFilter(maximumItemFilter, latestDateFilter))

                        if (res.isFalse())
                            Log.d(TAG, "Value at ${res.exception?.getMessage()}")
                        else
                        {
                            Log.d(TAG, "Download for $url is successful")
                            accumulateList(list, res.value!!)
                        }
                    }
                }
        )
    }

    executor.shutdown()

    try{
        if (!executor.awaitTermination(20.toLong(), TimeUnit.SECONDS))
            executor.shutdownNow()

        return Result.right(ArrayList(list))
    }
    catch(ex: InterruptedException){
        executor.shutdownNow()
        return Result.wrong<List<RiverItemMeta>>(ex)
    }
}
