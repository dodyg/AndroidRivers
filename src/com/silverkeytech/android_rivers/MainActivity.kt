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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)


        var intent = getIntent()
        if(intent != null){
            var locationPath = intent!!.getStringExtra(DownloadService.PARAM_DOWNLOAD_LOCATION_PATH)
            if (!locationPath.isNullOrEmpty()){
                try{
                    var playIntent = Intent()
                    playIntent.setAction(android.content.Intent.ACTION_VIEW);
                    var file = File(locationPath!!);
                    playIntent.setDataAndType(Uri.fromFile(file), "audio/*");
                    startActivity(playIntent)
                }
                catch(e : Exception){
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
            else ->
                return super.onOptionsItemSelected(item)
        }
    }
}
