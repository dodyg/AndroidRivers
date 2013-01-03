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
import com.actionbarsherlock.app.SherlockListActivity
import com.actionbarsherlock.view.ActionMode
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuItem
import com.silverkeytech.android_rivers.db.Bookmark
import com.silverkeytech.android_rivers.db.BookmarkKind
import com.silverkeytech.android_rivers.db.DatabaseManager
import android.app.AlertDialog
import android.content.DialogInterface
import com.silverkeytech.android_rivers.db.getBookmarkCollectionFromDb
import com.silverkeytech.android_rivers.db.BookmarkCollection
import com.silverkeytech.android_rivers.db.checkIfUrlAlreadyBookmarked
import com.silverkeytech.android_rivers.db.saveBookmarkToDb

//Responsible of downloading, caching and viewing a news river content
public class FeedActivity(): SherlockListActivity()
{
    class object {
        public val TAG: String = javaClass<FeedActivity>().getSimpleName()
    }

    var feedUrl: String = ""
    var feedName: String = ""
    var feedLanguage: String = ""
    var mode: ActionMode? = null

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        setTheme(this.getVisualPref().getTheme())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feeds)

        var actionBar = getSupportActionBar()!!
        actionBar.setDisplayShowHomeEnabled(false) //hide the app icon.

        var i = getIntent()!!
        feedUrl = i.getStringExtra(Params.FEED_URL)!!
        feedName = i.getStringExtra(Params.FEED_NAME)!!
        feedLanguage = i.getStringExtra(Params.FEED_LANGUAGE)!!

        setTitle(feedName)

        downloadFeed()
    }

    fun downloadFeed(){
        DownloadFeed(this, false)
                .executeOnComplete {
            res ->
            if (res.isTrue()){
                var feed = res.value!!
                FeedContentRenderer(this, feedLanguage)
                        .handleNewsListing(feed.items)
            }else{
                toastee("Error ${res.exception?.getMessage()}", Duration.LONG)
            }
        }
                .execute(feedUrl)
    }

    val REFRESH: Int = 1

    public override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val feedBookmarked = checkIfUrlAlreadyBookmarked(feedUrl)

        val bookmarkMenu =  menu!!.findItem(R.id.feed_menu_bookmark)!!
        bookmarkMenu.setVisible(!feedBookmarked)
        return true
    }

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, REFRESH, 0, "Refresh")
        ?.setIcon(R.drawable.ic_menu_refresh)
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)

        var inflater = getSupportMenuInflater()!!
        inflater.inflate(R.menu.feed_menu, menu)

        return true
    }

    public override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.getItemId()){
            R.id.river_menu_refresh, REFRESH -> {
                downloadFeed()
                return true
            }
            R.id.feed_menu_bookmark -> {
                addBookmarkOption()
                return true
            }
            else ->
                return super.onOptionsItemSelected(item)
        }
    }

    fun saveBookmark(collection : BookmarkCollection?){
        val res = saveBookmarkToDb(feedName, feedUrl, BookmarkKind.RSS, feedLanguage, collection)

        if (res.isTrue()){
            if (collection == null)
                toastee("$feedName is bookmarked.")
            else
                toastee("$feedName is bookmarked to your ${collection.title} collection.")
        }
        else {
            toastee("Sorry, I cannot bookmark $feedUrl", Duration.LONG)
        }
    }

    fun addBookmarkOption(){
        var coll = getBookmarkCollectionFromDb()

        if (!coll.isEmpty()){

            val dialog = AlertDialog.Builder(this)

            dialog.setTitle("Bookmark to a collection")

            var collectionTitles = coll.map { it.title }.toArray(array<String>())

            dialog.setItems(collectionTitles, object : DialogInterface.OnClickListener{
                public override fun onClick(p0: DialogInterface?, p1: Int) {
                    val currentCollection = coll[p1]
                    saveBookmark(currentCollection)
                }
            } )

            var createdDialog = dialog.create()!!
            createdDialog.show()

        } else {
            saveBookmark(null)
        }
    }
}