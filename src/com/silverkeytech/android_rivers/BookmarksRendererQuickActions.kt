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

/*
  The functions here are specific to handle the long press in each of the main application mode (RIVER, RSS, COLLECTION)
*/

package com.silverkeytech.android_rivers

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.PopupWindow
import com.silverkeytech.android_rivers.db.Bookmark
import com.silverkeytech.android_rivers.db.BookmarkCollection
import com.silverkeytech.android_rivers.db.DatabaseManager
import com.silverkeytech.android_rivers.db.clearBookmarksFromCollection
import com.silverkeytech.android_rivers.outlines.Outline
import com.silverkeytech.android_rivers.db.removeItemByUrlFromBookmarkDb

fun inflater(context: Context): LayoutInflater {
    val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    return inflater
}

fun showCollectionQuickActionPopup(context: MainActivity, collection: BookmarkCollection, item: View, list: View) {
    //overlay popup at top of clicked overview position
    val popupWidth = item.getWidth()
    val popupHeight = item.getHeight()

    val x = context.getLayoutInflater()!!.inflate(R.layout.main_collection_quick_actions, null, false)!!
    val pp = PopupWindow(x, popupWidth, popupHeight, true)

    x.setBackgroundColor(android.graphics.Color.LTGRAY)

    x.setOnClickListener {
        pp.dismiss()
    }

    val delete = x.findViewById(R.id.main_collection_quick_action_delete_icon) as ImageView
    delete.setOnClickListener {
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
                context.getApplication().getMain().clearRiverBookmarksCache()

                context.toastee("Bookmark collection removed")
                context.refreshBookmarkCollection()
            }
        }
        catch(e: Exception){
            context.toastee("Error in trying to remove this bookmark ${e.getMessage()}")
        }
        pp.dismiss()
    }

    val edit = x.findViewById(R.id.main_collection_quick_action_edit_icon) as ImageView
    edit.setOnClickListener{
        pp.dismiss()
        startCollectionActivity(context, collection.id, collection.title)
    }

    val itemLocation = getLocationOnScreen(item)
    pp.showAtLocation(list, Gravity.TOP or Gravity.LEFT, itemLocation.x, itemLocation.y)
}

fun showRssBookmarkQuickActionPopup(context: MainActivity, currentBookmark: Bookmark, item: View, list: View) {
    //overlay popup at top of clicked overview position
    val popupWidth = item.getWidth()
    val popupHeight = item.getHeight()

    val x = context.getLayoutInflater()!!.inflate(R.layout.main_feed_quick_actions, null, false)!!
    val pp = PopupWindow(x, popupWidth, popupHeight, true)

    x.setBackgroundColor(android.graphics.Color.LTGRAY)

    x.setOnClickListener {
        pp.dismiss()
    }

    val icon = x.findViewById(R.id.main_feed_quick_action_delete_icon) as ImageView
    icon.setOnClickListener {
        try{
            val res = removeItemByUrlFromBookmarkDb(currentBookmark.url)
            if (res.isFalse())
                context.toastee("Error in removing this bookmark ${res.exception?.getMessage()}")
            else {
                context.toastee("Bookmark removed")
                context.refreshRssBookmarks()
            }
        }
        catch(e: Exception){
            context.toastee("Error in trying to remove this bookmark ${e.getMessage()}")
        }
        pp.dismiss()
    }

    val itemLocation = getLocationOnScreen(item)
    pp.showAtLocation(list, Gravity.TOP or Gravity.LEFT, itemLocation.x, itemLocation.y)
}

fun showRiverBookmarksQuickActionPopup(context: MainActivity, currentOutline: Outline, item: View, list: View) {
    //overlay popup at top of clicked overview position
    val popupWidth = item.getWidth()
    val popupHeight = item.getHeight()

    val x = inflater(context).inflate(R.layout.main_river_quick_actions, null, false)!!
    val pp = PopupWindow(x, popupWidth, popupHeight, true)

    x.setBackgroundColor(android.graphics.Color.LTGRAY)

    x.setOnClickListener {
        pp.dismiss()
    }

    val icon = x.findViewById(R.id.main_river_quick_action_delete_icon) as ImageView
    icon.setOnClickListener {
        try{
            val res = DatabaseManager.cmd().bookmark().deleteByUrl(currentOutline.url!!)
            if (res.isFalse())
                context.toastee("Error in removing this bookmark ${res.exception?.getMessage()}")
            else {
                context.toastee("Bookmark removed")
                context.refreshRiverBookmarks(false)
            }
        }
        catch(e: Exception){
            context.toastee("Error in trying to remove this bookmark ${e.getMessage()}")
        }
        pp.dismiss()
    }

    val itemLocation = getLocationOnScreen(item)
    pp.showAtLocation(list, Gravity.TOP or Gravity.LEFT, itemLocation.x, itemLocation.y)
}

data class Location(val x: Int, val y: Int)

fun getLocationOnScreen(item: View): Location {
    val loc = intArray(0, 1)
    item.getLocationOnScreen(loc)
    val popupX = loc[0]
    val popupY = loc[1]

    return Location(popupX, popupY)
}