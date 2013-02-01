package com.silverkeytech.android_rivers

import com.actionbarsherlock.app.SherlockListFragment
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import com.actionbarsherlock.app.SherlockListFragment
import com.silverkeytech.android_rivers.db.Bookmark
import com.silverkeytech.android_rivers.db.BookmarkCollection
import com.silverkeytech.android_rivers.db.BookmarkKind
import com.silverkeytech.android_rivers.db.DatabaseManager
import com.silverkeytech.android_rivers.db.SortingOrder
import com.silverkeytech.android_rivers.db.getBookmarkCollectionFromDb
import com.silverkeytech.android_rivers.db.getBookmarksFromDb
import com.silverkeytech.android_rivers.db.removeItemByUrlFromBookmarkDb
import com.silverkeytech.android_rivers.db.clearBookmarksFromCollection
import com.silverkeytech.android_rivers.db.getBookmarksFromDbAsOpml
import com.silverkeytech.android_rivers.db.saveOpmlAsBookmarks
import com.silverkeytech.android_rivers.outlines.Opml
import com.silverkeytech.android_rivers.outlines.Outline
import java.util.ArrayList
import com.silverkeytech.android_rivers.outlines.sortOutlineAsc
import com.silverkeytech.android_rivers.outlines.sortOutlineDesc
import com.actionbarsherlock.view.Menu
import android.view.MenuItem
import com.actionbarsherlock.view.MenuInflater
import com.silverkeytech.android_rivers.db.saveBookmarkToDb

public class RiverListFragment() : SherlockListFragment() {
    class object {
        public val TAG: String = javaClass<RiverListFragment>().getSimpleName()
    }

    val DEFAULT_SUBSCRIPTION_LIST = "http://hobieu.apphb.com/api/1/default/riverssubscription"

    var parent : Activity? = null
    var lastEnteredUrl: String? = ""

    var isFirstLoad : Boolean = true
    public override fun onAttach(activity: Activity?) {
        super<SherlockListFragment>.onAttach(activity)
        parent = activity
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super<SherlockListFragment>.onCreate(savedInstanceState)
    }

    public override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val vw = inflater!!.inflate(R.layout.river_list_fragment, container, false)

        Log.d(TAG, "We are being created")

        return vw
    }

    public override fun onStart() {
        super<SherlockListFragment>.onStart()

        val downloadIf = parent!!.getSetupPref().getDownloadDefaultRiversIfNecessary()
        displayRiverBookmarks(downloadIf)
    }

    public override fun onResume() {
        Log.d(TAG, "OnResume")
        super<SherlockListFragment>.onResume()
    }

    public override fun onHiddenChanged(hidden: Boolean) {
        Log.d(TAG, "OnHiddenChanged $hidden")

        if (!hidden && !isFirstLoad){
            displayRiverBookmarks(false)
        }

        isFirstLoad = false
        super<SherlockListFragment>.onHiddenChanged(hidden)
    }

    public override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.river_list_fragment_menu, menu)
        super<SherlockListFragment>.onCreateOptionsMenu(menu, inflater)
    }

    public override fun onOptionsItemSelected(item: com.actionbarsherlock.view.MenuItem?): Boolean {
        when(item!!.getItemId()) {
            R.id.river_list_fragment_menu_download_all_rivers -> {
                val subscriptionList = parent!!.getMain().getRiverBookmarksCache()

                if (subscriptionList != null){
                    val titleList = subscriptionList.body?.outline?.iterator()?.map { it.text!! }?.toArrayList()!!
                    val urlList = subscriptionList.body?.outline?.iterator()?.map { it.url!! }?.toArrayList()!!

                    startDownloadAllRiverService(parent!!, titleList, urlList)
                    return true
                }
                else {
                    parent!!.toastee("Sorry, there's nothing to download.", Duration.AVERAGE)
                    return true
                }
            }
            R.id.river_list_fragment_menu_show_add_dialog ->{

                return true
            }
            else -> return false
        }
    }

    public override fun onPause() {
        Log.d(TAG, "OnPause")
        super<SherlockListFragment>.onPause()
    }

    fun showMessage(msg: String) {
        val txt = getView()!!.findViewById(R.id.river_list_fragment_message_tv) as TextView
        if (msg.isNullOrEmpty()){
            txt.setVisibility(View.INVISIBLE)
            txt.setText("")
        }
        else{
            txt.setVisibility(View.VISIBLE)
            txt.setText(msg)
        }
    }

    private fun displayAddNewRiverDialog(){
        val dlg = createSingleInputDialog(parent!!, "Add new river", lastEnteredUrl, "Set url here", {
            dlg, url ->
            lastEnteredUrl = url
            Log.d(TAG, "Entered $url")
            if (url.isNullOrEmpty()){
                parent!!.toastee("Please enter url of the river")
            }
            else {
                var currentUrl = url!!
                if (!currentUrl.contains("http://"))
                    currentUrl = "http://" + currentUrl

                val u = safeUrlConvert(currentUrl)
                if (u.isTrue()){
                    DownloadRiverContent(parent!!, "en")
                            .executeOnComplete {
                        res, lang ->
                        if (res.isTrue()){
                            var river = res.value!!
                            var sortedNewsItems = river.getSortedNewsItems()
                            parent!!.getMain().setRiverCache(currentUrl, sortedNewsItems)

                            var res2 = saveBookmarkToDb(currentUrl, currentUrl, BookmarkKind.RIVER, "en", null)

                            if (res2.isTrue()){
                                parent!!.toastee("$currentUrl river is successfully bookmarked", Duration.LONG)
                                parent!!.getMain().clearRiverBookmarksCache()
                                refreshRiverBookmarks(false)
                            } else{
                                parent!!.toastee("Sorry, we cannot add this $currentUrl river", Duration.LONG)
                            }
                        }
                        else{
                            parent!!.toastee("$currentUrl is not a valid river", Duration.LONG)
                        }
                    }
                            .execute(currentUrl)

                    dlg?.dismiss()
                }else{
                    Log.d(TAG, "RIVER $currentUrl conversion generates ${u.exception?.getMessage()}")
                    parent!!.toastee("The url you entered is not valid. Please try again", Duration.LONG)
                }
            }
        })
    }

    private fun displayRiverBookmarks(retrieveDefaultFromInternet: Boolean) {
        val cache = parent!!.getMain().getRiverBookmarksCache()

        if (cache != null){
            Log.d(TAG, "Get bookmarks from cache")
            handleRiversListing(cache, parent!!.getContentPref().getRiverBookmarksSorting())
        }  else{
            Log.d(TAG, "Try to retrieve bookmarks from DB")
            val bookmarks = getBookmarksFromDbAsOpml(BookmarkKind.RIVER, SortingOrder.NONE)

            if (bookmarks.body!!.outline!!.count() > 0){
                Log.d(TAG, "Now bookmarks come from the db")
                parent!!.getMain().setRiverBookmarksCache(bookmarks)
                handleRiversListing(bookmarks, parent!!.getContentPref().getRiverBookmarksSorting())
            }
            else if (retrieveDefaultFromInternet){
                Log.d(TAG, "Start downloading bookmarks from the Internet")
                DownloadBookmarks(parent!!, true)
                        .executeOnComplete({
                    res ->
                    Log.d(TAG, "Using downloaded bookmark data from the Internt")
                    val res2 = saveOpmlAsBookmarks(res.value!!)

                    if (res2.isTrue()){
                        handleRiversListing(res2.value!!, parent!!.getContentPref().getRiverBookmarksSorting())
                        Log.d(TAG, "Bookmark data from the Internet is successfully saved")
                        parent!!.getSetupPref().setDownloadDefaultRivers(false)//we only download standard rivers from the internet by default when at setup the first time
                    }
                    else{
                        Log.d(TAG, "Saving opml bookmark to db fails ${res2.exception?.getMessage()}")
                        parent!!.toastee("Sorry, we cannot download your initial bookmarks at the moment ${res2.exception?.getMessage()}", Duration.LONG)
                    }
                })
                        .execute(DEFAULT_SUBSCRIPTION_LIST)
            }
            else{
                //this happen when you remove the last item from the river bookmark. So have it render an empty opml
                handleRiversListing(Opml(), parent!!.getContentPref().getRiverBookmarksSorting())
            }
        }
    }

    fun handleRiversListing(opml: Opml, sort: Int) {
        var outlines = ArrayList<Outline>()

        opml.body?.outline?.forEach {
            outlines.add(it)
        }

        if (outlines.count() == 0){
            showMessage(parent!!.getString(R.string.empty_river_items_list)!!)
        } else
            showMessage("")

        //perform sorting in the outlines (three choices, asc, desc and none)
        if (sort == PreferenceValue.SORT_ASC){
            outlines = sortOutlineAsc(outlines) as ArrayList<Outline>
        } else if (sort == PreferenceValue.SORT_DESC){
            outlines = sortOutlineDesc(outlines) as ArrayList<Outline>
        }

        val adapter = object : ArrayAdapter<Outline>(parent!!, android.R.layout.simple_list_item_1, android.R.id.text1, outlines){
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                val text = outlines[position].toString()
                return currentListItem(text!!, convertView, parent)
            }
        }

        val list = getView()!!.findViewById(android.R.id.list) as ListView
        list.setAdapter(adapter)

        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long) {
                val currentOutline = outlines.get(p2)

                var lang =
                        if (!currentOutline.language.isNullOrEmpty())
                            currentOutline.language!!
                        else
                            "en"

                startRiverActivity(parent!!, currentOutline.url!!, currentOutline.text!!, lang)
            }
        })

        list.setOnItemLongClickListener(object : AdapterView.OnItemLongClickListener{
            public override fun onItemLongClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long): Boolean {
                val currentOutline = outlines.get(p2)
                showRiverBookmarksQuickActionPopup(parent!!, currentOutline, p1!!, list)
                return true
            }
        })
    }


    public data class ViewHolder (var name: TextView)

    public fun currentListItem(text: String, convertView: View?, parent: ViewGroup?): View? {
        var holder: ViewHolder?

        var vw: View? = convertView

        if (vw == null){
            vw = inflater().inflate(android.R.layout.simple_list_item_1, parent, false)

            holder = ViewHolder(vw!!.findViewById(android.R.id.text1) as TextView)
            holder!!.name.setText(text)
            vw!!.setTag(holder)
        }else{
            holder = vw!!.getTag() as ViewHolder
            holder!!.name.setText(text)
        }

        return vw
    }

    fun inflater(): LayoutInflater {
        val inflater: LayoutInflater = parent!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater
    }

    fun refreshRiverBookmarks(retrieveRiversFromInternetIfNoneExisted: Boolean)
    {
        parent.getMain().clearRiverBookmarksCache()
        displayRiverBookmarks(retrieveRiversFromInternetIfNoneExisted)
    }

    fun showRiverBookmarksQuickActionPopup(context: Activity, currentOutline: Outline, item: View, list: View) {
        //overlay popup at top of clicked overview position
        val popupWidth = item.getWidth()
        val popupHeight = item.getHeight()

        val x = inflater(context).inflate(R.layout.main_river_quick_actions, null, false)!!
        val pp = PopupWindow(x, popupWidth, popupHeight, true)

        x.setBackgroundColor(android.graphics.Color.LTGRAY)

        x.setOnClickListener {
            pp.dismiss()
        }

        val removeIcon = x.findViewById(R.id.main_river_quick_action_delete_icon) as ImageView
        removeIcon.setOnClickListener {
            val dlg = createConfirmationDialog(context = context, message = "Are you sure about removing this River bookmark?", positive = {
                try{
                    val res = DatabaseManager.cmd().bookmark().deleteByUrl(currentOutline.url!!)
                    if (res.isFalse())
                        context.toastee("Error in removing this bookmark ${res.exception?.getMessage()}")
                    else {
                        context.toastee("Bookmark removed")
                        refreshRiverBookmarks(false)
                    }
                }
                catch(e: Exception){
                    context.toastee("Error in trying to remove this bookmark ${e.getMessage()}")
                }
                pp.dismiss()
            }, negative = {
                pp.dismiss()
            })

            dlg.show()
        }

        val editIcon = x.findViewById(R.id.main_river_quick_action_edit_icon) as ImageView
        editIcon.setOnClickListener {
            val dlg = createSingleInputDialog(context, "Edit River Title", currentOutline.text, "Set title here", {
                dlg, title ->
                if (title.isNullOrEmpty()){
                    context.toastee("Please enter title", Duration.LONG)
                }
                else {
                    val record = DatabaseManager.query().bookmark().byUrl(currentOutline.url!!)

                    val isLocalUrl = isLocalUrl(currentOutline.url!!)

                    if (record.exists){
                        record.value!!.title = title!!

                        try{
                            //perform this additional step so that when you modify a local RIVER, it updates the collection name as well
                            if (isLocalUrl){
                                val collectionId = extractIdFromLocalUrl(currentOutline.url!!)
                                if (collectionId != null){
                                    val collectionRecord = DatabaseManager.query().bookmarkCollection().byId(collectionId)

                                    if (collectionRecord.exists){
                                        collectionRecord.value!!.title = title
                                        DatabaseManager.bookmarkCollection!!.update(collectionRecord.value!!)
                                    }
                                }
                            }

                            DatabaseManager.bookmark!!.update(record.value!!)
                            context.toastee("Bookmark title is successfully modified")
                            refreshRiverBookmarks(false)
                            pp.dismiss()
                        }catch (ex: Exception){
                            context.toastee("Sorry, I cannot update the title of this river for the following reason ${ex.getMessage()}", Duration.LONG)
                        }
                    } else {
                        context.toastee("Sorry, I cannot update the title of this river", Duration.LONG)
                    }
                }

            })
            dlg.show()
        }

        val itemLocation = getLocationOnScreen(item)
        pp.showAtLocation(list, Gravity.TOP or Gravity.LEFT, itemLocation.x, itemLocation.y)
    }
}