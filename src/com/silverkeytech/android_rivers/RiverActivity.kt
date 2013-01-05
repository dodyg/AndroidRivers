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

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.actionbarsherlock.app.SherlockListActivity
import com.actionbarsherlock.view.ActionMode
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuItem
import com.silverkeytech.android_rivers.db.BookmarkKind
import com.silverkeytech.android_rivers.db.checkIfUrlAlreadyBookmarked
import com.silverkeytech.android_rivers.db.getBookmarksUrlsFromDbByCollection
import com.silverkeytech.android_rivers.db.saveBookmarkToDb

//Responsible of downloading, caching and viewing a news river content
public class RiverActivity(): SherlockListActivity(), WithVisualModificationPanel
{
    class object {
        public val TAG: String = javaClass<RiverActivity>().getSimpleName()
    }

    var riverUrl: String = ""
    var riverName: String = ""
    var riverLanguage: String = ""
    var mode: ActionMode? = null

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        setTheme(this.getVisualPref().getTheme())
        super<SherlockListActivity>.onCreate(savedInstanceState)
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


    fun downloadRiver(riverUrl: String, ignoreCache: Boolean) {
        var cache = getApplication().getMain().getRiverCache(riverUrl)

        if (cache != null && !ignoreCache){
            Log.d(TAG, "Cache is hit for url $riverUrl")
            RiverContentRenderer(this, riverLanguage).handleNewsListing(cache!!)
        }
        else{
            if (isLocalUrl(riverUrl)){
                val id = extractIdFromLocalUrl(riverUrl)
                if (id != null){
                    Log.d(TAG, "Downloading bookmark collection $id")

                    val urls = getBookmarksUrlsFromDbByCollection(id)

                    DownloadCollectionAsRiver(this, id).executeOnCompletion {
                        url, res ->
                        if (res.isTrue()){
                            Log.d(TAG, "Downloaded ${res.value?.count()} items")
                            val sortedNews = res.value!!
                            RiverContentRenderer(this, riverLanguage).handleNewsListing(sortedNews)
                        }
                        else {
                            Log.d(TAG, "Downloading collection $id with ${urls.size} urls fails")
                        }
                    }.execute(*urls)
                }
                else{
                    Log.d(TAG, "Cannot extract id from $riverUrl")
                }
            }
            else{
                DownloadRiverContent(this, riverLanguage).execute(riverUrl)
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

    public override fun getActivity() : Activity{
        return this
    }

    public override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val riverBookmarked = checkIfUrlAlreadyBookmarked(riverUrl)

        val bookmarkMenu = menu?.findItem(R.id.river_menu_bookmark)!!
        bookmarkMenu.setVisible(!riverBookmarked)
        return true
    }

    public override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.getItemId()){
            R.id.river_menu_refresh, REFRESH -> {
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

                    this@RiverActivity.getApplication().getMain().clearRiverBookmarksCache()
                    return true
                } else{
                    toastee("Sorry, we cannot add this $riverUrl", Duration.LONG)
                    return false
                }
            }
            else ->
                return super<SherlockListActivity>.onOptionsItemSelected(item)
        }
    }
}

