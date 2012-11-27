package com.silverkeytech.android_rivers

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.util.Log

public class TryOutActivity() : Activity()
{
    class object {
        public val TAG: String = javaClass<TryOutActivity>().getSimpleName()!!
    }

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tryout)
        handleDownloadImage()
        handleDownloadFile()
        handleDownloadLargeImage()
    }

    fun handleDownloadImage(){
        var btn = findView<Button>(R.id.tryout_download_image_btn)
        btn.setOnClickListener {
            Log.d(TAG, "Start downloading image")
            DownloadImage(this).execute("http://www.hdwallpapersarena.com/wp-content/uploads/2012/11/puppy3-150x150.jpg")
        }
    }

    fun handleDownloadLargeImage(){
        var btn = findView<Button>(R.id.tryout_download_large_image_btn)
        btn.setOnClickListener {
            Log.d(TAG, "Start downloading image")
            DownloadImage(this).execute("http://www.fantom-xp.com/wallpapers/42/Iceberg_Very_Large.jpg")
        }
    }

    fun handleDownloadFile(){
        var btn = findView<Button>(R.id.tryout_download_file_btn)
        btn.setOnClickListener {
            Log.d(TAG, "Start downloading file")
        }
    }

    public override fun onBackPressed() {
        super<Activity>.onBackPressed()
        finish()
    }
}