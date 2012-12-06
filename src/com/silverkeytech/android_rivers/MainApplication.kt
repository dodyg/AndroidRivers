package com.silverkeytech.android_rivers

import android.app.Application
import android.support.v4.util.LruCache
import com.silverkeytech.android_rivers.riverjs.FeedsRiver
import com.silverkeytech.android_rivers.outlines.Opml
import android.util.Log
import java.util.ArrayList
import com.silverkeytech.android_rivers.riverjs.FeedItemMeta
import android.content.Context

fun Application?.getMain() : MainApplication{
    return this!! as MainApplication
}

public class MainApplication() : Application()
{
    class object {
        public val TAG: String = javaClass<MainApplication>().getSimpleName()
    }

    var riverCache : LruCache<String, CacheItem<List<FeedItemMeta>>>  = LruCache<String, CacheItem<List<FeedItemMeta>>>(inMegaByte(4))
    var subscriptionCache : CacheItem<Opml>? = null

    public override fun onCreate() {
        Log.d(TAG, "Main Application is started")
        super<Application>.onCreate()
    }

    public fun getSubscriptionListCache() : Opml? {
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

    public fun setSubscriptionListCache(opml : Opml){
        subscriptionCache = CacheItem(opml)
        subscriptionCache?.setExpireInMinutesFromNow(600)
    }

    public fun getRiverCache (uri : String) : List<FeedItemMeta>? {
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

    public fun setRiverCache(uri : String, river : List<FeedItemMeta>){
        var item = CacheItem(river)
        item.setExpireInMinutesFromNow(15)
        riverCache.put(uri, item)
    }

    public override fun onLowMemory() {
        riverCache.evictAll();
    }
}