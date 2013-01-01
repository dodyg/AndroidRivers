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
import android.content.Context
import android.support.v4.util.LruCache
import android.util.Log
import com.silverkeytech.android_rivers.db.Database
import com.silverkeytech.android_rivers.db.DatabaseManager
import com.silverkeytech.android_rivers.outliner.OutlineContent
import com.silverkeytech.android_rivers.outlines.Opml
import com.silverkeytech.android_rivers.riverjs.RiverItemMeta
import java.util.ArrayList

fun Application?.getMain(): MainApplication {
    return this!! as MainApplication
}

public class MainApplication(): Application()
{
    class object {
        public val TAG: String = javaClass<MainApplication>().getSimpleName()
    }

    var riverCache = LruCache<String, CacheItem<List<RiverItemMeta>>>(inMegaByte(4))
    var opmlCache = LruCache<String, CacheItem<ArrayList<OutlineContent>>>(inMegaByte(4))
    var riverBookmarksCache: CacheItem<Opml>? = null

    public override fun onCreate() {
        Log.d(TAG, "Main Application is started")
        super<Application>.onCreate()

        val path = this.getApplicationContext()!!.getDatabasePath(Database.DATABASE_NAME)!!
        Log.d(TAG, "My location ${path.canonicalPath}")

        //if (path.exists())
          //  path.delete()// for development only

        if (true){
            var db = this.openOrCreateDatabase(Database.DATABASE_NAME, Context.MODE_PRIVATE, null)
            db?.close()
            Log.d(TAG, "Create DB")
        }else{
            Log.d(TAG, "DB already exists")
        }

        DatabaseManager.init(this.getApplicationContext()!!)
    }

    public fun getRiverBookmarksCache(): Opml? {
        if (riverBookmarksCache == null)
            return null
        else{
            if (riverBookmarksCache!!.isExpired){
                riverBookmarksCache = null
                return null
            } else {
                return riverBookmarksCache?.item
            }
        }
    }

    public fun clearRiverBookmarksCache() {
        riverBookmarksCache = null
    }

    public fun setRiverBookmarksCache(opml: Opml) {
        riverBookmarksCache = CacheItem(opml)
        riverBookmarksCache?.setExpireInMinutesFromNow(10.toHoursInMinutes())
    }

    public fun getOpmlCache(uri: String): ArrayList<OutlineContent>? {
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

    public fun setOpmlCache(uri: String, opml: ArrayList<OutlineContent>, expirationInMinutes: Int = 30) {
        var item = CacheItem(opml)
        item.setExpireInMinutesFromNow(expirationInMinutes)
        opmlCache.put(uri, item)
    }

    public fun getRiverCache (uri: String): List<RiverItemMeta>? {
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

    public fun setRiverCache(uri: String, river: List<RiverItemMeta>, expirationInMinutes: Int = 30) {
        var item = CacheItem(river)
        item.setExpireInMinutesFromNow(expirationInMinutes)
        riverCache.put(uri, item)
    }

    public override fun onLowMemory() {
        riverCache.evictAll();
        opmlCache.evictAll()
    }
}