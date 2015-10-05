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
import com.actionbarsherlock.view.ActionMode
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuItem
import com.silverkeytech.android_rivers.db.BookmarkKind
import com.silverkeytech.android_rivers.db.checkIfUrlAlreadyBookmarked
import com.silverkeytech.android_rivers.db.getBookmarksUrlsFromDbByCollection
import com.silverkeytech.android_rivers.db.saveBookmarkToDb
import com.silverkeytech.news_engine.riverjs.RiverItemMeta
import com.silverkeytech.news_engine.riverjs.RiverItemSource
import java.util.ArrayList
import org.holoeverywhere.app.Activity
import org.holoeverywhere.app.ListActivity
import com.silverkeytech.android_rivers.R
import com.silverkeytech.android_rivers.Params
import com.silverkeytech.android_rivers.WithVisualModificationPanel
import com.silverkeytech.android_rivers.getVisualPref
import com.silverkeytech.android_rivers.asyncs.DownloadCollectionAsRiverAsync
import com.silverkeytech.android_rivers.asyncs.DownloadRiverContentAsync
import com.silverkeytech.android_rivers.asyncs.downloadOpmlAsync
import com.silverkeytech.android_rivers.startRiverSourcesActivity
import com.silverkeytech.android_rivers.PreferenceDefaults
import com.silverkeytech.android_rivers.ResizeTextActionMode
import com.silverkeytech.android_rivers.isLocalUrl
import com.silverkeytech.android_rivers.extractIdFromLocalUrl
import com.silverkeytech.news_engine.riverjs.getSortedNewsItems

//Responsible of downloading, caching and viewing a news river content
public class RiverActivity(): ListActivity(), WithVisualModificationPanel
{
    companion object {
        public val TAG: String = javaClass<RiverActivity>().getSimpleName()
    }

    var riverUrl: String = ""
    var riverName: String = ""
    var riverLanguage: String = ""
    var mode: ActionMode? = null

    var currentTheme: Int? = null
    var isOnCreate: Boolean = true

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        currentTheme = this.getVisualPref().theme
        isOnCreate = true
        setTheme(currentTheme!!)

        super<ListActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.river)

        val actionBar = getSupportActionBar()!!
        actionBar.setDisplayShowHomeEnabled(false) //hide the app icon.

        val i = getIntent()!!
        riverUrl = i.getStringExtra(Params.RIVER_URL)!!
        riverName = i.getStringExtra(Params.RIVER_NAME)!!
        riverLanguage = i.getStringExtra(Params.RIVER_LANGUAGE)!!

        setTitle(riverName)

        downloadRiver(riverUrl, false)
    }

    protected override fun onResume() {
        super<ListActivity>.onResume()
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

    var sortedNewsItems: List<RiverItemMeta>? = null

    fun downloadRiver(riverUrl: String, ignoreCache: Boolean) {
        var cache = getMain().getRiverCache(riverUrl)

        if (cache != null && !ignoreCache){
            sortedNewsItems = cache!!
            Log.d(TAG, "Cache is hit for url $riverUrl")
            RiverContentRenderer(this, riverLanguage).handleNewsListing(cache!!)
        }
        else{
            if (isLocalUrl(riverUrl)){
                val id = extractIdFromLocalUrl(riverUrl)
                if (id != null){
                    Log.d(TAG, "Downloading bookmark collection $id")

                    val urls = getBookmarksUrlsFromDbByCollection(id)

                    DownloadCollectionAsRiverAsync(this, id).executeOnCompletion {
                        url, res ->
                        if (res.isTrue()){
                            Log.d(TAG, "Downloaded ${res.value?.count()} items")
                            sortedNewsItems = res.value!!
                            RiverContentRenderer(this, riverLanguage).handleNewsListing(sortedNewsItems!!)
                        }
                        else {
                            Log.d(TAG, "Downloading collection $id with ${urls.size()} urls fails")
                        }
                    }.execute(*(urls as Array<String?>))
                }
                else{
                    Log.d(TAG, "Cannot extract id from $riverUrl")
                }
            }
            else{
                DownloadRiverContentAsync(this, riverLanguage)
                        .executeOnComplete {
                    res, lang ->
                    var river = res.value!!
                    sortedNewsItems = river.getSortedNewsItems()

                    this@RiverActivity.getMain().setRiverCache(riverUrl, sortedNewsItems!!)
                    RiverContentRenderer(this@RiverActivity, lang).handleNewsListing(sortedNewsItems!!)
                }.execute(riverUrl)
            }
        }
    }

    public override fun onBackPressed() {
        Log.d(TAG, "Exit River News Listing")
        finish()
    }

    val REFRESH: Int = 1
    val RESIZE_TEXT: Int = 2


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

    public override fun refreshContent() {
        downloadRiver(riverUrl, false)
    }

    public override fun getActivity(): Activity {
        return this
    }

    public override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val riverBookmarked = checkIfUrlAlreadyBookmarked(riverUrl)

        val bookmarkMenu = menu?.findItem(R.id.river_menu_bookmark)!!
        bookmarkMenu.setVisible(!riverBookmarked)
        return true
    }

    //Collect river sources into a list and make sure they are unique (based on the source uris)
    fun collectRiverSources() {
        if (sortedNewsItems != null && sortedNewsItems!!.size() > 0){
            val distinct = ArrayList<RiverItemSource>();
            var itm = sortedNewsItems!!.get(0).source
            sortedNewsItems!!.map { x -> x.source }.sortedBy { x -> x.uri!! }.forEach { x ->
                if (itm.uri != x.uri && !distinct.any { y -> y.uri == x.uri }){
                    Log.d("River Sources Collection", "${itm.uri}")
                    distinct.add(itm)
                    itm = x
                }
            }

            Log.d(TAG, "Unique sources are ${distinct.size()}")

            val sortedDistinct = distinct.sortedBy { x -> x.title!! }

            startRiverSourcesActivity(this, riverName, riverUrl,
                    ArrayList(sortedDistinct.map { x -> x.title!! }),
                    ArrayList(sortedDistinct.map { x -> x.uri!! }))
        }
        else{
            Log.d(TAG, "There are no sources")
        }
    }

    public override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.getItemId()){
            R.id.river_menu_sources -> {
                collectRiverSources()
                return false
            }

            R.id.river_menu_help -> {
                downloadOpmlAsync(this, PreferenceDefaults.CONTENT_OUTLINE_HELP_SOURCE, getString(R.string.help))
                return true
            }
            REFRESH -> {
                downloadRiver(riverUrl, true)
                return true
            }
            RESIZE_TEXT -> {
                mode = startActionMode(ResizeTextActionMode(this, mode))
                return true
            }
            R.id.river_menu_bookmark -> {
                var res = saveBookmarkToDb(riverName, riverUrl, BookmarkKind.RIVER, riverLanguage, null)

                if (res.isTrue()){
                    toastee("$riverName is added to your bookmark.")
                    this@RiverActivity.getMain().clearRiverBookmarksCache()
                    return true
                } else{
                    toastee("Sorry, we cannot add this $riverUrl", Duration.LONG)
                    return false
                }
            }
            else ->
                return super<ListActivity>.onOptionsItemSelected(item)
        }
    }
}

