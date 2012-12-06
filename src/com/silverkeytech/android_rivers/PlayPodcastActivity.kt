package com.silverkeytech.android_rivers

import android.os.Bundle
import android.widget.TextView

/**
 * Created with IntelliJ IDEA.
 * User: Dody
 * Date: 29/11/12
 * Time: 13:34
 * To change this template use File | Settings | File Templates.
 */
public class PlayPodcastActivity: MainActivity(){

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play_podcast)

        var intent = getIntent()

        if (intent != null){
            var title = findView<TextView>(R.id.play_podcast_title_tv)
            var message = findView<TextView>(R.id.play_podcast_message_tv)

            var location = intent!!.getStringExtra(DownloadService.PARAM_DOWNLOAD_LOCATION_PATH)
            message.setText("Location for download at " + location)
        }

        toastee("play mee", Duration.LONG)
    }
}