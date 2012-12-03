package com.silverkeytech.android_rivers

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.actionbarsherlock.app.SherlockListActivity

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

        var i = getIntent()!!
        riverUrl = i.getStringExtra(Params.RIVER_URL)!!
        riverName = i.getStringExtra(Params.RIVER_NAME)!!

        setTitle(riverName)
        DownloadRiverContent(this).execute(riverUrl)
    }

    public override fun onBackPressed() {
        Log.d(TAG, "Exit River News Listing")
        finish()
    }

    /*
    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater = getMenuInflater()!!
        inflater.inflate(R.menu.river_menu, menu)
        return true
    }

    public override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.getItemId()){
            R.id.river_menu_refresh -> {
                DownloadRiverContent(this).execute(riverUrl)
                return true
            }
            else ->
                return super.onOptionsItemSelected(item)
        }
    }
      */
}