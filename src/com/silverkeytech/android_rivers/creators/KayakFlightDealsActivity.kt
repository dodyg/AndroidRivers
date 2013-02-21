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

import org.holoeverywhere.app.Activity
import org.holoeverywhere.widget.Button
import org.holoeverywhere.widget.EditText
import org.holoeverywhere.widget.Spinner
import java.util.ArrayList
import android.widget.ArrayAdapter
import com.silverkeytech.android_rivers.addBookmarkOption
import com.silverkeytech.android_rivers.saveBookmark
import com.silverkeytech.android_rivers.db.checkIfUrlAlreadyBookmarked

public class KayakFlightDealsActivity () : Activity(){
    class object {
        public val TAG: String = javaClass<KayakFlightDealsActivity>().getSimpleName()
    }

    var feedUrl: String = ""
    var feedName: String = ""
    var feedLanguage: String = ""
    var feedDateIsParseable: Boolean = false

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        setTheme(this.getVisualPref().getTheme())
        super<Activity>.onCreate(savedInstanceState)
        setContentView(R.layout.kayak_flight_deals)

        var actionBar = getSupportActionBar()!!
        actionBar.setDisplayShowHomeEnabled(false) //hide the app icon.

        setTitle("Kayak.com Flight Deals")

        //handle UI
        val airportCodes = getAirportCodes(this)
        val names = airportCodes.iterator().map { x -> x.name }.toArrayList()

        val completion = AirportAutoComplete.getUI(this, R.id.kayak_flight_deals_area, names)!!

        val bookmark = findViewById(R.id.kayak_flight_deals_bookmark_btn)!! as Button
        bookmark.setEnabled(false)
        bookmark.setOnClickListener{
            addBookmarkOption(this, feedDateIsParseable){
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
                val specificCode = airportCodes.find { x -> x.name == input.trim()}?.code
                val code = if (specificCode != null) specificCode else ""

                feedUrl = "http://www.kayak.com/h/rss/buzz?code=$code"

                Log.d(TAG, "Downloading feed $feedUrl")

                //fill here
                DownloadFeed(this, false)
                        .executeOnComplete {
                    res ->
                    if (res.isTrue()){
                        val feed = res.value!!
                        feedDateIsParseable = feed.isDateParseable
                        if (!feed.language.isNullOrEmpty()){
                            feedLanguage = feed.language
                        }

                        if (feed.items.size > 0  && !checkIfUrlAlreadyBookmarked(feedUrl))
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