package com.silverkeytech.android_rivers

import android.os.Bundle
import android.util.Log
import com.actionbarsherlock.app.SherlockListActivity
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuItem
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener

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
        actionBar.setDisplayHomeAsUpEnabled(true)

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

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //var inflater = getSupportMenuInflater()!!

        //inflater.inflate(R.menu.river_menu, menu)

        menu?.add("Refresh")
        ?.setOnMenuItemClickListener(object: OnMenuItemClickListener{
            public override fun onMenuItemClick(item: MenuItem?): Boolean {
                DownloadRiverContent(this@RiverActivity).execute(riverUrl)
                return true
            }
        })
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)

        //menu?.add("Tryout")
        //    ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM or android.view.MenuItem.SHOW_AS_ACTION_WITH_TEXT)

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
}