package com.silverkeytech.android_rivers

import android.app.Application
import android.support.v4.util.LruCache
import com.silverkeytech.android_rivers.riverjs.FeedsRiver

fun Application?.getMain() : MainApplication{
    return this!! as MainApplication
}

public class MainApplication() : Application()
{
    var riverCache : LruCache<String, CacheItem<FeedsRiver>>  = LruCache<String, CacheItem<FeedsRiver>>(inMegaByte(4))

    public override fun onCreate() {
        super<Application>.onCreate()
    }

    public fun getRiverCache (uri : String) : FeedsRiver? {
        var content = riverCache.get(uri)

        if (content == null)
            return null
        else{
            if (content!!.isExpired){
                riverCache.remove(uri)
                return null
            }else{
                return content?.item
            }
        }
    }

    public fun setRiverCache(uri : String, river : FeedsRiver){
        var item = CacheItem(river)
        item.setExpireInSecondsFromNow(15)
        riverCache.put(uri, item)
    }

    public override fun onLowMemory() {
        riverCache.evictAll();
    }
}