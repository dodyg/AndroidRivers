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

import android.app.AlertDialog
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
import com.silverkeytech.android_rivers.db.SortingOrder
import com.silverkeytech.android_rivers.db.clearBookmarksFromCollection
import com.silverkeytech.android_rivers.db.getBookmarkCollectionFromDb
import com.silverkeytech.android_rivers.db.removeItemByUrlFromBookmarkDb
import com.silverkeytech.android_rivers.outlines.Outline

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
                    context.refreshBookmarkCollection()
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

    val removeIcon = x.findViewById(R.id.main_feed_quick_action_delete_icon) as ImageView
    removeIcon.setOnClickListener {
        val dlg = createConfirmationDialog(context = context, message = "Are you sure about removing this RSS bookmark?", positive = {
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

    val removeIcon = x.findViewById(R.id.main_river_quick_action_delete_icon) as ImageView
    removeIcon.setOnClickListener {
        val dlg = createConfirmationDialog(context = context, message = "Are you sure about removing this River bookmark?", positive = {
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
                        context.refreshRiverBookmarks(false)
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

data class Location(val x: Int, val y: Int)

fun getLocationOnScreen(item: View): Location {
    val loc = intArray(0, 1)
    item.getLocationOnScreen(loc)
    val popupX = loc[0]
    val popupY = loc[1]

    return Location(popupX, popupY)
}