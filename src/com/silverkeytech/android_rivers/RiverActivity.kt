package com.silverkeytech.android_rivers

import android.os.Bundle
import android.util.Log
import com.actionbarsherlock.app.SherlockListActivity
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuItem
import com.actionbarsherlock.view.ActionMode
import android.app.Activity
import android.content.Context

//Responsible of downloading, caching and viewing a news river content
public class RiverActivity() : SherlockListActivity()
{
    class object {
        public val TAG: String = javaClass<RiverActivity>().getSimpleName()
    }

    var riverUrl : String = ""
    var riverName : String = ""
    var mode : ActionMode? = null

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        setTheme(this.getVisualPref().getTheme())
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

    val RESIZE_TEXT : Int = 2

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menu?.add(0, RESIZE_TEXT, 0, "Resize Text")
        ?.setIcon(android.R.drawable.ic_menu_preferences)
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)

        menu?.add(0, REFRESH, 0, "Refresh")
        ?.setIcon(R.drawable.ic_menu_refresh)
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)

        var inflater = getSupportMenuInflater()!!
        inflater.inflate(R.menu.river_menu, menu)

        return true
    }

    internal fun refreshContent(){
        downloadContent(riverUrl, false)
    }

    public override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.getItemId()){
            R.id.river_menu_refresh, REFRESH -> {
                downloadContent(riverUrl, true)
                return true
            }
            RESIZE_TEXT -> {
                mode = startActionMode(ResizeTextActionMode(this, mode))
                return true
            }
            else ->
                return super.onOptionsItemSelected(item)
        }
    }
}

public class ResizeTextActionMode (private var context : RiverActivity, private var mode : ActionMode?) : ActionMode.Callback{
    val INCREASE_SIZE = 1
    val DECREASE_SIZE = 2
    val SWITCH_THEME : Int = 3

    //Get the display text size from preference
    fun increaseTextSize(){
        var pref = context.getVisualPref()
        var textSize = pref.getListTextSize()
        textSize += 2
        pref.setListTextSize(textSize)
    }

    fun decreaseTextSize(){
        var pref = context.getVisualPref()
        var textSize = pref.getListTextSize()
        textSize -= 2
        pref.setListTextSize(textSize)
    }

    public override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when(item!!.getItemId()){
            INCREASE_SIZE -> {
                increaseTextSize()
                context.refreshContent()
            }
            DECREASE_SIZE -> {
                decreaseTextSize()
                context.refreshContent()
            }
            SWITCH_THEME -> {
                context.getVisualPref().switchTheme()
                var intent = context.getIntent()
                context.finish()
                context.startActivity(intent)
                return true
            }
            else -> {
                if (mode != null)
                    mode.finish()
            }
        }

        return true
    }

    public override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    public override fun onDestroyActionMode(mode: ActionMode?) {

    }

    public override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        menu?.add(0, SWITCH_THEME, 0 , "Switch Theme")
        ?.setIcon(R.drawable.monitor)
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        menu?.add(0, DECREASE_SIZE, 0, "Decrease Text Size")
            ?.setIcon(android.R.drawable.btn_minus)
            ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        menu?.add(0, INCREASE_SIZE, 0, "Increase Text Size")
            ?.setIcon(android.R.drawable.btn_plus)
            ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        return true
    }
}