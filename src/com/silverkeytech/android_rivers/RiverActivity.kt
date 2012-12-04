package com.silverkeytech.android_rivers

import android.os.Bundle
import android.util.Log
import com.actionbarsherlock.app.SherlockListActivity
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuItem

//Responsible of downloading, caching and viewing a news river content
public class RiverActivity() : SherlockListActivity()
{
    class object {
        public val TAG: String = javaClass<RiverActivity>().getSimpleName()
    }

    var riverUrl : String = ""
    var riverName : String = ""

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.river)

        var actionBar = getSupportActionBar()!!
        actionBar.setDisplayShowHomeEnabled(false) //hide the app icon.

        var i = getIntent()!!
        riverUrl = i.getStringExtra(Params.RIVER_URL)!!
        riverName = i.getStringExtra(Params.RIVER_NAME)!!

        setTitle(riverName)

        downloadContent(riverUrl, false)
    }

    fun downloadContent(riverUrl : String, ignoreCache : Boolean){
        var cache = getApplication().getMain().getRiverCache(riverUrl)

        if (cache != null && !ignoreCache){
            Log.d(TAG, "Cache is hit for url $riverUrl")
            var now = System.currentTimeMillis()
            RiverContentRenderer(this).handleNewsListing(cache!!)
            var after = System.currentTimeMillis()
            Log.d(TAG, "Cache display takes ${after - now} ms")
        }
        else
            DownloadRiverContent(this, false).execute(riverUrl)
    }

    public override fun onBackPressed() {
        Log.d(TAG, "Exit River News Listing")
        finish()
    }

    val REFRESH : Int = 1

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, REFRESH, 0, "Refresh")
        ?.setIcon(R.drawable.ic_menu_refresh)
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)

        var inflater = getSupportMenuInflater()!!
        inflater.inflate(R.menu.river_menu, menu)

        return true
    }

    public override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.getItemId()){
            R.id.river_menu_refresh, REFRESH -> {
                downloadContent(riverUrl, true)
                return true
            }
            else ->
                return super.onOptionsItemSelected(item)
        }
    }
}