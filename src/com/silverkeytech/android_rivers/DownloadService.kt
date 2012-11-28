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


public class DownloadService() : IntentService("DownloadService"){
    class object{
        public val PARAM_DOWNLOAD_URL : String= "downloadUrl"
        public val TAG: String = javaClass<DownloadService>().getSimpleName()!!
    }

    var targetUrl : String? = null

    protected override fun onHandleIntent(p0: Intent?) {
        targetUrl = p0!!.getStringExtra(PARAM_DOWNLOAD_URL)
        Log.d(TAG, "onHandleIntent with ${targetUrl}")

        var inferredName = getFileNameFromUri(targetUrl!!)

        var result = Activity.RESULT_CANCELED
        var filename : String = ""
        try{
            var req = HttpRequest.get(targetUrl)

            if (inferredName == null)
                inferredName = generateThrowawayName() + ".mp3"

            var directory = Environment.getExternalStorageDirectory()!!.getPath() + "/" + Environment.DIRECTORY_PODCASTS
            filename  = directory + "/" + inferredName

            Log.d(TAG, "Podcast to be stored at ${filename}")

            var output = File(filename)
            req!!.receive(output)

            result = Activity.RESULT_OK
        }
        catch(e : HttpRequestException){
            Log.d(TAG, "Exception happend at attempt to download ${e.getMessage()}")
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
