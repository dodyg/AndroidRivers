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

package com.silverkeytech.android_rivers.services

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.RemoteViews
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import com.google.gson.Gson
import com.silverkeytech.android_rivers.db.getBookmarksUrlsFromDbByCollection
import com.silverkeytech.news_engine.riverjs.River
import com.silverkeytech.news_engine.riverjs.RiverItemMeta
import com.silverkeytech.news_engine.syndications.SyndicationFilter
import com.silverkeytech.android_rivers.downloadSingleFeed
import java.util.Random
import java.util.Vector
import android.content.res.Resources
import com.silverkeytech.news_engine.riverjs.accumulateList
import com.silverkeytech.news_engine.riverjs.sortRiverItemMeta
import com.silverkeytech.news_engine.riverjs.getSortedNewsItems
import com.silverkeytech.android_rivers.activities.MainWithFragmentsActivity
import com.silverkeytech.android_rivers.activities.getMain
import com.silverkeytech.android_rivers.R
import com.silverkeytech.android_rivers.PreferenceDefaults
import com.silverkeytech.android_rivers.Params
import com.silverkeytech.android_rivers.with
import com.silverkeytech.android_rivers.isModernAndroid
import com.silverkeytech.android_rivers.httpGet
import com.silverkeytech.android_rivers.scrubJsonP
import com.silverkeytech.android_rivers.isLocalUrl
import com.silverkeytech.android_rivers.daysBeforeNow
import com.silverkeytech.android_rivers.extractIdFromLocalUrl
import com.silverkeytech.android_rivers.toHoursInMinutes


public class DownloadAllRiversService(): IntentService("DownloadAllRiversService"){
    companion object{
        public val TAG: String = javaClass<DownloadAllRiversService>().getSimpleName()
    }

    var targetUrls: List<String?> ? = null
    var targetTitles: List<String?> ? = null

    fun prepareNotification(): Notification {
        val notificationIntent = Intent(Intent.ACTION_MAIN)
        notificationIntent.setClass(getApplicationContext()!!, javaClass<MainWithFragmentsActivity>())

        val contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val notification = NotificationCompat.Builder(this)
                .setTicker(this.getString(R.string.start_downloading_all_rivers))
        ?.setWhen(System.currentTimeMillis())
        ?.setContentIntent(contentIntent)
        ?.build()

        notification!!.icon = android.R.drawable.stat_sys_download

        val remote = RemoteViews(getApplicationContext()!!.getPackageName(), R.layout.notification_download_progress).with {
            this.setImageViewResource(R.id.notification_download_progress_status_icon, android.R.drawable.stat_sys_download_done)
            this.setProgressBar(R.id.notification_download_progress_status_progress, 100, 0, false)
            this.setTextViewText(R.id.notification_download_progress_status_text, getString(R.string.download_starts))
        }

        if (isModernAndroid()){
            //workaround on grey background on Android 4.03   https://code.google.com/p/android/issues/detail?id=23863&thanks=23863&ts=1325611036
            val id = Resources.getSystem()!!.getIdentifier("status_bar_latest_event_content", "id", "android")
            notification.contentView?.removeAllViews(id)
            notification.contentView!!.addView(id, remote)
        }
        else
            notification.contentView = remote

        return notification
    }

    protected override fun onHandleIntent(p0: Intent?) {
        targetTitles = p0?.getStringArrayListExtra(Params.RIVERS_DOWNLOAD_TITLE)
        targetUrls = p0?.getStringArrayListExtra(Params.RIVERS_DOWNLOAD_URLS)

        Log.d(TAG, "Getting parcel successfully ${targetTitles?.count()}")

        val notificationId = Random().nextLong().toInt()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var notification = prepareNotification()
        var riverNumber = 0
        val riverTotal = targetUrls!!.count()
        var progress: Int
        var errorCount = 0

        fun updateText(msg: String) {
            notification.contentView!!.setTextViewText(R.id.notification_download_progress_status_text, msg)
        }

        fun updateProgress(sofar: Int) {
            notification.contentView!!.setProgressBar(R.id.notification_download_progress_status_progress, 100, sofar, false)
        }

        fun notify() {
            notificationManager.notify(notificationId, notification)
        }

        for(url in targetUrls?.iterator()){
            if (url != null){
                var successful = false

                //take care of notification
                progress = (riverNumber * 100) div riverTotal

                val title = targetTitles?.get(riverNumber)

                updateText("Downloading $title ")
                updateProgress(progress)
                notify()

                //do the river remote download
                var req: String? = null

                if (!isLocalUrl(url)){
                    try{
                        req = httpGet(url).body()
                        successful = true
                    }
                    catch(e: HttpRequestException){
                        val ex = e.getCause()
                        Log.d(TAG, "Error while trying to download $url with error message ${ex?.getMessage()}")

                        updateText("Problem downloading $title ")
                        notify()
                        errorCount++
                    }

                    if (successful){
                        try{
                            val scrubbed = scrubJsonP(req!!)
                            val feeds = Gson().fromJson(scrubbed, javaClass<River>())!!

                            val sortedNewsItems = feeds.getSortedNewsItems()

                            getMain().setRiverCache(url, sortedNewsItems, 3.toHoursInMinutes())
                            Log.d(TAG, "Download for $url is successful")
                        }
                        catch(e: Exception)
                        {
                            updateText("Problem parsing $title ")
                            notify()
                            Log.d(TAG, "Error while trying to parse $url")
                            errorCount++
                        }
                    }
                }
                else {
                    val latestDate = daysBeforeNow(PreferenceDefaults.CONTENT_BOOKMARK_COLLECTION_LATEST_DATE_FILTER_IN_DAYS)

                    val id = extractIdFromLocalUrl(url)
                    if (id != null){
                        Log.d(TAG, "Downloading bookmark collection $id")

                        val urls = getBookmarksUrlsFromDbByCollection(id)

                        val list = Vector<RiverItemMeta>()
                        for(u in urls){
                            val res = downloadSingleFeed(u, SyndicationFilter(PreferenceDefaults.CONTENT_BOOKMARK_COLLECTION_MAX_ITEMS_FILTER, latestDate))

                            if (res.isFalse())
                                Log.d(TAG, "Value at ${res.exception?.getMessage()}")
                            else
                            {
                                Log.d(TAG, "Download for $u is successful")
                                accumulateList(list, res.value!!)
                            }
                        }

                        if (list.count() > 0){
                            val sortedNewsItems = sortRiverItemMeta(list)
                            getMain().setRiverCache(url, sortedNewsItems, 3.toHoursInMinutes())
                        }
                    }
                    else{
                        updateText("Problem downloading collection river $title due to id extraction failure")
                        notify()
                        Log.d(TAG, "Cannot extract id from $url")
                    }
                }

                riverNumber++
            }
        }

        var msg: String

        if (errorCount == 0){
            msg = getString(R.string.all_rivers_successfully_downloaded)
        }
        else{
            msg = "${riverTotal - errorCount} are successfully downloaded out of ${riverTotal}"
        }

        updateText(msg)
        updateProgress(100)
        notify()
    }

    public override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
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