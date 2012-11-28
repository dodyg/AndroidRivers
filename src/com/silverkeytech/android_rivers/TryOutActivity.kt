package com.silverkeytech.android_rivers

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.util.Log
import android.content.Intent
import android.os.Messenger
import android.os.Handler
import android.os.Message

public class TryOutActivity() : Activity()
{
    class object {
        public val TAG: String = javaClass<TryOutActivity>().getSimpleName()!!
    }

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tryout)
        handleDownloadGifImage()
        handleDownloadFile()
        handleDownloadJpgImage()
        handleDownloadPngImage()
    }

    fun handleDownloadGifImage(){
        var btn = findView<Button>(R.id.tryout_download_gif_image_btn)
        btn.setOnClickListener {
            Log.d(TAG, "Start downloading gif image")
            DownloadImage(this).execute("http://www.science.mcmaster.ca/brainbee/images/stories/announcements/brainbee-logo-small.gif")
        }
    }

    fun handleDownloadJpgImage(){
        var btn = findView<Button>(R.id.tryout_download_jpg_image_btn)
        btn.setOnClickListener {
            Log.d(TAG, "Start downloading jpeg image")
            DownloadImage(this).execute("http://www.fantom-xp.com/wallpapers/42/Iceberg_Very_Large.jpg")
        }
    }

    fun handleDownloadPngImage(){
        var btn = findView<Button>(R.id.tryout_download_png_image_btn)
        btn.setOnClickListener {
            Log.d(TAG, "Start downloading png image")
            DownloadImage(this).execute("http://bartelme.at/material/news/NetNewsWire256.png")
        }
    }

    fun handleDownloadFile(){
        var btn = findView<Button>(R.id.tryout_download_file_btn)
        btn.setOnClickListener {
            Log.d(TAG, "Start downloading file")

            var messenger = Messenger(object : Handler(){
                public override fun handleMessage(msg: Message?) {
                    var path = msg!!.obj as String

                    if (msg.arg1 == Activity.RESULT_OK && !path.isNullOrEmpty()){
                        toastee("File is successfully downloaded at $path")
                    }else{
                        toastee("Download failed")
                    }
                }
            })

            var intent = Intent(this, javaClass<DownloadService>())
            intent.putExtra(DownloadService.PARAM_DOWNLOAD_URL, "http://podcastdownload.npr.org/anon.npr-podcasts/podcast/13/166038315/npr_166038315.mp3")
            intent.putExtra(Params.MESSENGER, messenger)
            this.startService(intent)
        }
    }

    public override fun onBackPressed() {
        super<Activity>.onBackPressed()
        finish()
    }
}