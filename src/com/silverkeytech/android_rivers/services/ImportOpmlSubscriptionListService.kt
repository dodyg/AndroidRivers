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
import com.silverkeytech.android_rivers.db.BookmarkKind
import com.silverkeytech.android_rivers.db.checkIfUrlAlreadyBookmarked
import com.silverkeytech.android_rivers.db.saveBookmarkToDb
import com.silverkeytech.news_engine.transformXmlToOpml
import com.silverkeytech.news_engine.outlines.Outline
import com.silverkeytech.android_rivers.downloadSingleFeed
import java.util.Random
import android.content.res.Resources
import com.silverkeytech.android_rivers.activities.MainWithFragmentsActivity
import com.silverkeytech.android_rivers.R
import com.silverkeytech.android_rivers.with
import com.silverkeytech.android_rivers.Params
import com.silverkeytech.android_rivers.isModernAndroid
import com.silverkeytech.android_rivers.httpGet

public class ImportOpmlSubscriptionListService: IntentService("ImportOpmlSubscriptionListService"){
    companion object{
        public val TAG: String = javaClass<ImportOpmlSubscriptionListService>().getSimpleName()
    }

    var targetUrl: String ? = null

    fun prepareNotification(): Notification {
        val notificationIntent = Intent(Intent.ACTION_MAIN)
        notificationIntent.setClass(getApplicationContext()!!, javaClass<MainWithFragmentsActivity>())

        val contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val notification = NotificationCompat.Builder(this)
                .setTicker(this.getString(R.string.start_importing_opml_subscription_list))
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
        } else
            notification.contentView = remote

        return notification
    }

    protected override fun onHandleIntent(p0: Intent?) {
        targetUrl = p0?.getStringExtra(Params.OPML_SUBSCRIPTION_LIST_URI)

        val notificationId = Random().nextLong().toInt()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var notification = prepareNotification()

        fun updateText(msg: String) {
            notification.contentView!!.setTextViewText(R.id.notification_download_progress_status_text, msg)
        }

        fun updateProgress(sofar: Int) {
            notification.contentView!!.setProgressBar(R.id.notification_download_progress_status_progress, 100, sofar, false)
        }
        fun notify() {
            notificationManager.notify(notificationId, notification)
        }

        updateText("Downloading subscription list")
        notify()

        var progress: Int

        fun traverseOutline(outline: Outline?, process: (Outline?) -> Unit) {
            if (outline != null){
                process(outline)
            }
            for(ln in outline?.outline?.iterator())
                traverseOutline(ln, process)
        }

        var totalOutlinesToBeProcessed = 0

        try{
            val req = httpGet(targetUrl!!).body()

            val opml = transformXmlToOpml(req?.replace("<?xml version=\"1.0\" encoding=\"utf-8\" ?>", ""))
            updateText("Download completed. Start processing subscription list")

            opml.value?.body?.outline?.forEach {
                traverseOutline(it, { otl ->
                    if (otl != null)
                        totalOutlinesToBeProcessed++
                })
            }

            updateText("Processing $totalOutlinesToBeProcessed items")

            Log.d(TAG, "Outlines to be processed $totalOutlinesToBeProcessed")

            opml.value?.body?.outline?.forEach{
                traverseOutline(it, { otl ->
                    saveOutline(otl!!)
                    progress++
                    val soFar = (progress * 100) div totalOutlinesToBeProcessed
                    updateProgress(soFar)
                    notify()
                })
            }

            updateText("OPML subscription list import is completed")
        }
        catch(e: HttpRequestException){
            Log.d(TAG, "Error in importing ${e.getCause()}")
            updateText("There is a problem in completing OPML subscription import")
        }
    }

    fun saveOutline(outline: Outline) {
        try {
            val url = outline.xmlUrl
            if (!url.isNullOrEmpty()){
                //check if the url already existed or not
                val alreadyBookmarked = checkIfUrlAlreadyBookmarked(url!!)

                if (!alreadyBookmarked){
                    //download the url
                    val res = downloadSingleFeed(url)
                    if (res.isTrue()){
                        val title = res.value!!.title
                        val language = if (res.value!!.language.isNullOrBlank()) "en" else res.value!!.language

                        saveBookmarkToDb(title, url, BookmarkKind.RSS, language, null)
                    }
                }
            }
        }
        catch(e: Exception){
            Log.d(TAG, "Error in trying to save outline ${e.getCause()}")
        }
    }
}