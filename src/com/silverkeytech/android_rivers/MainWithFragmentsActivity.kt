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

import android.os.Bundle
import android.util.Log
import android.view.View
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuItem
import org.holoeverywhere.app.Activity

enum class MainActivityMode {
    RIVER
    RSS
    COLLECTION
    PODCASTS
}

public open class MainWithFragmentsActivity(): Activity() {
    class object {
        public val TAG: String = javaClass<MainWithFragmentsActivity>().getSimpleName()
    }

    val DEFAULT_SUBSCRIPTION_LIST = "http://hobieu.apphb.com/api/1/default/riverssubscription"

    var mode: MainActivityMode = MainActivityMode.RIVER

    var currentTheme: Int? = null
    var isOnCreate: Boolean = true

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        currentTheme = this.getVisualPref().getTheme()

        isOnCreate = true
        setTheme(currentTheme!!)

        getMain().flags.reset()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_with_fragments)

        var actionBar = getSupportActionBar()!!
        actionBar.setDisplayShowHomeEnabled(false) //hide the app icon.
        actionBar.setDisplayShowTitleEnabled(true)

        showAndHide(showRiver = true)
        setTitle()
    }

    protected override fun onResume() {
        super.onResume()
        //skip if this event comes after onCreate
        if (!isOnCreate){
            Log.d(TAG, "RESUMING Current Theme $currentTheme vs ${this.getVisualPref().getTheme()}")
            //detect if there has been any theme changes
            if (currentTheme != null && currentTheme!! != this.getVisualPref().getTheme()){
                Log.d(TAG, "Theme changes detected - updating theme")
                restart()
            } else {
                showAccordingToCurrentMode()

                if (getMain().flags.isRssJustBookmarked && mode == MainActivityMode.RSS){
                    getMain().flags.reset()
                    Log.d(TAG, "RSS is just bookmarked")
                    //it doesn't do anything at the moment - this is an artifact of old design
                } else
                    if (getMain().flags.isRiverJustBookmarked && mode == MainActivityMode.RIVER){
                        getMain().flags.reset()
                        Log.d(TAG, "River is just bookmarked")
                        //it doesn't do anything at the moment - this is an artifact of old design
                    }
            }
        }else {
            Log.d(TAG, "RESUMING AFTER CREATION")
            isOnCreate = false
        }
    }

    public override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (menu != null){
            menu.findItem(R.id.main_menu_tryout)
            menu.findItem(R.id.main_menu_updates).andHide()

            val backward = menu.findItem(SWITCH_BACKWARD)
            val forward = menu.findItem(SWITCH_FORWARD)
            when(mode){
                MainActivityMode.RIVER -> {
                    backward!!.setEnabled(false)
                }
                MainActivityMode.RSS -> {
                    backward!!.setEnabled(true)
                }
                MainActivityMode.COLLECTION -> {
                    backward!!.setEnabled(true)
                }
                MainActivityMode.PODCASTS -> {
                    backward!!.setEnabled(true)
                    forward!!.setEnabled(false)
                }
                else -> {
                }
            }
        }

        return super.onPrepareOptionsMenu(menu)
    }

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = getSupportMenuInflater()!!
        inflater.inflate(R.menu.main_fragments_menu, menu)

        //fixed top menu
        menu?.add(0, SWITCH_BACKWARD, 0, "<")
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        menu?.add(0, SWITCH_FORWARD, 0, ">")
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        menu?.add(0, EXPLORE, 0, "MORE NEWS")
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        return super.onCreateOptionsMenu(menu)
    }

    fun showAndHide(showRiver: Boolean = false, showRss: Boolean = false, showCollection: Boolean = false, showPodcasts: Boolean = false) {
        val riverFragment = this.findFragmentById(R.id.main_rivers_fragment)
        val rssFragment = this.findFragmentById(R.id.main_rss_fragment)
        val collectionFragment = this.findFragmentById(R.id.main_collection_fragment)
        val podcastsFragment = this.findFragmentById(R.id.main_podcast_fragment)

        val transaction = this.beginFragmentTransaction()

        if (showRiver) transaction.show(riverFragment) else transaction.hide(riverFragment)
        if (showRss) transaction.show(rssFragment) else transaction.hide(rssFragment)
        if (showCollection) transaction.show(collectionFragment) else transaction.hide(collectionFragment)
        if (showPodcasts) transaction.show(podcastsFragment) else transaction.hide(podcastsFragment)

        transaction.commit()
    }

    fun showAccordingToCurrentMode() {
        when(mode){
            MainActivityMode.RIVER -> showAndHide(showRiver = true)
            MainActivityMode.RSS -> showAndHide(showRss = true)
            MainActivityMode.COLLECTION -> showAndHide(showCollection = true)
            MainActivityMode.PODCASTS -> showAndHide(showPodcasts = true)
            else -> {
            }
        }
    }

    fun setTitle() {
        val actionBar = getSupportActionBar()!!
        when(mode){
            MainActivityMode.RIVER -> actionBar.setTitle("Rivers")
            MainActivityMode.RSS -> actionBar.setTitle("RSS")
            MainActivityMode.COLLECTION -> actionBar.setTitle("Collections")
            MainActivityMode.PODCASTS -> actionBar.setTitle("Podcasts")
            else -> {
            }
        }
    }

    public override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.getItemId()) {
            EXPLORE -> {
                downloadOpml(this, PreferenceDefaults.CONTENT_OUTLINE_MORE_NEWS_SOURCE, "Get more news")
                return true
            }
            SWITCH_FORWARD -> {
                changeModeForward()
                displayModeContent(mode, false)
                setTitle()
                this.supportInvalidateOptionsMenu()
                return true
            }
            SWITCH_BACKWARD -> {
                changeModeBackward()
                displayModeContent(mode, false)
                setTitle()
                this.supportInvalidateOptionsMenu()
                return true
            }
            R.id.main_menu_help ->{
                downloadOpml(this, PreferenceDefaults.CONTENT_OUTLINE_HELP_SOURCE, getString(R.string.help)!!)
                return true
            }
            R.id.main_menu_tryout ->{
                startTryoutActivity(this)
                return true
            }
            else ->
                return super.onOptionsItemSelected(item)
        }
    }


    fun displayModeContent(mode: MainActivityMode, downloadRiverBookmarksFromInternetIfNoneExisted: Boolean) {
        when(mode){
            MainActivityMode.RIVER -> {
                showAndHide(showRiver = true)
            }
            MainActivityMode.RSS -> {
                showAndHide(showRss = true)
            }
            MainActivityMode.COLLECTION ->{
                showAndHide(showCollection = true)
            }
            MainActivityMode.PODCASTS -> {
                showAndHide(showPodcasts = true)
            }
            else -> {
            }
        }
    }

    //this is one directional forward
    fun changeModeForward() {
        val currentMode = mode
        mode = when (currentMode){
            MainActivityMode.RIVER -> MainActivityMode.RSS
            MainActivityMode.RSS -> MainActivityMode.COLLECTION
            MainActivityMode.COLLECTION -> MainActivityMode.PODCASTS
            else -> MainActivityMode.RIVER
        }
    }

    //this is one directional bacwkward
    fun changeModeBackward() {
        val currentMode = mode
        mode = when (currentMode){
            MainActivityMode.PODCASTS -> MainActivityMode.COLLECTION
            MainActivityMode.COLLECTION -> MainActivityMode.RSS
            MainActivityMode.RSS -> MainActivityMode.RIVER
            else -> MainActivityMode.RIVER
        }
    }

    val SWITCH_BACKWARD: Int = 0
    val SWITCH_FORWARD: Int = 1
    val EXPLORE: Int = 2

}

data class Location(val x: Int, val y: Int)

fun getLocationOnScreen(item: View): Location {
    val loc = intArray(0, 1)
    item.getLocationOnScreen(loc)
    val popupX = loc[0]
    val popupY = loc[1]

    return Location(popupX, popupY)
}