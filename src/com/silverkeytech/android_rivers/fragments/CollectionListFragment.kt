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

package com.silverkeytech.android_rivers.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuInflater
import com.silverkeytech.android_rivers.db.BookmarkCollection
import com.silverkeytech.android_rivers.db.BookmarkCollectionKind
import com.silverkeytech.android_rivers.db.BookmarkKind
import com.silverkeytech.android_rivers.db.DatabaseManager
import com.silverkeytech.android_rivers.db.SortingOrder
import com.silverkeytech.android_rivers.db.addNewCollection
import com.silverkeytech.android_rivers.db.clearBookmarksFromCollection
import com.silverkeytech.android_rivers.db.getBookmarkCollectionFromDb
import com.silverkeytech.android_rivers.db.removeItemByUrlFromBookmarkDb
import com.silverkeytech.android_rivers.db.saveBookmarkToDb
import org.holoeverywhere.LayoutInflater
import org.holoeverywhere.app.Activity
import org.holoeverywhere.app.AlertDialog
import com.silverkeytech.android_rivers.R
import com.silverkeytech.android_rivers.makeLocalUrl
import com.silverkeytech.android_rivers.currentTextViewItem
import com.silverkeytech.android_rivers.getVisualPref
import com.silverkeytech.android_rivers.activities.getStandardDialogBackgroundColor
import com.silverkeytech.android_rivers.handleFontResize
import com.silverkeytech.android_rivers.startRiverActivity
import com.silverkeytech.android_rivers.createConfirmationDialog
import com.silverkeytech.android_rivers.activities.getMain
import com.silverkeytech.android_rivers.activities.getLocationOnScreen
import com.silverkeytech.android_rivers.startCollectionActivity
import com.silverkeytech.android_rivers.activities.Duration
import com.silverkeytech.android_rivers.activities.toastee
import com.silverkeytech.android_rivers.findView

public class CollectionListFragment: MainListFragment() {
    companion object {
        public val TAG: String = javaClass<CollectionListFragment>().getSimpleName()
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super<MainListFragment>.onCreate(savedInstanceState)
    }

    public override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val vw = inflater!!.inflate(R.layout.collection_list_fragment, container, false)

        return vw
    }

    public override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.collection_list_fragment_menu, menu)
        super<MainListFragment>.onCreateOptionsMenu(menu, inflater)
    }

    public override fun onOptionsItemSelected(item: com.actionbarsherlock.view.MenuItem?): Boolean {
        when(item!!.getItemId()) {
            R.id.collection_list_fragment_menu_add_new -> {
                showAddNewCollectionDialog()
                return true
            }
            else -> return false
        }
    }

    public override fun onHiddenChanged(hidden: Boolean) {
        Log.d(TAG, "OnHiddenChanged $hidden")
        if (!hidden){
            displayBookmarkCollection()
        }
        super<MainListFragment>.onHiddenChanged(hidden)
    }

    fun showMessage(msg: String) {
        val txt = getView().findView<TextView>(R.id.collection_list_fragment_message_tv)
        if (msg.isNullOrBlank()){
            txt.setVisibility(View.INVISIBLE)
            txt.setText("")
        }
        else{
            val textSize = parent.getVisualPref().listTextSize
            txt.setVisibility(View.VISIBLE)
            handleFontResize(txt, msg, textSize.toFloat())
        }
    }

    private fun displayBookmarkCollection() {
        val coll = getBookmarkCollectionFromDb(SortingOrder.ASC)
        handleCollection(coll)
    }

    fun handleCollection(coll: List<BookmarkCollection>) {
        if (coll.count() == 0){
            showMessage("""Collection is useful to group your feeds from 'more news'. Each collection presents the news in one river of news. Use menu option to add a new collection.""")
        }
        else
            showMessage("")

        val textSize = parent.getVisualPref().listTextSize

        val adapter = object : ArrayAdapter<BookmarkCollection>(parent, android.R.layout.simple_list_item_1, android.R.id.text1, coll){
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val text = coll[position].toString()
                return currentTextViewItem(text, convertView, parent, textSize.toFloat(), false, this@CollectionListFragment.getLayoutInflater()!!)
            }
        }

        val list = getView().findView<ListView>(android.R.id.list)
        list.setAdapter(adapter)
        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>, p1: View, p2: Int, p3: Long) {
                val current = coll[p2]
                //make the local url and delegate to riveractivity to figure out whether
                //to use this collection data from cache or perform the arduous task of
                //downloading and transforming rss feeds into river
                val localUrl = makeLocalUrl(current.id)
                startRiverActivity(parent, localUrl, current.title, "en")
            }
        })

        list.setOnItemLongClickListener(object : AdapterView.OnItemLongClickListener{
            public override fun onItemLongClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long): Boolean {
                val current = coll[p2]
                showCollectionQuickActionPopup(parent, current, p1!!, list)
                return true
            }
        })
    }

    fun showCollectionQuickActionPopup(context: Activity, collection: BookmarkCollection, item: View, list: View) {
        //overlay popup at top of clicked overview position
        val popupWidth = item.getWidth()
        val popupHeight = item.getHeight()

        val x = context.getLayoutInflater().inflate(R.layout.main_collection_quick_actions, null, false)!!
        val pp = PopupWindow(x, popupWidth, popupHeight, true)

        x.setBackgroundColor(android.graphics.Color.LTGRAY)

        x.setOnClickListener {
            pp.dismiss()
        }

        val delete = x.findView<ImageView>(R.id.main_collection_quick_action_delete_icon)
        delete.setOnClickListener {
            val dlg = createConfirmationDialog(context = context, message = "Are you sure about deleting this collection?", positive = {
                try{
                    Log.d("showCollectionQuickActionPopup", "Start clearing bookmarks from collection ${collection.id}")
                    clearBookmarksFromCollection(collection.id)
                    Log.d("showCollectionQuickActionPopup", "Start deleting collection ${collection.id}")

                    val res = DatabaseManager.cmd().bookmarkCollection().deleteById(collection.id)
                    if (res.isFalse())
                        context.toastee("Error in removing this bookmark collection ${res.exception?.getMessage()}")
                    else {
                        //assume that this collection is bookmarked. So remove the id from the bookmark
                        //and refresh the cache so when user view the rivers bookmark view, it is already removed
                        //This operation doesn't fail even if the collection was never added to the RIVER bookmark
                        val url = makeLocalUrl(collection.id)
                        removeItemByUrlFromBookmarkDb(url)
                        context.getMain().clearRiverBookmarksCache()
                        context.toastee("Bookmark collection removed")
                        displayBookmarkCollection()
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

        val edit = x.findView<ImageView>(R.id.main_collection_quick_action_edit_icon)
        edit.setOnClickListener{
            pp.dismiss()
            startCollectionActivity(context, collection.id, collection.title)
        }

        val itemLocation = getLocationOnScreen(item)
        pp.showAtLocation(list, Gravity.TOP or Gravity.LEFT, itemLocation.x, itemLocation.y)
    }


    fun showAddNewCollectionDialog() {
        val dlg: View = parent.getLayoutInflater().inflate(R.layout.collection_add_new, null)!!

        //take care of color
        dlg.setDrawingCacheBackgroundColor(parent.getStandardDialogBackgroundColor())

        val dialog = AlertDialog.Builder(parent)
        dialog.setView(dlg)
        dialog.setTitle("Add new collection")

        var input = dlg.findView<EditText>(R.id.collection_add_new_title_et)

        dialog.setPositiveButton("OK", object : DialogInterface.OnClickListener{
            public override fun onClick(p0: DialogInterface, p1: Int) {
                val text = input.getText().toString()
                if (text.isNullOrEmpty()){
                    parent.toastee("Please enter collection title", Duration.AVERAGE)
                    return
                }

                val res = addNewCollection(text, BookmarkCollectionKind.RIVER)

                if (res.isTrue()){
                    val url = makeLocalUrl(res.value!!.id)
                    //when a collection is added as a river, bookmark it immediately
                    saveBookmarkToDb(text, url, BookmarkKind.RIVER, "en", null)
                    parent.getMain().clearRiverBookmarksCache()

                    parent.toastee("Collection is successfully added")
                    displayBookmarkCollection()
                }
                else{
                    parent.toastee("Sorry, I have problem adding this new collection", Duration.AVERAGE)
                }
            }
        })

        dialog.setNegativeButton("Cancel", object : DialogInterface.OnClickListener{
            public override fun onClick(p0: DialogInterface, p1: Int) {
                p0.dismiss()
            }
        })

        val createdDialog = dialog.create()!!
        createdDialog.show()
    }
}