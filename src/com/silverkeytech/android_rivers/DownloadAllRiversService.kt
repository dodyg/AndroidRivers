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

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.RemoteViews
import com.github.kevinsawicki.http.HttpRequest
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import com.google.gson.Gson
import com.silverkeytech.android_rivers.riverjs.FeedsRiver
import java.util.Random

public class DownloadAllRiversService(): IntentService("DownloadAllRiversService"){
    class object{
        public val TAG: String = javaClass<DownloadAllRiversService>().getSimpleName()

        public val PARCEL: String = "Parcel"
        public val TITLES: String = "TITLES"
        public val URLS: String = "URLS"
    }

    var targetUrls: List<String?> ? = null
    var targetTitles: List<String?> ? = null


    fun prepareNotification(): Notification {
        var notificationIntent = Intent(Intent.ACTION_MAIN)
        notificationIntent.setClass(getApplicationContext(), javaClass<MainActivity>())

        var contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        var notification = NotificationCompat.Builder(this)
                .setTicker("Start downloading all rivers")
        ?.setWhen(System.currentTimeMillis())
        ?.setContentIntent(contentIntent)
        ?.build()

        notification!!.icon = android.R.drawable.star_big_on

        notification!!.contentView = RemoteViews(getApplicationContext()!!.getPackageName(), R.layout.download_progress)

        notification!!.contentView!!.setImageViewResource(R.id.download_progress_status_icon, android.R.drawable.btn_star);
        notification!!.contentView!!.setProgressBar(R.id.download_progress_status_progress, 100, 0, false)
        notification!!.contentView!!.setTextViewText(R.id.download_progress_status_text, "Downloading starts ")

        return notification!!
    }


    protected override fun onHandleIntent(p0: Intent?) {
        targetTitles = p0?.getStringArrayListExtra(TITLES)
        targetUrls = p0?.getStringArrayListExtra(URLS)

        Log.d(TAG, "Getting parcel successfully ${targetTitles?.count()}")

        val notificationId = Random().nextLong().toInt()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var notification = prepareNotification()
        var riverNumber = 0
        val riverTotal = targetUrls!!.count()
        var progress: Int
        var errorCount = 0

        for(val url in targetUrls?.iterator()){
            if (url != null){

                var successful = false

                //take care of notification
                progress = (riverNumber * 100) div riverTotal

                var title = targetTitles?.get(riverNumber)

                notification.contentView!!.setTextViewText(R.id.download_progress_status_text, "Downloading $title ")
                notification.contentView!!.setProgressBar(R.id.download_progress_status_progress, 100, progress, false)
                notificationManager.notify(notificationId, notification)

                //do the downloading
                var req: String? = null
                try{
                    req = HttpRequest.get(url)?.body()
                    successful = true
                }
                catch(e: HttpRequestException){
                    val ex = e.getCause()
                    Log.d(TAG, "Error while trying to download $url")

                    notification.contentView!!.setTextViewText(R.id.download_progress_status_text, "Problem downloading $title ")
                    notificationManager.notify(notificationId, notification)
                    errorCount++
                }

                if (successful){
                    try{
                        val scrubbed = scrubJsonP(req!!)
                        val gson = Gson()
                        val feeds = gson.fromJson(scrubbed, javaClass<FeedsRiver>())!!

                        var sortedNewsItems = feeds.getSortedNewsItems()

                        getApplication().getMain().setRiverCache(url, sortedNewsItems, 3.toHoursInMinutes())
                        Log.d(TAG, "Download for $url is successful")
                    }
                    catch(e: Exception)
                    {
                        notification.contentView!!.setTextViewText(R.id.download_progress_status_text, "Problem parsing $title ")
                        notificationManager.notify(notificationId, notification)
                        Log.d(TAG, "Error while trying to parse $url")
                        errorCount++
                    }
                }

                riverNumber++
            }
        }

        var msg: String

        if (errorCount == 0){
            msg = "All rivers successfully downloaded"
        }
        else{
            msg = "${riverTotal - errorCount} are successfully downloaded out of ${riverTotal}"
        }

        notification.contentView!!.setTextViewText(R.id.download_progress_status_text, msg)
        notification.contentView!!.setProgressBar(R.id.download_progress_status_progress, 100, 100, false)
        notificationManager.notify(notificationId, notification)
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