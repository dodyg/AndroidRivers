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

package com.silverkeytech.android_rivers

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.RemoteViews
import com.github.kevinsawicki.http.HttpRequest
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import com.google.gson.Gson
import com.silverkeytech.android_rivers.db.Bookmark
import com.silverkeytech.android_rivers.db.DatabaseManager
import com.silverkeytech.android_rivers.outliner.transformXmlToOpml
import com.silverkeytech.android_rivers.outliner.traverse
import com.silverkeytech.android_rivers.riverjs.FeedsRiver
import java.util.Random
import com.silverkeytech.android_rivers.outliner.transformFeedOpmlToOpml
import com.silverkeytech.android_rivers.outliner.startOutlinerActivity

//import com.silverkeytech.android_rivers.db.DatabaseManager

public class TryOutActivity(): Activity()
{
    class object {
        public val TAG: String = javaClass<TryOutActivity>().getSimpleName()
    }

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        setTheme(this.getVisualPref().getTheme())

        super.onCreate(savedInstanceState)
        setContentView(R.layout.tryout)
        handleDownloadRss()
        handleDownloadGifImage()
        handleDownloadFile()
        handleDownloadJpgImage()
        handleDownloadPngImage()
        handleHandleNotification()
        handleCreateBookmarkTable()
        handleInsertToBookmarkTable()
        handleOutliner()
        handleDownloadRecursiveOpml()
        handleRiverJsWithOpmlSource()

    }

    fun handleDownloadRss(){
        var btn = findView<Button>(R.id.tryout_download_rss_btn)

        btn.setOnClickListener {
            DownloadFeed(this, false)
                    .executeOnComplete {
                        res ->
                        if (res.isTrue()){
                            var rss = res.value!!.rss
                            toastee("Title ${rss?.channel?.title}")
                        }else{
                            toastee("Error ${res.exception?.getMessage()}", Duration.LONG)
                        }
                    }
                    .execute("http://cyber.law.harvard.edu/rss/examples/rss2sample.xml")
        }
    }

    fun handleDownloadGifImage() {
        var btn = findView<Button>(R.id.tryout_download_gif_image_btn)
        btn.setOnClickListener {
            Log.d(TAG, "Start downloading gif image")
            DownloadImage(this).execute("http://www.science.mcmaster.ca/brainbee/images/stories/announcements/brainbee-logo-small.gif")
        }
    }

    fun handleDownloadJpgImage() {
        var btn = findView<Button>(R.id.tryout_download_jpg_image_btn)
        btn.setOnClickListener {
            Log.d(TAG, "Start downloading jpeg image")
            DownloadImage(this).execute("http://www.fantom-xp.com/wallpapers/42/Iceberg_Very_Large.jpg")
        }
    }

    fun handleDownloadPngImage() {
        var btn = findView<Button>(R.id.tryout_download_png_image_btn)
        btn.setOnClickListener {
            Log.d(TAG, "Start downloading png image")
            DownloadImage(this).execute("http://bartelme.at/material/news/NetNewsWire256.png")
        }
    }

    fun handleDownloadFile() {
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

    fun handleCreateBookmarkTable() {
        var btn = findView<Button>(R.id.tryout_setup_bookmark_table_btn)

        btn.setOnClickListener(object : OnClickListener{
            public override fun onClick(p0: View?) {
                val total = DatabaseManager.query().bookmark().all()

                if (total.exist)
                    toastee("all ${total.values?.count()}", Duration.LONG)
                else
                    toastee("There is no record", Duration.LONG)
            }
        })
    }

    fun handleInsertToBookmarkTable() {
        var btn = findView<Button>(R.id.tryout_insert_data_bookmark_table_btn)

        btn.setOnClickListener {
            var bk = Bookmark()
            bk.title = "good morning america"
            bk.url = "http://www.cnn.com " + Random().nextInt()
            bk.kind = "book"
            DatabaseManager.bookmark!!.create(bk)
        }
    }

    var counter: Int = 1

    fun handleHandleNotification() {
        var btn = findView<Button>(R.id.tryout_show_notification_btn)
        btn.setOnClickListener {

            var notificationIntent = Intent(Intent.ACTION_MAIN)
            notificationIntent.setClass(getApplicationContext(), javaClass<MainActivity>())
            notificationIntent.putExtra(DownloadService.PARAM_DOWNLOAD_LOCATION_PATH, "Location PATH")

            var contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT)

            counter++

            var notification = NotificationCompat.Builder(this)
                    //.setContent()
                    .setTicker("This is the ticker")
            ?.setContentTitle("Android Rivers")
            ?.setContentText("Downloading a file")
            ?.setSmallIcon(android.R.drawable.gallery_thumb)
            ?.setProgress(100, 10, true)
            ?.setWhen(System.currentTimeMillis())
            ?.setContentIntent(contentIntent)
            ?.build()

            notification!!.contentView = RemoteViews(getApplicationContext()!!.getPackageName(), R.layout.download_progress)

            notification!!.contentView!!.setImageViewResource(R.id.download_progress_status_icon, android.R.drawable.btn_star);
            notification!!.contentView!!.setProgressBar(R.id.download_progress_status_progress, 100, 10, false)
            notification!!.contentView!!.setTextViewText(R.id.download_progress_status_text, "Download in progress")

            var nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(counter, notification)

            var thread = Thread(runnable{
                for(i in 11..100){
                    notification!!.contentView!!.setProgressBar(R.id.download_progress_status_progress, 100, i, false)
                    nm.notify(counter, notification)

                    Log.d(TAG, "We are progressing $i / 100")
                    try{
                        Thread.sleep(50)
                    }
                    catch(e: InterruptedException){
                        Log.d(TAG, "Exception ${e.getMessage()}")
                    }
                }
                //nm.cancel(counter);
            })

            thread.run()
        }
    }

    public fun handleOutliner() {
        var btn = findView<Button>(R.id.tryout_show_outline_btn)
        btn.setOnClickListener {

            val url = "http://scripting.com/toc.opml"

            DownloadOpml(this)
            .executeOnProcessedCompletion({
                res ->
                if (res.isTrue()){
                    var intent = Intent(Intent.ACTION_MAIN)
                    intent.setClass(getApplicationContext(), javaClass<OutlinerActivity>())
                    intent.putExtra(OutlinerActivity.OUTLINES_DATA, res.value!!)

                    startActivity(intent)
                }
                else{
                    toastee("Downloading url fails becaue of ${res.exception?.getMessage()}", Duration.LONG)
                }
            }, { outline -> outline.text != "<rules>" })
            .execute(url)

            //val url = "http://opmlviewer.com/Content/Directories.opml"
            //val url = "http://static.scripting.com/denver/wo/dave/2012/11/22/archive018.opml"
        }
    }

    public fun handleRiverJsWithOpmlSource() {
        var btn = findView<Button>(R.id.tryout_download_riverjs_with_opml_btn)

        btn.setOnClickListener(object: OnClickListener{
            public override fun onClick(p0: View?) {
                val url = "http://hobieu.apphb.com/api/1/samples/riverjswithopml"

                var i = Intent(this@TryOutActivity, javaClass<RiverActivity>())
                i.putExtra(Params.RIVER_URL, url)
                i.putExtra(Params.RIVER_NAME, "Sample River with OPML")
                i.putExtra(Params.RIVER_LANGUAGE, "en")

                startActivity(i)
            }
        })
    }

    public fun handleDownloadRecursiveOpml() {
        var btn = findView<Button>(R.id.tryout_download_recursive_opml_btn)

        btn.setOnClickListener(object: OnClickListener{
            public override fun onClick(p0: View?) {
                var req: String? = ""
                val url = "http://opmlviewer.com/Content/Directories.opml"

                try{
                    req = HttpRequest.get(url)?.body()
                }
                catch(e: HttpRequestException){
                    var ex = e.getCause()
                    Log.d(TAG, "Error in downloading OPML $url")
                    toastee("Error in downloading OPML from $url")
                }

                Log.d(TAG, "Text : $req")

                val opml = transformXmlToOpml(req?.replace("<?xml version=\"1.0\" encoding=\"utf-8\" ?>", ""))

                if(opml.isTrue()){
                    val sorted = opml.value!!.traverse()
                    toastee("Opml parsing is Great ${sorted.count()}")
                }   else{
                    Log.d(TAG, "Error in parsing opml  ${opml.exception?.getMessage()}")
                    toastee("Error in parsing opml ${opml.exception?.getMessage()}")
                }
            }
        })
    }

    public override fun onBackPressed() {
        super<Activity>.onBackPressed()
        finish()
    }
}

