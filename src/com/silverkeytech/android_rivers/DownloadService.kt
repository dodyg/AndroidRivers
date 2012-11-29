package com.silverkeytech.android_rivers

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.github.kevinsawicki.http.HttpRequest
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import android.os.Environment
import java.io.File
import android.app.Activity
import android.os.Messenger
import android.os.Message
import android.os.RemoteException
import android.support.v4.app.NotificationCompat
import android.app.PendingIntent
import android.widget.RemoteViews
import android.content.Context
import android.app.NotificationManager
import android.app.Notification
import java.util.Random
import java.net.URL
import java.io.BufferedInputStream
import java.io.FileOutputStream


public class DownloadService() : IntentService("DownloadService"){
    class object{
        public val PARAM_DOWNLOAD_URL : String= "downloadUrl"
        public val PARAM_DOWNLOAD_TITLE : String = "downloadTitle"
        public val PARAM_DOWNLOAD_LOCATION_PATH : String = "downloadLocationPath"

        public val TAG: String = javaClass<DownloadService>().getSimpleName()!!
    }

    var targetUrl : String? = null
    var targetTitle : String? = null

    fun prepareNotification(inferredName : String, filePath : String) : Notification{
        var notificationIntent = Intent(Intent.ACTION_MAIN)
        notificationIntent.setClass(getApplicationContext(), javaClass<MainActivity>())

        notificationIntent.putExtra(PARAM_DOWNLOAD_LOCATION_PATH, filePath)

        var contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        var notification = NotificationCompat.Builder(this)
                .setTicker("Podcast starts downloading")
        ?.setSmallIcon(android.R.drawable.gallery_thumb)
        ?.setProgress(100,10, true)
        ?.setWhen(System.currentTimeMillis())
        ?.setContentIntent(contentIntent)
        ?.build()

        notification!!.icon = android.R.drawable.star_big_on

        notification!!.contentView = RemoteViews(getApplicationContext()!!.getPackageName(), R.layout.download_progress)

        notification!!.contentView!!.setImageViewResource(R.id.download_progress_status_icon, android.R.drawable.btn_star);
        notification!!.contentView!!.setProgressBar(R.id.download_progress_status_progress, 100, 0, false)
        notification!!.contentView!!.setTextViewText(R.id.download_progress_status_text, "Downloading $targetTitle")

        return notification!!
    }

    protected override fun onHandleIntent(p0: Intent?) {
        targetUrl = p0!!.getStringExtra(PARAM_DOWNLOAD_URL)
        targetTitle = p0!!.getStringExtra(PARAM_DOWNLOAD_TITLE)

        Log.d(TAG, "onHandleIntent with ${targetUrl}")

        var inferredName = getFileNameFromUri(targetUrl!!)

        var result = Activity.RESULT_CANCELED
        var filename : String = ""

        var notification : Notification? = null

        var notificationId = Random().nextLong().toInt()
        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        try{
            var url = URL(targetUrl)
            var connection = url.openConnection()!!
            connection.connect()

            var fileLength : Int = connection.getContentLength()

            Log.d(TAG, "File length $fileLength")

            if (inferredName == null)
                inferredName = generateThrowawayName() + ".mp3"

            var directory = Environment.getExternalStorageDirectory()!!.getPath() + "/" + Environment.DIRECTORY_PODCASTS
            filename  = directory + "/" + inferredName

            notification = prepareNotification(inferredName!!, filename)

            Log.d(TAG, "Podcast to be stored at ${filename}")

            notificationManager.notify(notificationId, notification)

            var input = BufferedInputStream(url.openStream()!!)
            var output = FileOutputStream(filename)

            var data =  ByteArray(1024)
            Log.d(TAG, "Length of byte array ${data.size}")
            var total : Long = 0
            var progress : Int = 0

            var count : Int = input.read(data)

            var oldProgress = 0

            while (count != -1){
                total += count
                oldProgress = progress
                progress = (total.toInt() * 100) div fileLength
                Log.d(TAG, "Download progress ($total * 100) / $fileLength $progress")

                if (progress != oldProgress){
                    notification!!.contentView!!.setProgressBar(R.id.download_progress_status_progress, 100, progress, false)
                    notificationManager.notify(notificationId, notification)
                }

                output.write(data, 0, count)

                count = input.read(data)
            }

            output.flush()
            output.close()
            input.close()

            notification!!.contentView!!.setTextViewText(R.id.download_progress_status_text, "File successfully download to $filename")
            notificationManager.notify(notificationId, notification)

            result = Activity.RESULT_OK
        }
        catch(e : HttpRequestException){
            Log.d(TAG, "Exception happend at attempt to download ${e.getMessage()}")
            notification!!.contentView!!.setTextViewText(R.id.download_progress_status_text, "File $inferredName download cancelled")
            notificationManager.notify(notificationId, notification)
        }

        var extras = p0.getExtras()
        if (extras != null){
            var messenger = extras!!.get(Params.MESSENGER) as android.os.Messenger
            var msg = Message.obtain()!!
            msg.arg1 = result
            msg.obj = filename
            try{
                messenger.send(msg)
            }
            catch(e : RemoteException){
                Log.d(TAG, "Have problem when try to send a message ")
            }
        }
    }

    public override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
