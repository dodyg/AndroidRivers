package com.silverkeytech.android_rivers

import android.media.MediaScannerConnection
import android.net.Uri
import android.content.Context
import android.util.Log
import android.os.Environment

public class MediaScannerWrapper (val context : Context, val filePath : String, val mimeType : String )
    : MediaScannerConnection.MediaScannerConnectionClient{

    class object {
        public val TAG: String = javaClass<MediaScannerWrapper>().getSimpleName()

        fun scanPodcasts(context :Context, filePath : String){
            var scanner = MediaScannerWrapper(context, filePath, "audio/mpeg" )
            scanner.scan()
        }
    }

    var connection : MediaScannerConnection
    {
        connection = MediaScannerConnection(context, this)
    }

    public fun scan(){
        connection.connect()
    }

    public override fun onMediaScannerConnected() {
        connection.scanFile(filePath, mimeType)
        Log.d(TAG, "Start scanning at ${filePath} for ${mimeType}")
    }

    public override fun onScanCompleted(p0: String?, p1: Uri?) {
        Log.d(TAG, "Done scanning at ${filePath} for ${mimeType}")
    }
}
