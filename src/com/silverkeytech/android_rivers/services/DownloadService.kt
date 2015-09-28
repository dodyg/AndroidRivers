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

import android.app.Activity
import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Message
import android.os.RemoteException
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.RemoteViews
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import com.silverkeytech.android_rivers.db.savePodcastToDb
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.net.URL
import java.util.Random
import android.content.res.Resources
import com.silverkeytech.android_rivers.activities.MainWithFragmentsActivity
import com.silverkeytech.android_rivers.Params
import com.silverkeytech.android_rivers.R
import com.silverkeytech.android_rivers.with
import com.silverkeytech.android_rivers.isModernAndroid
import com.silverkeytech.android_rivers.generateThrowawayName
import com.silverkeytech.android_rivers.getFileNameFromUri

public class DownloadService(): IntentService("DownloadService"){
    companion object{
        public val TAG: String = javaClass<DownloadService>().getSimpleName()
    }

    var targetTitle: String? = null
    var targetUrl: String? = null
    var targetSourceTitle: String? = null
    var targetSourceUrl: String? = null

    fun prepareNotification(title: String, filePath: String): Notification {
        val notificationIntent = Intent(Intent.ACTION_MAIN)
        notificationIntent.setClass(getApplicationContext()!!, javaClass<MainWithFragmentsActivity>())
        notificationIntent.putExtra(Params.DOWNLOAD_LOCATION_PATH, filePath)

        val contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val notification = NotificationCompat.Builder(this)
                .setTicker("Start downloading $title")
        ?.setWhen(System.currentTimeMillis())
        ?.setContentIntent(contentIntent)
        ?.build()

        notification!!.icon = android.R.drawable.stat_sys_download

        val remote = RemoteViews(getApplicationContext()!!.getPackageName(), R.layout.notification_download_progress).with {
            this.setImageViewResource(R.id.notification_download_progress_status_icon, android.R.drawable.stat_sys_download_done)
            this.setProgressBar(R.id.notification_download_progress_status_progress, 100, 0, false)
            this.setTextViewText(R.id.notification_download_progress_status_text, "Downloading $targetTitle")
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
        targetTitle = p0?.getStringExtra(Params.DOWNLOAD_TITLE)
        targetUrl = p0?.getStringExtra(Params.DOWNLOAD_URL)
        targetSourceTitle = p0?.getStringExtra(Params.DOWNLOAD_SOURCE_TITLE)
        targetSourceUrl = p0?.getStringExtra(Params.DOWNLOAD_SOURCE_URL)

        Log.d(TAG, "onHandleIntent with ${targetUrl}")

        var inferredName = getFileNameFromUri(targetUrl!!)

        var result = Activity.RESULT_CANCELED
        var filename: String = ""

        var notification: Notification? = null

        val notificationId = Random().nextLong().toInt()
        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        try{
            val url = URL(targetUrl)
            val connection = url.openConnection()!!
            connection.connect()

            val fileLength: Int = connection.getContentLength()
            val contentType = connection.getContentType()!!

            Log.d(TAG, "File length $fileLength")

            if (inferredName == null)
                inferredName = generateThrowawayName() + ".mp3"

            val directory = Environment.getExternalStorageDirectory()!!.getPath() + "/" + Environment.DIRECTORY_PODCASTS
            filename = directory + "/" + inferredName

            notification = prepareNotification(targetTitle!!, filename)

            Log.d(TAG, "Podcast to be stored at ${filename}")

            notificationManager.notify(notificationId, notification!!)

            var input: BufferedInputStream? = null
            var output: FileOutputStream? = null
            try{
                input = BufferedInputStream(url.openStream()!!)
                output = FileOutputStream(filename)

                var data = ByteArray(1024)
                var total: Long = 0
                var progress: Int = 0

                var count: Int = input!!.read(data)

                var oldProgress: Int

                while (count != -1){
                    total += count
                    oldProgress = progress
                    progress = (total.toInt() * 100) div fileLength
                    Log.d(TAG, "Download progress ($total * 100) / $fileLength $progress")

                    if (progress != oldProgress){
                        notification!!.contentView!!.setProgressBar(R.id.notification_download_progress_status_progress, 100, progress, false)
                        notificationManager.notify(notificationId, notification!!)
                    }

                    output!!.write(data, 0, count)
                    count = input!!.read(data)
                }

                val res = savePodcastToDb(targetTitle!!,
                        targetUrl!!,
                        targetSourceTitle!!,
                        targetSourceUrl!!,
                        filename,
                        "",
                        contentType,
                        fileLength
                )

                if (res.isFalse()){
                    notification!!.contentView!!.setTextViewText(R.id.notification_download_progress_status_text, "File download fails to save to db")
                    notificationManager.notify(notificationId, notification!!)
                    result = Activity.RESULT_CANCELED
                }
                else{
                    notification!!.contentView!!.setTextViewText(R.id.notification_download_progress_status_text, "File successfully downloaded to $filename")
                    notification!!.contentView!!.setProgressBar(R.id.notification_download_progress_status_progress, 100, 100, false)
                    notificationManager.notify(notificationId, notification!!)
                    result = Activity.RESULT_OK
                }

            }catch (e: java.io.FileNotFoundException){
                notification!!.contentView!!.setTextViewText(R.id.notification_download_progress_status_text, "Download fails due to ${e.getMessage()}")
                notificationManager.notify(notificationId, notification!!)
                result = Activity.RESULT_CANCELED
            }
            finally{
                input?.close()
                output?.flush()
                output?.close()
            }
        }
        catch(e: Exception){
            Log.d(TAG, "Exception happend at attempt to download $targetTitle with error : ${e.getMessage()}")
            if (notification != null){
                notification!!.contentView!!.setTextViewText(R.id.notification_download_progress_status_text, "File $inferredName download cancelled due to error")
                notificationManager.notify(notificationId, notification!!)
            }
            result = Activity.RESULT_CANCELED
        }

        val extras = p0?.getExtras()
        if (extras != null){
            val messenger = extras.get(Params.MESSENGER) as android.os.Messenger
            val msg = Message.obtain()!!
            msg.arg1 = result
            msg.obj = filename
            try{
                messenger.send(msg)
            }
            catch(e: RemoteException){
                Log.d(TAG, "Have problem when try to send a message ")
            }
        }
    }

    public override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "OnStartCommand  with ${targetUrl}")

        return super<IntentService>.onStartCommand(intent, flags, startId)
    }

    public override fun onCreate() {
        super<IntentService>.onCreate()
        Log.d(TAG, "Service created  with ${targetUrl}")
    }

    public override fun onStart(intent: Intent?, startId: Int) {
        super<IntentService>.onStart(intent, startId)
        Log.d(TAG, "Service started  with ${targetUrl}")
    }

    public override fun onDestroy() {
        super<IntentService>.onDestroy()
        Log.d(TAG, "Service destroyed  with ${targetUrl}")
    }
}