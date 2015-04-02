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

package com.silverkeytech.android_rivers.activities

import android.os.Bundle
import android.util.Log
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuItem
import org.holoeverywhere.app.ListActivity
import com.silverkeytech.android_rivers.R
import com.silverkeytech.android_rivers.Params
import com.silverkeytech.android_rivers.PreferenceDefaults
import com.silverkeytech.android_rivers.getVisualPref
import com.silverkeytech.android_rivers.asyncs.downloadOpmlAsync

public open class RiverSourcesActivity(): ListActivity() {
    companion object {
        public val TAG: String = javaClass<RiverSourcesActivity>().getSimpleName()
    }

    var riverTitle: String = ""
    var riverUri: String = ""

    var sourcesUrls: List<String> ? = null
    var sourcesTitles: List<String> ? = null


    var currentTheme: Int? = null
    var isOnCreate: Boolean = true


    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        currentTheme = this.getVisualPref().theme
        isOnCreate = true
        setTheme(currentTheme!!)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.river_sources)

        var i = getIntent()!!
        riverTitle = i.getStringExtra(Params.RIVER_SOURCES_RIVER_TITLE)!!
        riverUri = i.getStringExtra(Params.RIVER_SOURCES_RIVER_URI)!!

        sourcesTitles = i.getStringArrayListExtra(Params.RIVER_SOURCES_TITLES)
        sourcesUrls = i.getStringArrayListExtra(Params.RIVER_SOURCES_URIS)

        var actionBar = getSupportActionBar()!!
        actionBar.setDisplayShowHomeEnabled(false) //hide the app icon.
        actionBar.setDisplayShowTitleEnabled(true)
        actionBar.setTitle("Sources of River '" + riverTitle + "'")

        displaySources()
    }


    protected override fun onResume() {
        super.onResume()
        //skip if this event comes after onCreate
        if (!isOnCreate){
            Log.d(TAG, "RESUMING Current Theme $currentTheme vs ${this.getVisualPref().theme}")
            //detect if there has been any theme changes
            if (currentTheme != null && currentTheme!! != this.getVisualPref().theme){
                Log.d(TAG, "Theme changes detected - updating theme")
                restart()
                return
            }
        }else {
            Log.d(TAG, "RESUMING AFTER CREATION")
            isOnCreate = false
        }
    }

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater = getSupportMenuInflater()!!
        inflater.inflate(R.menu.river_sources_menu, menu)
        return true
    }

    public override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.getItemId()){
            R.id.river_sources_menu_help -> {
                downloadOpmlAsync(this, PreferenceDefaults.CONTENT_OUTLINE_HELP_SOURCE, getString(R.string.help))
                return true
            }
            else -> {
                return false
            }
        }
    }

    public fun refreshSources() {
        displaySources()
    }

    fun displaySources() {
        RiverSourcesRenderer(this, "en").handleListing(sourcesTitles!!, sourcesUrls!!)
    }
}