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

public open class PodcastPlayerService(): Service(), MediaPlayer.OnErrorListener{
    class object{
        public val TAG: String = javaClass<PodcastPlayerService>().getSimpleName()
    }

    private val mBinder: IBinder = ServiceBinder()
    var mPlayer: MediaPlayer? = null
    private var length: Int = 0

    inner class ServiceBinder(): Binder() {
        fun getService(): PodcastPlayerService? {
            return this@PodcastPlayerService
        }
    }

    public override fun onBind(p0: Intent?): IBinder? {
        return mBinder
    }

    public override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val podcastTitle = intent!!.getStringExtra(Params.PODCAST_TITLE)
        val podcastPath = intent.getStringExtra(Params.PODCAST_PATH)

        Log.d(TAG, "Path $podcastPath")

        mPlayer = MediaPlayer.create(this, Uri.parse(podcastPath))
        mPlayer?.setOnErrorListener(this)
        mPlayer?.setLooping(false)
        mPlayer?.setVolume(100.0, 100.0)
        mPlayer?.start()

        return super<Service>.onStartCommand(intent, flags, startId)
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
