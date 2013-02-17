package com.silverkeytech.android_rivers.meta_weblog

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
import com.silverkeytech.android_rivers.MainWithFragmentsActivity
import com.silverkeytech.android_rivers.Params
import com.silverkeytech.android_rivers.with
import com.silverkeytech.android_rivers.R
import java.util.HashMap

public class BlogPostService(): IntentService("DownloadService"){
    class object{
        public val TAG: String = javaClass<BlogPostService>().getSimpleName()
    }

    var config : HashMap<String,String>? = null
    var post : HashMap<String, String>? = null

    fun prepareNotification(title: String, filePath: String): Notification {
        val notificationIntent = Intent(Intent.ACTION_MAIN)
        notificationIntent.setClass(getApplicationContext(), javaClass<MainWithFragmentsActivity>())
        notificationIntent.putExtra(Params.DOWNLOAD_LOCATION_PATH, filePath)

        val contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val notification = NotificationCompat.Builder(this)
                .setTicker("Posting $title")
        ?.setWhen(System.currentTimeMillis())
        ?.setContentIntent(contentIntent)
        ?.build()


        notification!!.icon = android.R.drawable.stat_sys_download

        notification.contentView = RemoteViews(getApplicationContext()!!.getPackageName(), R.layout.download_progress).with {
            this.setImageViewResource(R.id.download_progress_status_icon, android.R.drawable.stat_sys_download_done)
            this.setProgressBar(R.id.download_progress_status_progress, 100, 0, false)
            this.setTextViewText(R.id.download_progress_status_text, "Posting")
        }

        return notification
    }

    protected override fun onHandleIntent(p0: Intent?) {
        config = p0!!.getSerializableExtra("config")!! as HashMap<String, String>
        post = p0!!.getSerializableExtra("post")!!  as HashMap<String, String>

        Log.d(TAG, " Server is ${config?.get("server")}")
//        targetTitle = p0?.getStringExtra(Params.DOWNLOAD_TITLE)
//        targetUrl = p0?.getStringExtra(Params.DOWNLOAD_URL)
//        targetSourceTitle = p0?.getStringExtra(Params.DOWNLOAD_SOURCE_TITLE)
//        targetSourceUrl = p0?.getStringExtra(Params.DOWNLOAD_SOURCE_URL)

    }

    public override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "OnStartCommand")

        return super<IntentService>.onStartCommand(intent, flags, startId)
    }

    public override fun onCreate() {
        super<IntentService>.onCreate()
        Log.d(TAG, "Service created")
    }

    public override fun onStart(intent: Intent?, startId: Int) {
        super<IntentService>.onStart(intent, startId)
        Log.d(TAG, "Service created")
    }

    public override fun onDestroy() {
        super<IntentService>.onDestroy()
        Log.d(TAG, "Service created")
    }
}