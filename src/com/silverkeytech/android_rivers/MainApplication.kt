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

import android.app.Application
import android.support.v4.util.LruCache
import android.util.Log
import com.silverkeytech.android_rivers.outlines.Opml
import com.silverkeytech.android_rivers.riverjs.FeedItemMeta
import com.silverkeytech.android_rivers.outliner.OutlineContent
import java.util.ArrayList

fun Application?.getMain(): MainApplication {
    return this!! as MainApplication
}

public class MainApplication(): Application()
{
    class object {
        public val TAG: String = javaClass<MainApplication>().getSimpleName()
    }

    var riverCache = LruCache<String, CacheItem<List<FeedItemMeta>>>(inMegaByte(4))
    var opmlCache =  LruCache<String, CacheItem<ArrayList<OutlineContent>>>(inMegaByte(4))
    var subscriptionCache: CacheItem<Opml>? = null

    public override fun onCreate() {
        Log.d(TAG, "Main Application is started")
        super<Application>.onCreate()
    }

    public fun getSubscriptionListCache(): Opml? {
        if (subscriptionCache == null)
            return null
        else{
            if (subscriptionCache!!.isExpired){
                subscriptionCache = null
                return null
            } else {
                return subscriptionCache?.item
            }
        }
    }

    public fun setSubscriptionListCache(opml: Opml) {
        subscriptionCache = CacheItem(opml)
        subscriptionCache?.setExpireInMinutesFromNow(10.toHoursInMinutes())
    }

    public fun getOpmlCache(uri: String): ArrayList<OutlineContent>?{
        val content = opmlCache.get(uri)

        if (content == null)
            return null
        else{
            if (content.isExpired){
                opmlCache.remove(uri)
                return null
            }else{
                return content.item
            }
        }
    }

    public fun setOpmlCache(uri: String, opml: ArrayList<OutlineContent>, expirationInMinutes : Int =  30) {
        var item = CacheItem(opml)
        item.setExpireInMinutesFromNow(expirationInMinutes)
        opmlCache.put(uri, item)
    }

    public fun getRiverCache (uri: String): List<FeedItemMeta>? {
        val content = riverCache.get(uri)

        if (content == null)
            return null
        else{
            if (content.isExpired){
                riverCache.remove(uri)
                return null
            }else{
                return content.item
            }
        }
    }

    public fun setRiverCache(uri: String, river: List<FeedItemMeta>, expirationInMinutes : Int =  30) {
        var item = CacheItem(river)
        item.setExpireInMinutesFromNow(expirationInMinutes)
        riverCache.put(uri, item)
    }

    public override fun onLowMemory() {
        riverCache.evictAll();
    }
}