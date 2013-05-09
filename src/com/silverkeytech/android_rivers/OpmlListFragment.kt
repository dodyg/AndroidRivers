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

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuInflater
import com.silverkeytech.android_rivers.db.getBookmarksFromDb
import org.holoeverywhere.LayoutInflater
import org.holoeverywhere.app.Activity
import org.holoeverywhere.app.ListFragment
import com.silverkeytech.android_rivers.db.BookmarkKind
import android.widget.TextView
import com.silverkeytech.android_rivers.db.SortingOrder
import com.silverkeytech.android_rivers.db.Bookmark
import com.silverkeytech.android_rivers.db.BookmarkCollection
import com.silverkeytech.android_rivers.db.BookmarkKind
import com.silverkeytech.android_rivers.db.DatabaseManager
import com.silverkeytech.android_rivers.db.SortingOrder
import com.silverkeytech.android_rivers.db.removeItemByUrlFromBookmarkDb
import com.silverkeytech.android_rivers.db.saveBookmarkToDb


public class OpmlListFragment(): ListFragment() {
    class object {
        public val TAG: String = javaClass<OpmlListFragment>().getSimpleName()
    }

    var parent: Activity? = null
    var lastEnteredUrl: String? = ""

    var isFirstLoad: Boolean = true
    public override fun onAttach(activity: Activity?) {
        super<ListFragment>.onAttach(activity)
        parent = activity
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super<ListFragment>.onCreate(savedInstanceState)
    }

    public override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val vw = inflater!!.inflate(R.layout.opml_list_fragment, container, false)

        Log.d(TAG, "We are being created")

        return vw
    }

    public override fun onStart() {
        super<ListFragment>.onStart()

    }

    public override fun onResume() {
        Log.d(TAG, "OnResume")

        if (getUserVisibleHint()){
            Log.d(TAG, "OnResume - OpmlListFragment visible")
            displayOpmlList()
        }

        super<ListFragment>.onResume()
    }


    public override fun onHiddenChanged(hidden: Boolean) {
        Log.d(TAG, "OnHiddenChanged $hidden")

        if (!hidden && !isFirstLoad){
            displayOpmlList()
        }

        isFirstLoad = false
        super<ListFragment>.onHiddenChanged(hidden)
    }

    public override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.opml_list_fragment_menu, menu)
        super<ListFragment>.onCreateOptionsMenu(menu, inflater)
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

    public override fun onPause() {
        Log.d(TAG, "OnPause")
        super<ListFragment>.onPause()
    }

    private fun displayOpmlList(){
        val opmls = getBookmarksFromDb(BookmarkKind.OPML, SortingOrder.ASC)

        if (opmls.size == 0)
            showMessage(parent!!.getString(R.string.empty_opml_items_list)!!)
        else
            showMessage("")
    }

    private fun showMessage(msg: String) {
        val txt = getView()!!.findViewById(R.id.opml_list_fragment_message_tv) as TextView
        if (msg.isNullOrEmpty()){
            txt.setVisibility(View.INVISIBLE)
            txt.setText("")
        }
        else {
            val textSize = parent!!.getVisualPref().getListTextSize()
            txt.setVisibility(View.VISIBLE)
            handleFontResize(txt, msg, textSize.toFloat())
        }
    }

    public data class ViewHolder (var podcastName: TextView)

    fun displayAddNewOpmlDialog() {
        val dlg = createSingleInputDialog(parent!!, "Add new OPML", lastEnteredUrl , "Set url here", {
            dlg, url ->
            lastEnteredUrl = url
            Log.d(TAG, "Entered $url")
            if (url.isNullOrEmpty()){
                parent!!.toastee("Please enter url of the OPML", Duration.LONG)
            }
            else {
                var currentUrl = url!!
                if (!currentUrl.contains("http://"))
                    currentUrl = "http://" + currentUrl

                val u = safeUrlConvert(currentUrl)
                if (u.isTrue()){
                    DownloadOpml(parent)
                            .executeOnRawCompletion({
                        res ->
                        if (res.isTrue()){
                            val opml = res.value

                            val title = opml?.head?.title ?: "No Title"
                            val language = "en"

                            val res2 = saveBookmarkToDb(title, currentUrl, BookmarkKind.OPML, language, null)

                            if (res2.isTrue()){
                                parent!!.toastee("$currentUrl is successfully bookmarked")
                                displayOpmlList()
                            }
                            else{
                                parent!!.toastee("Sorry, we cannot add this $currentUrl river", Duration.LONG)
                            }
                        }
                        else{
                            parent!!.toastee("Downloading url fails because of ${res.exception?.getMessage()}", Duration.LONG)
                        }
                    })
                            .execute(currentUrl)

//                    DownloadFeed(parent!!, true)
//                            .executeOnComplete {
//                        res ->
//                        if (res.isTrue()){
//                            var feed = res.value!!
//
//                            val res2 = saveBookmarkToDb(feed.title, currentUrl, BookmarkKind.RSS, feed.language, null)
//
//                            if (res2.isTrue()){
//                                parent!!.toastee("$currentUrl is successfully bookmarked")
//                                displayRssBookmarks()
//                            }
//                            else{
//                                parent!!.toastee("Sorry, we cannot add this $currentUrl river", Duration.LONG)
//                            }
//                        }else{
//                            parent!!.toastee("Error ${res.exception?.getMessage()}", Duration.LONG)
//                        }
//                    }
//                            .execute(currentUrl)
                    dlg?.dismiss()
                }else{
                    Log.d(TAG, "RSS $currentUrl conversion generates ${u.exception?.getMessage()}")
                    parent!!.toastee("The url you entered is not valid. Please try again", Duration.LONG)
                }
            }
        })

        dlg.show()
    }
}
