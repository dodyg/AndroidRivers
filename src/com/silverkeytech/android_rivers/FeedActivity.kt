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

    var feedBookmarked: Boolean = false

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

        feedBookmarked = checkFeedBookmarkStatus(feedUrl)
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

    var bookmarkMenu: MenuItem? = null

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, REFRESH, 0, "Refresh")
        ?.setIcon(R.drawable.ic_menu_refresh)
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)


        var inflater = getSupportMenuInflater()!!
        inflater.inflate(R.menu.feed_menu, menu)

        bookmarkMenu = menu?.findItem(R.id.feed_menu_bookmark)

        if (feedBookmarked)
            bookmarkMenu?.setVisible(false)

        return true
    }

    public override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.getItemId()){
            R.id.river_menu_refresh, REFRESH -> {
                downloadFeed()
                return true
            }
            R.id.feed_menu_bookmark -> {
                try{
                    var bk = Bookmark()
                    bk.title = feedName
                    bk.url = feedUrl
                    bk.kind = BookmarkKind.SYNDICATION.toString()
                    bk.language = feedLanguage

                    DatabaseManager.bookmark!!.create(bk)
                    bookmarkMenu?.setVisible(false)
                    toastee("$feedName syndication is added to your bookmark.")

                    return true
                }
                catch(e: Exception){
                    toastee("Sorry, we cannot add this $feedUrl", Duration.LONG)
                    return false
                }
            }
            else ->
                return super.onOptionsItemSelected(item)
        }
    }

    fun checkFeedBookmarkStatus(url: String): Boolean {
        return DatabaseManager.query().bookmark().byUrl(url).exists
    }
}