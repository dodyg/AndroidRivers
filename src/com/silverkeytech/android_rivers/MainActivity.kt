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


    val EXPLORE: Int = 1


    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater = getSupportMenuInflater()!!
        inflater.inflate(R.menu.subscription_menu, menu)

        //top menu
        menu?.add(0, EXPLORE, 0, "MORE NEWS")
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)

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
            R.id.subscription_menu_scripting_com_all -> {
                downloadOpml("http://scripting.com/toc.opml", "Scripting.Com")
                return false
            }
            R.id.subscription_menu_curry_com_all -> {
                downloadOpml("http://blog.curry.com/toc.opml", "Adam Curry")
                return false
            }
            EXPLORE -> {
                downloadOpml("http://hobieu.apphb.com/api/1/opml/root", "Get more news")
                return false
            }
            else ->
                return super.onOptionsItemSelected(item)
        }
    }

    fun downloadOpml(url: String, title : String) {
        var opml = DownloadOpml(this)
        opml.setProcessedCompletedCallback({
            res ->
            if (res.isTrue()){
                var intent = Intent(Intent.ACTION_MAIN)
                intent.setClass(getApplicationContext(), javaClass<OutlinerActivity>())
                intent.putExtra(OutlinerActivity.OUTLINES_DATA, res.value!!)
                intent.putExtra(OutlinerActivity.OUTLINES_TITLE, title)

                startActivity(intent)
            }
            else{
                toastee("Downloading url fails becaue of ${res.exception?.getMessage()}", Duration.LONG)
            }
        }, { outline -> outline.text != "<rules>" })

        opml.execute(url)
    }
}
