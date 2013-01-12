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

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import com.actionbarsherlock.app.SherlockActivity
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuItem
import com.silverkeytech.android_rivers.db.BookmarkCollectionKind
import com.silverkeytech.android_rivers.db.BookmarkKind
import com.silverkeytech.android_rivers.db.addNewCollection
import com.silverkeytech.android_rivers.db.getBookmarkCollectionFromDb
import com.silverkeytech.android_rivers.db.getBookmarksFromDb
import com.silverkeytech.android_rivers.db.getBookmarksFromDbAsOpml
import com.silverkeytech.android_rivers.db.saveOpmlAsBookmarks
import java.io.File
import com.silverkeytech.android_rivers.db.SortingOrder
import android.app.Activity
import com.silverkeytech.android_rivers.db.saveBookmarkToDb
import com.silverkeytech.android_rivers.outlines.Opml

enum class MainActivityMode {
    RIVER
    RSS
    COLLECTION
    PODCASTS
}

public open class MainActivity(): SherlockActivity() {
    class object {
        public val TAG: String = javaClass<MainActivity>().getSimpleName()
    }

    val DEFAULT_SUBSCRIPTION_LIST = "http://hobieu.apphb.com/api/1/default/riverssubscription"

    var mode: MainActivityMode = MainActivityMode.RIVER

    var currentTheme: Int? = null
    var isOnCreate: Boolean = true

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        currentTheme = this.getVisualPref().getTheme()
        isOnCreate = true
        setTheme(currentTheme!!)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        var actionBar = getSupportActionBar()!!
        actionBar.setDisplayShowHomeEnabled(false) //hide the app icon.
        actionBar.setDisplayShowTitleEnabled(true)

        setTitle()

        var intent = getIntent()
        if(intent != null){
            val locationPath = intent!!.getStringExtra(Params.DOWNLOAD_LOCATION_PATH)
            if (!locationPath.isNullOrEmpty()){
                try{
                    startPodcastActivity(this, locationPath!!)
                }
                catch(e: Exception){
                    toastee("Sorry, I cannot play the file $locationPath. Please click Refresh on the menu option to download the news list again.", Duration.LONG)
                }
            }
            else {
                val downloadIf = getSetupPref().getDownloadDefaultRiversIfNecessary()
                Log.d(TAG, "Preference for download default rivers is $downloadIf")
                displayRiverBookmarks(downloadIf)
            }
        }
    }

    fun setTitle() {
        val actionBar = getSupportActionBar()!!
        when(mode){
            MainActivityMode.RIVER -> actionBar.setTitle("Rivers")
            MainActivityMode.RSS -> actionBar.setTitle("RSS")
            MainActivityMode.COLLECTION -> actionBar.setTitle("Collections")
            else -> {
            }
        }
    }

    //this is one directional forward
    fun changeModeForward() {
        val currentMode = mode
        mode = when (currentMode){
            MainActivityMode.RIVER -> MainActivityMode.RSS
            MainActivityMode.RSS -> MainActivityMode.COLLECTION
            MainActivityMode.COLLECTION -> MainActivityMode.PODCASTS
            else -> MainActivityMode.RIVER
        }
    }

    //this is one directional bacwkward
    fun changeModeBackward() {
        val currentMode = mode
        mode = when (currentMode){
            MainActivityMode.COLLECTION -> MainActivityMode.RSS
            MainActivityMode.RSS -> MainActivityMode.RIVER
            else -> MainActivityMode.RIVER
        }
    }

    protected override fun onResume() {
        super.onResume()
        //skip if this event comes after onCreate
        if (!isOnCreate){
            Log.d(TAG, "RESUMING Current Theme $currentTheme vs ${this.getVisualPref().getTheme()}")
            //detect if there has been any theme changes
            if (currentTheme != null && currentTheme!! != this.getVisualPref().getTheme()){
                Log.d(TAG, "Theme changes detected - updating theme")
                restart()
                return
            }

            displayModeContent(mode, false)
        }else {
            Log.d(TAG, "RESUMING AFTER CREATION")
            isOnCreate = false
        }
    }

    val SWITCH_BACKWARD: Int = 0
    val SWITCH_FORWARD: Int = 1
    val EXPLORE: Int = 2

    public override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (menu != null){
            val downloadAll = menu.findItem(R.id.main_menu_download_all_rivers)
            val newCollection = menu.findItem(R.id.main_menu_collection_add_new)
            val backward = menu.findItem(SWITCH_BACKWARD)
            val sort = menu.findItem(R.id.main_menu_sort)
            val tryout = menu.findItem(R.id.main_menu_tryout)
            tryout?.setVisible(false)

            val addDialog = menu.findItem(R.id.main_menu_show_add_dialog)

            when(mode){
                MainActivityMode.RIVER -> {
                    newCollection?.setVisible(false)
                    downloadAll?.setVisible(true)
                    backward?.setEnabled(false)
                    setSortButtonText(sort, nextSortCycle())
                    sort?.setVisible(true)
                    addDialog?.setVisible(true)
                }
                MainActivityMode.RSS -> {
                    newCollection?.setVisible(false)
                    downloadAll?.setVisible(false)
                    backward?.setEnabled(true)
                    sort?.setVisible(false)
                    addDialog?.setVisible(true)
                }
                MainActivityMode.COLLECTION -> {
                    newCollection?.setVisible(true)
                    downloadAll?.setVisible(false)
                    backward?.setEnabled(true)
                    sort?.setVisible(false)
                    addDialog?.setVisible(false)
                }
                else -> {
                }
            }
        }

        return super.onPrepareOptionsMenu(menu)
    }

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = getSupportMenuInflater()!!
        inflater.inflate(R.menu.main_menu, menu)

        //top menu

        menu?.add(0, SWITCH_BACKWARD, 0, "<")
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        menu?.add(0, SWITCH_FORWARD, 0, ">")
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        menu?.add(0, EXPLORE, 0, "MORE NEWS")
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)


        return true
    }

    fun promptNewCollection() {
        val dlg: View = this.getLayoutInflater()!!.inflate(R.layout.collection_add_new, null)!!

        //take care of color
        dlg.setDrawingCacheBackgroundColor(this.getStandardDialogBackgroundColor())

        val dialog = AlertDialog.Builder(this)
        dialog.setView(dlg)
        dialog.setTitle("Add new collection")

        var input = dlg.findViewById(R.id.collection_add_new_title_et)!! as EditText

        dialog.setPositiveButton("OK", object : DialogInterface.OnClickListener{
            public override fun onClick(p0: DialogInterface?, p1: Int) {
                val text = input.getText().toString()
                if (text.isNullOrEmpty()){
                    toastee("Please enter collection title", Duration.AVERAGE)
                    return
                }

                val res = addNewCollection(text, BookmarkCollectionKind.RIVER)

                if (res.isTrue()){
                    val url = makeLocalUrl(res.value!!.id)
                    //when a collection is added as a river, bookmark it immediately
                    saveBookmarkToDb(text, url, BookmarkKind.RIVER, "en", null)
                    getApplication().getMain().clearRiverBookmarksCache()

                    toastee("Collection is successfully added")
                    this@MainActivity.refreshBookmarkCollection()
                }
                else{
                    toastee("Sorry, I have problem adding this new collection", Duration.AVERAGE)
                }
            }
        })

        dialog.setNegativeButton("Cancel", object : DialogInterface.OnClickListener{
            public override fun onClick(p0: DialogInterface?, p1: Int) {
                p0?.dismiss()
            }
        })

        val createdDialog = dialog.create()!!
        createdDialog.show()
    }

    var lastEnteredUrl : String? = ""

    fun manageAddDialog(currentMode : MainActivityMode){
        if (currentMode == MainActivityMode.RIVER){
            val dlg = createAddSourceDialog(this, "Add new river", lastEnteredUrl, {
                dlg, url ->
                    lastEnteredUrl = url
                    Log.d(TAG, "Entered $url")
                    if (url.isNullOrEmpty()){
                        toastee("Please enter url of the river")
                    }
                    else {
                        var currentUrl = url!!
                        if (!currentUrl.contains("http://"))
                            currentUrl = "http://" + currentUrl

                        val u = safeUrlConvert(currentUrl)
                        if (u.isTrue()){
                            DownloadRiverContent(this, "en")
                            .executeOnComplete {
                                res, lang ->
                                    if (res.isTrue()){
                                        var river = res.value!!
                                        var sortedNewsItems = river.getSortedNewsItems()
                                        this@MainActivity.getApplication().getMain().setRiverCache(currentUrl, sortedNewsItems)

                                        var res2 = saveBookmarkToDb(currentUrl, currentUrl, BookmarkKind.RIVER, "en", null)

                                        if (res2.isTrue()){
                                            toastee("$currentUrl river is successfully bookmarked", Duration.LONG)
                                            this@MainActivity.getApplication().getMain().clearRiverBookmarksCache()
                                            this@MainActivity.refreshRiverBookmarks(false)
                                        } else{
                                            toastee("Sorry, we cannot add this $currentUrl river", Duration.LONG)
                                        }
                                    }
                                    else{
                                        toastee("$currentUrl is not a valid river", Duration.LONG)
                                    }
                            }
                            .execute(currentUrl)

                            dlg?.dismiss()
                        }else{
                            Log.d(TAG, "RIVER $currentUrl conversion generates ${u.exception?.getMessage()}")
                            toastee("The url you entered is not valid. Please try again", Duration.LONG)
                        }
                    }
            })

            dlg.show()
        }else if (currentMode == MainActivityMode.RSS){
            val dlg = createAddSourceDialog(this, "Add new RSS", lastEnteredUrl, {
                dlg, url ->
                lastEnteredUrl = url
                Log.d(TAG, "Entered $url")
                if (url.isNullOrEmpty()){
                    toastee("Please enter url of the river")
                }
                else {
                    var currentUrl = url!!
                    if (!currentUrl.contains("http://"))
                        currentUrl = "http://" + currentUrl

                    val u = safeUrlConvert(currentUrl)
                    if (u.isTrue()){
                        DownloadFeed(this, true)
                        .executeOnComplete {
                            res ->
                                if (res.isTrue()){
                                    var feed = res.value!!

                                    val res2 = saveBookmarkToDb(feed.title, currentUrl, BookmarkKind.RSS, feed.language, null)

                                    if (res2.isTrue()){
                                        toastee("$currentUrl is successfully bookmarked")
                                        this@MainActivity.refreshRssBookmarks()
                                    }
                                    else{
                                        toastee("Sorry, we cannot add this $currentUrl river", Duration.LONG)
                                    }
                                }else{
                                    toastee("Error ${res.exception?.getMessage()}", Duration.LONG)
                                }
                        }
                        .execute(currentUrl)
                        dlg?.dismiss()
                    }else{
                        Log.d(TAG, "RSS $currentUrl conversion generates ${u.exception?.getMessage()}")
                        toastee("The url you entered is not valid. Please try again", Duration.LONG)
                    }
                }
            })

            dlg.show()
        }
    }

    public override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.getItemId()) {
            R.id.main_menu_show_add_dialog ->{
                manageAddDialog(mode)
                return true
            }
            R.id.main_menu_sort ->{
                val nextSort = nextSortCycle()
                this.getContentPref().setRiverBookmarksSorting(nextSort)
                displayModeContent(mode, false)
                return true
            }
            R.id.main_menu_collection_add_new -> {
                promptNewCollection()
                return true
            }

            R.id.main_menu_refresh -> {
                displayModeContent(mode, true)
                return true
            }

            R.id.main_menu_tryout -> {
                var i = Intent(this, javaClass<TryOutActivity>())
                startActivity(i)
                return true
            }
            R.id.main_menu_download_all_rivers -> {
                val subscriptionList = getApplication().getMain().getRiverBookmarksCache()

                if (subscriptionList != null){
                    val titleList = subscriptionList.body?.outline?.iterator()?.map { it.text!! }?.toArrayList()!!
                    val urlList = subscriptionList.body?.outline?.iterator()?.map { it.url!! }?.toArrayList()!!

                    startDownloadAllRiverService(this, titleList, urlList)
                    return false
                }
                else {
                    toastee("Sorry, there's nothing to download.", Duration.AVERAGE)
                    return false
                }
            }
            EXPLORE -> {
                downloadOpml(this, "http://hobieu.apphb.com/api/1/opml/root", "Get more news")
                return false
            }
            SWITCH_FORWARD -> {
                changeModeForward()
                displayModeContent(mode, false)
                setTitle()
                this.supportInvalidateOptionsMenu()
                return true
            }
            SWITCH_BACKWARD -> {
                changeModeBackward()
                displayModeContent(mode, false)
                setTitle()
                this.supportInvalidateOptionsMenu()
                return true
            }
            else ->
                return super.onOptionsItemSelected(item)
        }
    }

    fun displayModeContent(mode: MainActivityMode, downloadRiverBookmarksFromInternetIfNoneExisted : Boolean) {
        when(mode){
            MainActivityMode.RIVER -> {
                displayRiverBookmarks(downloadRiverBookmarksFromInternetIfNoneExisted)
            }
            MainActivityMode.RSS -> {
                displayRssBookmarks()
            }
            MainActivityMode.COLLECTION ->{
                displayBookmarkCollection()
            }
            MainActivityMode.PODCASTS -> {
                startPodcastActivity(this)
                //we have to do this because this navigation involves two activities. Podcast activity is at the end of this navigation
                //When you are at podcast activity, you can only go back. When you click back, it will end podcast activity
                //and it will call the onresume event of MainActivity. Now we have to make sure that this activity is at its last state
                //which is MainActivityMode.COLLECTION
                this.mode = MainActivityMode.COLLECTION //this is the anchor when you return
            }
            else -> {
            }
        }
    }

    private fun displayRssBookmarks() {
        val bookmarks = getBookmarksFromDb(BookmarkKind.RSS, SortingOrder.ASC)
        BookmarksRenderer(this@MainActivity).handleBookmarkCollectionListing(bookmarks)
    }

    private fun displayBookmarkCollection() {
        val coll = getBookmarkCollectionFromDb(SortingOrder.ASC)
        BookmarksRenderer(this@MainActivity).handleCollection(coll)
    }

    public fun refreshBookmarkCollection() {
        displayBookmarkCollection()
    }

    public fun refreshRssBookmarks() {
        displayRssBookmarks()
    }

    public fun refreshRiverBookmarks(retrieveRiversFromInternetIfNoneExisted : Boolean)
    {
        this.getApplication().getMain().clearRiverBookmarksCache()
        displayRiverBookmarks(retrieveRiversFromInternetIfNoneExisted)
    }

    private fun displayRiverBookmarks(retrieveDefaultFromInternet: Boolean) {
        val cache = this.getApplication().getMain().getRiverBookmarksCache()

        if (cache != null){
            Log.d(TAG, "Get bookmarks from cache")
            BookmarksRenderer(this@MainActivity).handleRiversListing(cache, this.getContentPref().getRiverBookmarksSorting())
        }  else{
            Log.d(TAG, "Try to retrieve bookmarks from DB")
            val bookmarks = getBookmarksFromDbAsOpml(BookmarkKind.RIVER, SortingOrder.NONE)

            if (bookmarks.body!!.outline!!.count() > 0){
                Log.d(TAG, "Now bookmarks come from the db")
                this.getApplication().getMain().setRiverBookmarksCache(bookmarks)
                BookmarksRenderer(this@MainActivity).handleRiversListing(bookmarks, this.getContentPref().getRiverBookmarksSorting())
            }
            else if (retrieveDefaultFromInternet){
                Log.d(TAG, "Start downloading bookmarks from the Internet")
                DownloadBookmarks(this, true)
                        .executeOnComplete({
                    res ->
                    Log.d(TAG, "Using downloaded bookmark data from the Internt")
                    val res2 = saveOpmlAsBookmarks(res.value!!)

                    if (res2.isTrue()){
                        BookmarksRenderer(this@MainActivity).handleRiversListing(res2.value!!, this.getContentPref().getRiverBookmarksSorting())
                        Log.d(TAG, "Bookmark data from the Internet is successfully saved")
                        getSetupPref().setDownloadDefaultRivers(false)//we only download standard rivers from the internet by default when at setup the first time
                    }
                    else{
                        Log.d(TAG, "Saving opml bookmark to db fails ${res2.exception?.getMessage()}")
                        toastee("Sorry, we cannot download your initial bookmarks at the moment ${res2.exception?.getMessage()}", Duration.LONG)
                    }
                })
                        .execute(DEFAULT_SUBSCRIPTION_LIST)
            }
            else{
                //this happen when you remove the last item from the river bookmark. So have it render an empty opml
                BookmarksRenderer(this@MainActivity).handleRiversListing(Opml(), this.getContentPref().getRiverBookmarksSorting())
            }
        }
    }

    fun setSortButtonText(item : MenuItem?, sort : Int){
        when (sort){
            PreferenceValue.SORT_ASC -> item?.setTitle(this.getString(R.string.sort_asc))
            PreferenceValue.SORT_DESC -> item?.setTitle(this.getString(R.string.sort_desc))
            PreferenceValue.SORT_NONE -> item?.setTitle(this.getString(R.string.sort_cancel))
            else -> {

            }
        }
    }

    fun nextSortCycle() : Int{
        val sort = this.getContentPref().getRiverBookmarksSorting()
        return when (sort){
            PreferenceValue.SORT_ASC -> PreferenceValue.SORT_DESC
            PreferenceValue.SORT_DESC -> PreferenceValue.SORT_NONE
            PreferenceValue.SORT_NONE -> PreferenceValue.SORT_ASC
            else -> {
                PreferenceValue.SORT_ASC
            }
        }
    }
}

fun downloadOpml(context : Activity, url: String, title: String) {
    val cache = context.getApplication().getMain().getOpmlCache(url)

    if (cache != null){
        startOutlinerActivity(context, cache, title, url, false)
    }
    else{
        DownloadOpml(context)
                .executeOnProcessedCompletion({
            res ->
            if (res.isTrue()){
                startOutlinerActivity(context, res.value!!, title, url, false)
            }
            else{
                context.toastee("Downloading url fails because of ${res.exception?.getMessage()}", Duration.LONG)
            }
        }, { outline -> outline.text != "<rules>" })
                .execute(url)
    }
}