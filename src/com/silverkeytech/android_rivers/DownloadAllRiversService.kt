package com.silverkeytech.android_rivers

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.github.kevinsawicki.http.HttpRequest
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import com.silverkeytech.android_rivers.riverjs.FeedsRiver
import com.google.gson.Gson

public class DownloadAllRiversService(): IntentService("DownloadAllRiversService"){
    class object{
        public val TAG: String = javaClass<DownloadAllRiversService>().getSimpleName()

        public val PARCEL : String = "Parcel"
        public val TITLES : String = "TITLES"
        public val URLS : String = "URLS"
    }

    var targetUrls: List<String?> ? = null
    var targetTitles : List<String?> ? = null

    protected override fun onHandleIntent(p0: Intent?) {
        targetTitles = p0?.getStringArrayListExtra(TITLES)
        targetUrls = p0?.getStringArrayListExtra(URLS)

        Log.d(TAG, "Getting parcel successfully ${targetTitles?.count()}")


        val gson = Gson()

        for(val url in targetUrls?.iterator()){
            if (url != null){
                var req: String? = null
                try{
                    req = HttpRequest.get(url)?.body()
                }
                catch(e: HttpRequestException){
                    val ex = e.getCause()
                    Log.d(TAG, "Error while trying to download $url")
                }

                try{
                    val scrubbed = scrubJsonP(req!!)
                    val feeds = gson.fromJson(scrubbed, javaClass<FeedsRiver>())!!

                    var sortedNewsItems = feeds.getSortedNewsItems()

                    getApplication().getMain().setRiverCache(url, sortedNewsItems)
                    Log.d(TAG, "Download for $url is successful")
                }
                catch(e: Exception)
                {
                    Log.d(TAG, "Error while trying to parse $url")
                }
            }
        }

    }

    public override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "OnStartCommand ")

        return super<IntentService>.onStartCommand(intent, flags, startId)
    }

    public override fun onCreate() {
        super<IntentService>.onCreate()
        Log.d(TAG, "Service created")
    }

    public override fun onStart(intent: Intent?, startId: Int) {
        super<IntentService>.onStart(intent, startId)
        Log.d(TAG, "Service started ")
    }

    public override fun onDestroy() {
        super<IntentService>.onDestroy()
        Log.d(TAG, "Service destroyed ")
    }
}