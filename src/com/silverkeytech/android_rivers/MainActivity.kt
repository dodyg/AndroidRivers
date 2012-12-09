package com.silverkeytech.android_rivers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.actionbarsherlock.app.SherlockActivity
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuItem
import java.io.File

public open class MainActivity(): SherlockActivity() {
    class object {
        public val TAG: String = javaClass<MainActivity>().getSimpleName()
    }

    val DEFAULT_SUBSCRIPTION_LIST = "http://hobieu.apphb.com/api/1/default/riverssubscription"

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        setTheme(this.getVisualPref().getTheme())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        var intent = getIntent()
        if(intent != null){
            val locationPath = intent!!.getStringExtra(DownloadService.PARAM_DOWNLOAD_LOCATION_PATH)
            if (!locationPath.isNullOrEmpty()){
                try{
                    var playIntent = Intent()
                    playIntent.setAction(android.content.Intent.ACTION_VIEW);
                    var file = File(locationPath!!);
                    playIntent.setDataAndType(Uri.fromFile(file), "audio/*");
                    startActivity(playIntent)
                }
                catch(e: Exception){
                    toastee("Sorry, I cannot play the file $locationPath. Please click Refresh on the menu option to download the news list again.", Duration.LONG)
                }
            }
        }

        DownloadSubscription(this, false).execute(DEFAULT_SUBSCRIPTION_LIST)
    }

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater = getSupportMenuInflater()!!
        inflater.inflate(R.menu.subscription_menu, menu)
        return true
    }

    public override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.getItemId()){
            R.id.subscription_menu_refresh -> {
                DownloadSubscription(this, true).execute(DEFAULT_SUBSCRIPTION_LIST)
                return true
            }
            R.id.river_menu_tryout -> {
                var i = Intent(this, javaClass<TryOutActivity>())
                startActivity(i)
                return true
            }
            R.id.subscription_menu_download_all -> {
                val subscriptionList = getApplication().getMain().getSubscriptionListCache()

                if (subscriptionList != null){
                    var intent = Intent(this, javaClass<DownloadAllRiversService>())
                    var titleList = subscriptionList.body?.outline?.iterator()?.map { it?.text }?.toArrayList()
                    var urlList = subscriptionList.body?.outline?.iterator()?.map { it?.url }?.toArrayList()

                    intent.putStringArrayListExtra(DownloadAllRiversService.TITLES, titleList)
                    intent.putStringArrayListExtra(DownloadAllRiversService.URLS, urlList)

                    startService(intent)
                    return true
                }
                else {
                    toastee("Sorry, there's nothing to download.", Duration.AVERAGE)
                    return false
                }
            }
            else ->
                return super.onOptionsItemSelected(item)
        }
    }
}
