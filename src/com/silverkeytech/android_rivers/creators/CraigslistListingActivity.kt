package com.silverkeytech.android_rivers.creators


import android.os.Bundle
import android.util.Log
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
import org.holoeverywhere.widget.EditText
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

        setTitle("Craigslist")

        //handle UI

        //city auto complete
        val cities = getCraigsListCities(this)
        val cityNames = cities.iterator().map { x -> x.location }.toArrayList<String>()
        val completion = CityAutoComplete.getUI(this, R.id.craigslist_listing_city, cityNames)!!

        //categories
        var categories = getCraigsListCategories(this)
        val categoryNames = categories.iterator().map { x -> x.name}.toArrayList<String>()

        val adapter = ArrayAdapter<String>(this, org.holoeverywhere.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(org.holoeverywhere.R.layout.simple_spinner_dropdown_item)

        val categoryList = findViewById(R.id.craigslist_listing_category)!! as Spinner
        categoryList.setAdapter(adapter)

        val go = findViewById(R.id.craigslist_listing__go_btn)!! as Button
        go.setOnClickListener {
        }
    }
}