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
            addBookmarkOption()
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

                        if (feed.items.size > 0)
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

    //Note: This is a duplication with FeedActivity
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

    //Note: This is a duplication with FeedActivity
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
}