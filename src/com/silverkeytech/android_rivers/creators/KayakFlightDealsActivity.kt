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

import android.os.Bundle
import android.util.Log
import com.silverkeytech.android_rivers.asyncs.DownloadFeedAsync
import com.silverkeytech.android_rivers.activities.Duration
import com.silverkeytech.android_rivers.activities.FeedContentRenderer
import com.silverkeytech.android_rivers.R
import com.silverkeytech.android_rivers.getVisualPref
import com.silverkeytech.android_rivers.setOnClickListener
import com.silverkeytech.android_rivers.activities.toastee
import org.holoeverywhere.app.Activity
import org.holoeverywhere.widget.Button
import com.silverkeytech.android_rivers.addBookmarkOption
import com.silverkeytech.android_rivers.saveBookmark
import com.silverkeytech.android_rivers.db.checkIfUrlAlreadyBookmarked
import com.silverkeytech.android_rivers.getStoredPref

public class KayakFlightDealsActivity (): Activity(){
    companion object {
        public val TAG: String = javaClass<KayakFlightDealsActivity>().getSimpleName()
    }

    var feedUrl: String = ""
    var feedName: String = ""
    var feedLanguage: String = ""
    var feedDateIsParseable: Boolean = false

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        setTheme(this.getVisualPref().theme)
        super<Activity>.onCreate(savedInstanceState)
        setContentView(R.layout.kayak_flight_deals)

        var actionBar = getSupportActionBar()!!
        actionBar.setDisplayShowHomeEnabled(false) //hide the app icon.

        setTitle(this.getString(R.string.title_kayak_flight_deals))

        //handle UI
        val airportCodes = getAirportCodes(this)
        val names = airportCodes.asSequence().map { x -> x.name }.toArrayList()

        val completion = AirportAutoComplete.getUI(this, R.id.kayak_flight_deals_area, names)!!

        //if there's a previous working city stored in preference, load it up
        val cityPref = this.getStoredPref().kayakCity
        if (cityPref != "")
            completion.setText(cityPref)

        val bookmark = findViewById(R.id.kayak_flight_deals_bookmark_btn)!! as Button
        bookmark.setEnabled(false)
        bookmark.setOnClickListener{
            addBookmarkOption(this, feedDateIsParseable) {
                collection ->
                saveBookmark(this, feedName, feedUrl, feedLanguage, collection)
                bookmark.setEnabled(false)
            }
        }

        val go = findViewById(R.id.kayak_flight_deals_go_btn)!! as Button
        go.setOnClickListener {
            val input = completion.getText().toString()

            if (input.isNullOrEmpty()){
                toastee("Please enter a city location")
            }else{

                feedName = input
                val specificCode = airportCodes.find { x -> x.name == input.trim() }?.code
                val code = if (specificCode != null) specificCode else ""

                feedUrl = "http://www.kayak.com/h/rss/buzz?code=$code"

                Log.d(TAG, "Downloading feed $feedUrl")

                //fill here
                DownloadFeedAsync(this, false)
                        .executeOnComplete {
                    res ->
                    if (res.isTrue()){
                        val feed = res.value!!
                        feedDateIsParseable = feed.isDateParseable
                        if (!feed.language.isNullOrBlank()){
                            feedLanguage = feed.language
                        }

                        //If the feed is correct, keep the city name
                        if (feed.items.size() > 0)
                            this@KayakFlightDealsActivity.getStoredPref().kayakCity = input.trim()

                        if (feed.items.size() > 0 && !checkIfUrlAlreadyBookmarked(feedUrl))
                            bookmark.setEnabled(true)
                        else
                            bookmark.setEnabled(false)

                        FeedContentRenderer(this, feedLanguage)
                                .handleNewsListing(R.id.kayak_flight_deals_results_lv, feedName, feedUrl, feed.items)
                    }else{
                        toastee("Error ${res.exception?.getMessage()}", Duration.LONG)
                    }
                }.execute(feedUrl)
            }
        }
    }
}