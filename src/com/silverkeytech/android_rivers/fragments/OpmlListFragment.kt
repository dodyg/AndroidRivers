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

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuInflater
import com.silverkeytech.android_rivers.db.getBookmarksFromDb
import org.holoeverywhere.LayoutInflater
import org.holoeverywhere.app.Activity
import com.silverkeytech.android_rivers.db.Bookmark
import com.silverkeytech.android_rivers.db.BookmarkKind
import com.silverkeytech.android_rivers.db.SortingOrder
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import android.view.Gravity
import com.silverkeytech.android_rivers.db.DatabaseManager
import com.silverkeytech.android_rivers.db.saveBookmarkToDb
import com.silverkeytech.android_rivers.R
import com.silverkeytech.android_rivers.currentTextViewItem
import com.silverkeytech.android_rivers.getVisualPref
import com.silverkeytech.android_rivers.handleFontResize
import com.silverkeytech.android_rivers.activities.Duration
import com.silverkeytech.android_rivers.Result
import com.silverkeytech.android_rivers.None
import com.silverkeytech.android_rivers.activities.getLocationOnScreen
import com.silverkeytech.android_rivers.activities.toastee
import com.silverkeytech.android_rivers.asyncs.DownloadOpmlAsync
import com.silverkeytech.android_rivers.createConfirmationDialog
import com.silverkeytech.android_rivers.createSingleInputDialog
import com.silverkeytech.android_rivers.safeUrlConvert
import com.silverkeytech.android_rivers.asyncs.downloadOpmlAsync
import com.silverkeytech.android_rivers.tryGetUriFromClipboard
import com.silverkeytech.android_rivers.findView

public class OpmlListFragment(): MainListFragment() {
    companion object {
        public val TAG: String = javaClass<OpmlListFragment>().getSimpleName()
    }

    var lastEnteredUrl: String? = ""
    var isFirstLoad: Boolean = true

    public override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super<MainListFragment>.onCreate(savedInstanceState)
    }

    public override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val vw = inflater!!.inflate(R.layout.opml_list_fragment, container, false)

        Log.d(TAG, "We are being created")

        return vw
    }

    public override fun onResume() {
        Log.d(TAG, "OnResume")

        if (getUserVisibleHint()){
            Log.d(TAG, "OnResume - OpmlListFragment visible")
            displayOpmlList()
        }

        super<MainListFragment>.onResume()
    }

    public override fun onHiddenChanged(hidden: Boolean) {
        Log.d(TAG, "OnHiddenChanged $hidden")

        if (!hidden && !isFirstLoad){
            displayOpmlList()
        }

        isFirstLoad = false
        super<MainListFragment>.onHiddenChanged(hidden)
    }

    public override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.opml_list_fragment_menu, menu)
        super<MainListFragment>.onCreateOptionsMenu(menu, inflater)
    }

    public override fun onOptionsItemSelected(item: com.actionbarsherlock.view.MenuItem?): Boolean {
        when(item!!.getItemId()) {
            R.id.opml_list_fragment_menu_show_add_dialog -> {
                displayAddNewOpmlDialog()
                return false
            }
            else -> return false
        }
    }

    private fun displayOpmlList() {
        val opmls = getBookmarksFromDb(BookmarkKind.OPML, SortingOrder.ASC)

        handleOpmlListing(opmls)
    }

    fun handleOpmlListing(bookmarks: List<Bookmark>) {

        if (bookmarks.size() == 0)
            showMessage(parent.getString(R.string.empty_opml_items_list))
        else
            showMessage("")

        val textSize = parent.getVisualPref().listTextSize

        val adapter = object : ArrayAdapter<Bookmark>(parent, android.R.layout.simple_list_item_1, android.R.id.text1, bookmarks){
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val text = bookmarks[position].toString()
                return currentTextViewItem(text, convertView, parent, textSize.toFloat(), false, this@OpmlListFragment.getLayoutInflater()!!)
            }
        }

        val list = getView().findView<ListView>(android.R.id.list)
        list.setAdapter(adapter)
        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>, p1: View, p2: Int, p3: Long) {
                val bookmark = bookmarks.get(p2)
                downloadOpmlAsync(this@OpmlListFragment.getActivity()!!, bookmark.url, bookmark.title)
            }
        })

        list.setOnItemLongClickListener(object : AdapterView.OnItemLongClickListener{
            public override fun onItemLongClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long): Boolean {
                val bookmark = bookmarks.get(p2)
                showOpmlQuickActionPopup(parent, bookmark, p1!!, list)
                return true
            }
        })
    }

    fun showOpmlQuickActionPopup(context: Activity, current: Bookmark, item: View, list: View) {
        //overlay popup at top of clicked overview position
        val popupWidth = item.getWidth()
        val popupHeight = item.getHeight()

        val x = context.getLayoutInflater().inflate(R.layout.opml_quick_actions, null, false)!!
        val pp = PopupWindow(x, popupWidth, popupHeight, true)

        x.setBackgroundColor(android.graphics.Color.LTGRAY)

        x.setOnClickListener {
            pp.dismiss()
        }

        val icon = x.findView<ImageView>(R.id.opml_quick_action_delete_icon)
        icon.setOnClickListener {
            val dlg = createConfirmationDialog(context = context, message = "Are you sure about deleting this OPML bookmark?", positive = {
                try{
                    val res = removeBookmark(current.url)
                    if (res.isFalse())
                        context.toastee("Error in removing this OPML bookmark ${res.exception?.getMessage()}")
                    else {
                        context.toastee("OPML bookmark removed")
                        displayOpmlList()
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

        val itemLocation = getLocationOnScreen(item)
        pp.showAtLocation(list, Gravity.TOP or Gravity.LEFT, itemLocation.x, itemLocation.y)
    }

    public fun removeBookmark (url: String): Result<None> {
        return DatabaseManager.cmd().bookmark().deleteByUrl(url)
    }

    private fun showMessage(msg: String) {
        val txt = getView().findView<TextView>(R.id.opml_list_fragment_message_tv)
        if (msg.isNullOrBlank()){
            txt.setVisibility(View.INVISIBLE)
            txt.setText("")
        }
        else {
            val textSize = parent.getVisualPref().listTextSize
            txt.setVisibility(View.VISIBLE)
            handleFontResize(txt, msg, textSize.toFloat())
        }
    }

    fun displayAddNewOpmlDialog() {
        val (hasClip, uri) = this.tryGetUriFromClipboard()

        if (hasClip)     {
            lastEnteredUrl = uri
        }

        Log.d(TAG, "Last entered value is $lastEnteredUrl")
        val dlg = createSingleInputDialog(parent, "Add new OPML", lastEnteredUrl, "Set url here", {
            dlg, url ->
            if (url.isNullOrBlank()){
                parent.toastee("Please enter url of the OPML", Duration.LONG)
            }
            else {
                var currentUrl = url!!
                if (!currentUrl.contains("http://"))
                    currentUrl = "http://" + currentUrl

                lastEnteredUrl = currentUrl

                val u = safeUrlConvert(currentUrl)
                if (u.isTrue()){
                    DownloadOpmlAsync(parent)
                            .executeOnRawCompletion({
                        res ->
                        if (res.isTrue()){
                            val opml = res.value

                            val title = opml?.head?.title ?: "No Title"
                            val language = "en"

                            val res2 = saveBookmarkToDb(title, currentUrl, BookmarkKind.OPML, language, null)

                            if (res2.isTrue()){
                                lastEnteredUrl = "" //reset value when the opml url is bookmarked
                                parent.toastee("$currentUrl is successfully bookmarked")
                                displayOpmlList()
                            }
                            else{
                                parent.toastee("Sorry, we cannot add this $currentUrl river", Duration.LONG)
                            }
                        }
                        else{
                            parent.toastee("Downloading url fails because of ${res.exception?.getMessage()}", Duration.LONG)
                        }
                    })
                            .execute(currentUrl)
                    dlg?.dismiss()
                }else{
                    Log.d(TAG, "RSS $currentUrl conversion generates ${u.exception?.getMessage()}")
                    parent.toastee("The url you entered is not valid. Please try again", Duration.LONG)
                }
            }
        })

        dlg.show()
    }
}
