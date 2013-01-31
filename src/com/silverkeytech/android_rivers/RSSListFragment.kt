package com.silverkeytech.android_rivers

import com.actionbarsherlock.app.SherlockListFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.util.Log

import android.app.Activity
import android.content.Context
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.silverkeytech.android_rivers.db.Bookmark
import com.silverkeytech.android_rivers.db.BookmarkCollection
import com.silverkeytech.android_rivers.outlines.Opml
import com.silverkeytech.android_rivers.outlines.Outline
import com.silverkeytech.android_rivers.outlines.sortOutlineAsc
import com.silverkeytech.android_rivers.outlines.sortOutlineDesc
import java.util.ArrayList
import android.view.ViewGroup.LayoutParams
import com.silverkeytech.android_rivers.db.getBookmarkCollectionFromDb
import com.silverkeytech.android_rivers.db.SortingOrder
import com.silverkeytech.android_rivers.db.getBookmarksFromDb
import com.silverkeytech.android_rivers.db.BookmarkKind
import android.widget.PopupWindow
import android.widget.ImageView
import android.app.AlertDialog
import com.silverkeytech.android_rivers.db.DatabaseManager
import android.view.Gravity
import com.silverkeytech.android_rivers.db.removeItemByUrlFromBookmarkDb


public class RssListFragment() : SherlockListFragment() {
    class object {
        public val TAG: String = javaClass<RssListFragment>().getSimpleName()
    }

    var parent : Activity? = null

    public override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val vw = inflater!!.inflate(R.layout.rss_list_fragment, container, false)

        return vw
    }


    fun showMessage(msg: String) {
        val txt = parent!!.findView<TextView>(R.id.rss_list_fragment_message_tv)
        if (msg.isNullOrEmpty()){
            txt.setVisibility(View.INVISIBLE)
            txt.setText("")
        }
        else{
            txt.setVisibility(View.VISIBLE)
            txt.setText(msg)
        }
    }

    fun handleRssListing(bookmarks: List<Bookmark>) {
        if (bookmarks.count() == 0){
            showMessage(parent!!.getString(R.string.empty_rss_items_list)!!)
        }
        else
            showMessage("")

        val adapter = object : ArrayAdapter<Bookmark>(parent, android.R.layout.simple_list_item_1, android.R.id.text1, bookmarks){
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                val text = bookmarks[position].toString()
                return currentListItem(text, convertView, parent)
            }
        }

        val list = getView()!!.findViewById(android.R.id.list) as ListView
        list.setAdapter(adapter)
        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long) {
                val bookmark = bookmarks.get(p2)
                startFeedActivity(parent!!, bookmark.url, bookmark.title, bookmark.language)
            }
        })

        list.setOnItemLongClickListener(object : AdapterView.OnItemLongClickListener{
            public override fun onItemLongClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long): Boolean {
                val currentBookmark = bookmarks.get(p2)
                showRssBookmarkQuickActionPopup(parent!!, currentBookmark, p1!!, list)
                return true
            }
        })
    }


    public override fun onAttach(activity: Activity?) {
        super<SherlockListFragment>.onAttach(activity)
        parent = activity
    }

    public override fun onHiddenChanged(hidden: Boolean) {
        Log.d(TAG, "OnHiddenChanged $hidden")
        if (!hidden){
            displayRssBookmarks()
        }
        super<SherlockListFragment>.onHiddenChanged(hidden)
    }

    fun displayRssBookmarks(){
        val bookmarks = getBookmarksFromDb(BookmarkKind.RSS, SortingOrder.ASC)
        handleRssListing(bookmarks)
    }

    public override fun onResume() {
        Log.d(TAG, "OnResume")
        super<SherlockListFragment>.onResume()
    }

    public override fun onPause() {
        Log.d(TAG, "OnPause")
        super<SherlockListFragment>.onPause()
    }

    fun showRssBookmarkQuickActionPopup(context: Activity, currentBookmark: Bookmark, item: View, list: View) {
        //overlay popup at top of clicked overview position
        val popupWidth = item.getWidth()
        val popupHeight = item.getHeight()

        val x = context.getLayoutInflater()!!.inflate(R.layout.main_feed_quick_actions, null, false)!!
        val pp = PopupWindow(x, popupWidth, popupHeight, true)

        x.setBackgroundColor(android.graphics.Color.LTGRAY)

        x.setOnClickListener {
            pp.dismiss()
        }

        val removeIcon = x.findViewById(R.id.main_feed_quick_action_delete_icon) as ImageView
        removeIcon.setOnClickListener {
            val dlg = createConfirmationDialog(context = context, message = "Are you sure about removing this RSS bookmark?", positive = {
                try{
                    val res = removeItemByUrlFromBookmarkDb(currentBookmark.url)
                    if (res.isFalse())
                        context.toastee("Error in removing this bookmark ${res.exception?.getMessage()}")
                    else {
                        context.toastee("Bookmark removed")
                        displayRssBookmarks()
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


        fun showCollectionAssignmentPopup(alreadyBelongsToACollection: Boolean) {
            var coll = getBookmarkCollectionFromDb(sortByTitleOrder = SortingOrder.ASC)

            if (coll.size == 0){
                context.toastee("Please create a collection before assigning a bookmark to it")
                pp.dismiss()
            }
            else if (coll.size == 1 && alreadyBelongsToACollection){
                context.toastee("This RSS already belongs to a collection and there is no other collection to reassign it to")
                pp.dismiss()
            }
            else {
                val dialog = AlertDialog.Builder(context)
                if (alreadyBelongsToACollection)
                    dialog.setTitle("Reassign bookmark to collection")
                else
                    dialog.setTitle("Assign bookmark to collection")

                val collectionWithoutCurrent = coll.filter { x -> x.id != currentBookmark.collection?.id }
                var collectionTitles = collectionWithoutCurrent.map { x -> x.title }.toArray(array<String>())

                dialog.setItems(collectionTitles, dlgClickListener {
                    dlg, idx ->
                    val selectedCollection = collectionWithoutCurrent[idx]

                    if (currentBookmark.collection == null){
                        currentBookmark.collection = BookmarkCollection()
                    }

                    currentBookmark.collection!!.id = selectedCollection.id

                    try{
                        DatabaseManager.bookmark!!.update(currentBookmark)
                        if (alreadyBelongsToACollection)
                            context.toastee("This RSS has been successfully reassigned to '${selectedCollection.title}' collection", Duration.LONG)
                        else
                            context.toastee("This RSS has been successfuly assigned to '${selectedCollection.title}' collection", Duration.LONG)


                    } catch(ex: Exception){
                        context.toastee("Sorry, I have problem updating this RSS bookmark record", Duration.LONG)
                    }

                    pp.dismiss()
                })

                var createdDialog = dialog.create()!!
                createdDialog.setCanceledOnTouchOutside(true)
                createdDialog.setCancelable(true)
                createdDialog.show()
            }
        }

        val editIcon = x.findViewById(R.id.main_feed_quick_action_edit_icon) as ImageView
        editIcon.setOnClickListener {
            //check if it already belongs to a collection so there is no need to download.
            val alreadyBelongsToACollection = currentBookmark.collection != null
            //do a verification that this feed can actually be part of a collection
            if (!alreadyBelongsToACollection){
                DownloadFeed(context, true)
                        .executeOnComplete {
                    res ->
                    if (res.isTrue()){
                        var feed = res.value!!
                        if (!feed.isDateParseable){
                            context.toastee("Sorry, this feed cannot belong to a collection because we cannot determine its dates", Duration.LONG)
                        }
                        else{
                            showCollectionAssignmentPopup(alreadyBelongsToACollection)
                        }
                    }else{
                        context.toastee("Error ${res.exception?.getMessage()}", Duration.LONG)
                    }
                }
                        .execute(currentBookmark.url)
            }else
                showCollectionAssignmentPopup(alreadyBelongsToACollection)

        }

        val itemLocation = getLocationOnScreen(item)
        pp.showAtLocation(list, Gravity.TOP or Gravity.LEFT, itemLocation.x, itemLocation.y)
    }


    public data class ViewHolder (var name: TextView)

    fun currentListItem(text: String, convertView: View?, parent: ViewGroup?): View? {
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

    data class Location(val x: Int, val y: Int)

    fun getLocationOnScreen(item: View): Location {
        val loc = intArray(0, 1)
        item.getLocationOnScreen(loc)
        val popupX = loc[0]
        val popupY = loc[1]

        return Location(popupX, popupY)
    }
}