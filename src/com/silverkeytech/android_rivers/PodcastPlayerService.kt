package com.silverkeytech.android_rivers

import android.app.Service
import android.app.Service.START_STICKY
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaPlayer.OnErrorListener
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import android.net.Uri
import android.util
import android.util.Log
import android.os.PowerManager
import android.widget.RemoteViews
import android.app.Notification
import android.app.PendingIntent
import android.support.v4.app.NotificationCompat
import java.util.Random
import android.app.NotificationManager
import android.content.Context

public open class PodcastPlayerService(): Service(), MediaPlayer.OnErrorListener{
    class object{
        public val TAG: String = javaClass<PodcastPlayerService>().getSimpleName()
    }

    private val binder: IBinder = ServiceBinder()
    private var mPlayer: MediaPlayer? = null
    private var length: Int = 0

    inner class ServiceBinder(): Binder() {
        fun getService(): PodcastPlayerService? {
            return this@PodcastPlayerService
        }
    }

    public override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    fun prepareNotification(): Notification {
        val notificationIntent = Intent(Intent.ACTION_MAIN)
        notificationIntent.setClass(getApplicationContext(), javaClass<MainWithFragmentsActivity>())

        val contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val notification = NotificationCompat.Builder(this)
                .setTicker("Start playing Podcast")
        ?.setWhen(System.currentTimeMillis())
        ?.setContentIntent(contentIntent)
        ?.build()

        notification!!.icon = android.R.drawable.star_big_on

        notification.contentView = RemoteViews(getApplicationContext()!!.getPackageName(), R.layout.download_progress).with {
            this.setImageViewResource(R.id.download_progress_status_icon, android.R.drawable.btn_star)
            this.setProgressBar(R.id.download_progress_status_progress, 100, 0, false)
            this.setTextViewText(R.id.download_progress_status_text, getString(R.string.download_starts))
        }

        return notification
    }

    val notificationId = Random().nextLong().toInt()

    fun updateText(notification : Notification, msg: String) {
        notification.contentView!!.setTextViewText(R.id.download_progress_status_text, msg)
    }

    fun updateProgress(notification : Notification, sofar: Int) {
        notification.contentView!!.setProgressBar(R.id.download_progress_status_progress, 100, sofar, true)
    }

    public override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val podcastTitle = intent!!.getStringExtra(Params.PODCAST_TITLE)
        val podcastPath = intent.getStringExtra(Params.PODCAST_PATH)

        val notification = prepareNotification()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        updateText(notification, "Playing $podcastTitle")
        notificationManager.notify(notificationId, notification)

        Log.d(TAG, "Path $podcastPath")

        mPlayer = MediaPlayer.create(this, Uri.parse(podcastPath))
        mPlayer?.setOnErrorListener(this)
        mPlayer?.setLooping(false)
        mPlayer?.setVolume(100.0, 100.0)
        mPlayer?.start()

        mPlayer!!.setOnCompletionListener(object: MediaPlayer.OnCompletionListener{
            public override fun onCompletion(p0: MediaPlayer?) {
                updateText(notification, "Podcast completed")
                updateProgress(notification, 100)
                notificationManager.notify(notificationId, notification)
            }
        })

        return super<Service>.onStartCommand(intent, flags, startId)
    }



    public fun isPlaying() : Boolean{
        if (mPlayer != null)
            return mPlayer!!.isPlaying()
        else
            return false
    }

    public fun pauseMusic(): Unit {
        if (mPlayer!!.isPlaying()){
            mPlayer!!.pause()
            length = mPlayer!!.getCurrentPosition()
        }
    }

    public fun resumeMusic(): Unit {
        if (!mPlayer!!.isPlaying())
        {
            mPlayer?.seekTo(length)
            mPlayer?.start()
        }
    }

    public fun stopMusic(): Unit {
        mPlayer?.stop()
        mPlayer?.release()
        mPlayer = null
    }

    public override fun onDestroy(): Unit {
        super<Service>.onDestroy()
        if (mPlayer != null){
            try {
                mPlayer!!.stop()
                mPlayer!!.release()
            }
            finally {
                mPlayer = null
            }
        }
    }

    public override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Toast.makeText(this, "Music player failed", Toast.LENGTH_SHORT)?.show()
        if (mPlayer != null){
            try
            {
                mPlayer!!.stop()
                mPlayer!!.release()
            }
            finally
            {
                mPlayer = null
            }
        }
        return false
    }
}
