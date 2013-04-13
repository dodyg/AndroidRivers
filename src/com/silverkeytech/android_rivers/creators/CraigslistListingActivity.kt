package com.silverkeytech.android_rivers.creators

import android.os.Bundle
import com.silverkeytech.android_rivers.DownloadFeed
import com.silverkeytech.android_rivers.Duration
import com.silverkeytech.android_rivers.FeedContentRenderer
import com.silverkeytech.android_rivers.R
import com.silverkeytech.android_rivers.getVisualPref
import com.silverkeytech.android_rivers.isNullOrEmpty
import com.silverkeytech.android_rivers.setOnClickListener
import com.silverkeytech.android_rivers.toastee
import org.holoeverywhere.ArrayAdapter
import org.holoeverywhere.app.Activity
import org.holoeverywhere.widget.Button
import org.holoeverywhere.widget.Spinner
import com.silverkeytech.android_rivers.addBookmarkOption
import com.silverkeytech.android_rivers.saveBookmark
import com.silverkeytech.android_rivers.db.checkIfUrlAlreadyBookmarked

public class CraigslistListingActivity (): Activity(){
    class object {
        public val TAG: String = javaClass<CraigslistListingActivity>().getSimpleName()
    }

    var feedUrl: String = ""
    var feedName: String = ""
    var feedLanguage: String = ""
    var feedDateIsParseable: Boolean = false

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        setTheme(this.getVisualPref().getTheme())
        super<Activity>.onCreate(savedInstanceState)
        setContentView(R.layout.craigslist_listing)

        var actionBar = getSupportActionBar()!!
        actionBar.setDisplayShowHomeEnabled(false) //hide the app icon.

        setTitle(this.getString(R.string.title_craigslist))

        //handle UI

        //city auto complete
        val cities = getCraigsListCities(this)
        val cityNames = cities.iterator().map { x -> x.location }.toArrayList<String>()
        val completion = CityAutoComplete.getUI(this, R.id.craigslist_listing_city, cityNames)!!

        //categories
        var categories = getCraigsListCategories(this)
        val categoryNames = categories.iterator().map { x -> x.name }.toArrayList<String>()

        val adapter = ArrayAdapter<String>(this, org.holoeverywhere.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(org.holoeverywhere.R.layout.simple_spinner_dropdown_item)

        val categoryList = findViewById(R.id.craigslist_listing_category)!! as Spinner
        categoryList.setAdapter(adapter)


        val bookmark = findViewById(R.id.craigslist_listing_bookmark_btn)!! as Button
        bookmark.setEnabled(false)

        bookmark.setOnClickListener{
            addBookmarkOption(this, feedDateIsParseable) {
                collection ->
                saveBookmark(this, feedName, feedUrl, feedLanguage, collection)
                bookmark.setEnabled(false)
            }
        }

        val go = findViewById(R.id.craigslist_listing__go_btn)!! as Button
        go.setOnClickListener {

            val input = completion.getText().toString()

            if (input.isNullOrEmpty()){
                toastee("Please enter a city location")
            }else{
                val chosenCityUrl = cities.find { x -> x.location == input.trim() }?.url
                val cityUrl = if (chosenCityUrl != null) chosenCityUrl else ""

                //get selected categories
                val catPosition = categoryList.getSelectedItemPosition()
                val categoryCode = categories.get(catPosition).code

                feedUrl = "$cityUrl/$categoryCode/index.rss"

                DownloadFeed(this, false)
                        .executeOnComplete {
                    res ->
                    if (res.isTrue()){
                        val feed = res.value!!
                        feedDateIsParseable = feed.isDateParseable
                        if (!feed.language.isNullOrEmpty()){
                            feedLanguage = feed.language
                        }

                        feedName = feed.title

                        if (feed.items.size > 0 && !checkIfUrlAlreadyBookmarked(feedUrl))
                            bookmark.setEnabled(true)
                        else
                            bookmark.setEnabled(false)

                        FeedContentRenderer(this, feedLanguage)
                                .handleNewsListing(R.id.craigslist_listing_results_lv, feedName, feedUrl, feed.items)
                    }else{
                        toastee("Error ${res.exception?.getMessage()}", Duration.LONG)
                    }
                }.execute(feedUrl)
            }
        }
    }
}