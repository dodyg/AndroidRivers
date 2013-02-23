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

package com.silverkeytech.android_rivers.creators

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import com.silverkeytech.android_rivers.DownloadFeed
import com.silverkeytech.android_rivers.Duration
import com.silverkeytech.android_rivers.FeedContentRenderer
import com.silverkeytech.android_rivers.R
import com.silverkeytech.android_rivers.db.BookmarkCollection
import com.silverkeytech.android_rivers.db.BookmarkKind
import com.silverkeytech.android_rivers.db.SortingOrder
import com.silverkeytech.android_rivers.db.getBookmarkCollectionFromDb
import com.silverkeytech.android_rivers.db.saveBookmarkToDb
import com.silverkeytech.android_rivers.getVisualPref
import com.silverkeytech.android_rivers.isNullOrEmpty
import com.silverkeytech.android_rivers.setOnClickListener
import com.silverkeytech.android_rivers.toastee
import org.holoeverywhere.ArrayAdapter
import org.holoeverywhere.app.Activity
import org.holoeverywhere.widget.Button
import org.holoeverywhere.widget.EditText
import org.holoeverywhere.widget.Spinner
import com.silverkeytech.android_rivers.addBookmarkOption
import com.silverkeytech.android_rivers.saveBookmark
import com.silverkeytech.android_rivers.db.checkIfUrlAlreadyBookmarked

public class GoogleNewsSearchActivity () : Activity(){
    class object {
        public val TAG: String = javaClass<GoogleNewsSearchActivity>().getSimpleName()
    }

    var feedUrl: String = ""
    var feedName: String = ""
    var feedLanguage: String = ""
    var feedDateIsParseable: Boolean = false

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        setTheme(this.getVisualPref().getTheme())
        super<Activity>.onCreate(savedInstanceState)
        setContentView(R.layout.google_news_search)

        var actionBar = getSupportActionBar()!!
        actionBar.setDisplayShowHomeEnabled(false) //hide the app icon.

        setTitle("Google News Finder")

        //handle UI

        val searchTerm = findViewById(R.id.google_news_search_term)!! as EditText
        searchTerm.setHint("Optional search term")

        val regionList = findViewById(R.id.google_news_search_region)!! as Spinner

        val maps = getEditionsAndLanguages()
        val listName = maps.iterator().map { x -> x.key }.toArrayList<String>()

        val adapter = ArrayAdapter<String>(this, org.holoeverywhere.R.layout.simple_spinner_item, listName);
        adapter.setDropDownViewResource(org.holoeverywhere.R.layout.simple_spinner_dropdown_item);
        regionList.setAdapter(adapter);

        val bookmark = findViewById(R.id.google_news_search_bookmark_btn)!! as Button
        bookmark.setEnabled(false)
        bookmark.setOnClickListener{
            addBookmarkOption(this, feedDateIsParseable){
                collection ->
                    saveBookmark(this, feedName, feedUrl, feedLanguage, collection)
                    bookmark.setEnabled(false)
            }
        }

        val go = findViewById(R.id.google_news_search_go_btn)!! as Button
        go.setOnClickListener {
            val position = regionList.getSelectedItemPosition()
            val key = listName.get(position)
            val info = maps[key]!!
            feedUrl = "https://news.google.com/news/feeds?cf=all&ned=${info.edition}&hl=${info.lang}&output=rss"
            val search = searchTerm.getText()?.toString()
            if (!search.isNullOrEmpty()){
                feedUrl +="&q=${java.net.URLEncoder.encode(search!!, "UTF-8")}"
            }

            Log.d(TAG, "Downloading $feedUrl")

            feedName = if (search.isNullOrEmpty()) key else search!!

            DownloadFeed(this, false)
                    .executeOnComplete {
                res ->
                if (res.isTrue()){
                    val feed = res.value!!
                    feedDateIsParseable = feed.isDateParseable
                    if (!feed.language.isNullOrEmpty()){
                        feedLanguage = feed.language
                    }

                    if (feed.items.size > 0 && !checkIfUrlAlreadyBookmarked(feedUrl))
                        bookmark.setEnabled(true)
                    else
                        bookmark.setEnabled(false)

                    FeedContentRenderer(this, feedLanguage)
                            .handleNewsListing(R.id.google_news_search_results_lv, feedName, feedUrl, feed.items)
                }else{
                    toastee("Error ${res.exception?.getMessage()}", Duration.LONG)
                }
            }.execute(feedUrl)
        }
    }
}