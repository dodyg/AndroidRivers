package com.silverkeytech.android_rivers

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.util.Log
import android.content.Intent
import android.os.Messenger
import android.os.Handler
import android.os.Message
import android.support.v4.app.NotificationCompat
import android.content.Context
import android.app.NotificationManager
import android.app.PendingIntent
import android.widget.RemoteViews
import com.j256.ormlite.android.AndroidConnectionSource
import java.sql.SQLException
import android.database.sqlite.SQLiteOpenHelper
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.android.apptools.OpenHelperManager
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import com.silverkeytech.android_rivers.db.Bookmark
import android.view.View.OnClickListener
import android.view.View

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
        handleHandleNotification()
        handleCreateBookmarkTable()
        handleInsertToBookmarkTable()
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

    fun handleCreateBookmarkTable(){
        var btn = findView<Button>(R.id.tryout_setup_bookmark_table_btn)

        btn.setOnClickListener(object : OnClickListener{
            public override fun onClick(p0: View?) {
                var connection : ConnectionSource? = null

                try{
                    connection = AndroidConnectionSource(OpenHelperManager.getHelper(this@TryOutActivity))

                    TableUtils.createTable(connection, javaClass<Bookmark>())
                    Log.d(TAG, "Table bookmarks created")
                }
                catch(e : SQLException){
                    Log.d(TAG, "Exception when trying to create a Bookmark table ${e.getMessage()}")
                }
                finally{
                    connection?.close()
                }
            }
        })
    }

    fun handleInsertToBookmarkTable(){
        var btn = findView<Button>(R.id.tryout_insert_data_bookmark_table_btn)

        btn.setOnClickListener {
            var connection : ConnectionSource? = null

            try{
                connection = AndroidConnectionSource(OpenHelperManager.getHelper(this@TryOutActivity))

                TableUtils.createTable(connection, javaClass<Bookmark>())
                Log.d(TAG, "Table bookmarks created")
            }
            catch(e : SQLException){
                Log.d(TAG, "Exception when trying to create a Bookmark table ${e.getMessage()}")
            }
            finally{
                connection!!.close()
            }
            0
        }

    }

    var counter : Int = 1

    fun handleHandleNotification(){
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
                    ?.setProgress(100,10, true)
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
                    catch( e : InterruptedException){
                         Log.d(TAG, "Exception ${e.getMessage()}")
                    }
                }
                //nm.cancel(counter);
            })

            thread.run()
        }
    }

    public override fun onBackPressed() {
        super<Activity>.onBackPressed()
        finish()
    }
}