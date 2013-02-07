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

import org.holoeverywhere.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import com.actionbarsherlock.view.ActionMode
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuItem
import com.silverkeytech.android_rivers.db.BookmarkCollection
import com.silverkeytech.android_rivers.db.BookmarkKind
import com.silverkeytech.android_rivers.db.SortingOrder
import com.silverkeytech.android_rivers.db.checkIfUrlAlreadyBookmarked
import com.silverkeytech.android_rivers.db.getBookmarkCollectionFromDb
import com.silverkeytech.android_rivers.db.saveBookmarkToDb
import org.holoeverywhere.app.ListActivity

//Responsible of downloading, caching and viewing a news river content
public class FeedActivity(): ListActivity(), WithVisualModificationPanel
{
    class object {
        public val TAG: String = javaClass<FeedActivity>().getSimpleName()
    }

    var feedUrl: String = ""
    var feedName: String = ""
    var feedLanguage: String = ""
    var mode: ActionMode? = null

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        //setTheme(this.getVisualPref().getTheme())
        setTheme(R.style.Holo_Theme_Light)
        super<ListActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.feeds)

        var actionBar = getSupportActionBar()!!
        actionBar.setDisplayShowHomeEnabled(false) //hide the app icon.

        var i = getIntent()!!
        feedUrl = i.getStringExtra(Params.FEED_URL)!!
        feedName = i.getStringExtra(Params.FEED_NAME)!!
        feedLanguage = i.getStringExtra(Params.FEED_LANGUAGE)!!

        setTitle(feedName)

        downloadFeed(false)
    }

    var feedDateIsParseable: Boolean = false

    fun downloadFeed(ignoreCache: Boolean) {
        DownloadFeed(this, ignoreCache)
                .executeOnComplete {
            res ->
            if (res.isTrue()){
                var feed = res.value!!
                feedDateIsParseable = feed.isDateParseable
                Log.d(TAG, "$feedUrl is parseable = $feedDateIsParseable")
                if (!feed.language.isNullOrEmpty()){
                    Log.d(TAG, "Obtained feed language is ${feed.language}")
                    feedLanguage = feed.language
                }
                FeedContentRenderer(this, feedLanguage)
                        .handleNewsListing(feedName, feedUrl, feed.items)
            }else{
                toastee("Error ${res.exception?.getMessage()}", Duration.LONG)
            }
        }
                .execute(feedUrl)
    }

    val REFRESH: Int = 1
    val RESIZE_TEXT: Int = 2


    public override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val feedBookmarked = checkIfUrlAlreadyBookmarked(feedUrl)

        val bookmarkMenu = menu!!.findItem(R.id.feed_menu_bookmark)!!
        bookmarkMenu.setVisible(!feedBookmarked)
        return true
    }

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, RESIZE_TEXT, 0, "Resize Text")
        ?.setIcon(android.R.drawable.ic_menu_preferences)
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)

        menu?.add(0, REFRESH, 0, "Refresh")
        ?.setIcon(R.drawable.ic_menu_refresh)
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)

        var inflater = getSupportMenuInflater()!!
        inflater.inflate(R.menu.feed_menu, menu)

        return true
    }

    public override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.getItemId()){
            R.id.feed_menu_help -> {
                downloadOpml(this, PreferenceDefaults.CONTENT_OUTLINE_HELP_SOURCE, getString(R.string.help)!!)
                return true
            }
            REFRESH -> {
                downloadFeed(true)
                return true
            }
            RESIZE_TEXT -> {
                //mode = startActionMode(ResizeTextActionMode(this, mode))
                return true
            }
            R.id.feed_menu_bookmark -> {
                addBookmarkOption()
                return true
            }
            else ->
                return super<ListActivity>.onOptionsItemSelected(item)
        }
    }

    fun saveBookmark(collection: BookmarkCollection?) {
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

    fun addBookmarkOption() {
        var coll = getBookmarkCollectionFromDb(sortByTitleOrder = SortingOrder.ASC)

        if (!coll.isEmpty()){
            if (feedDateIsParseable){
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Bookmark to a collection")

                val collectionTitles = coll.map { it.title }.toArray()
                val withInstruction = arrayListOf(getString(R.string.skip_collection), *collectionTitles).toArray(array<String>())

                dialog.setItems(withInstruction, object : DialogInterface.OnClickListener{
                    public override fun onClick(p0: DialogInterface?, p1: Int) {
                        if (p1 == 0) {
                            //skip adding bookmark to collection. Just add to RSS bookmark
                            saveBookmark(null)
                        } else {
                            val currentCollection = coll[p1 - 1] //we added one extra item that was "no instruction" so the index must be deducted by 1
                            saveBookmark(currentCollection)
                        }
                        this@FeedActivity.getMain().flags.isRssJustBookmarked = true
                    }
                })

                var createdDialog = dialog.create()!!
                createdDialog.setCanceledOnTouchOutside(true)
                createdDialog.setCancelable(true)
                createdDialog.show()
            }
            else{
                toastee("This feed's date cannot be determined so it is bookmarked without collection immediately", Duration.LONG)
                saveBookmark(null)
            }

        } else {
            //no collection is available so save it straight to bookmark
            saveBookmark(null)
        }
    }


    override fun getActivity(): Activity {
        return this
    }

    override fun refreshContent() {
        downloadFeed(false)
    }
}