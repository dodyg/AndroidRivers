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
import android.util.Log
import com.silverkeytech.android_rivers.db.BookmarkCollection
import com.silverkeytech.android_rivers.db.BookmarkKind
import com.silverkeytech.android_rivers.db.SortingOrder
import com.silverkeytech.android_rivers.db.getBookmarkCollectionFromDb
import com.silverkeytech.android_rivers.db.saveBookmarkToDb
import org.holoeverywhere.app.Activity
import com.silverkeytech.android_rivers.activities.toastee
import com.silverkeytech.android_rivers.activities.Duration

//Note: This is a duplication with FeedActivity
fun saveBookmark(context: Activity, feedName: String, feedUrl: String, feedLanguage: String, collection: BookmarkCollection?) {
    Log.d("saveBookmark", "Name = '$feedName' - Url = '$feedUrl' - Language = '$feedLanguage'")

    val res = saveBookmarkToDb(feedName, feedUrl, BookmarkKind.RSS, feedLanguage, collection)

    if (res.isTrue()){
        if (collection == null)
            context.toastee("$feedName is bookmarked.")
        else
            context.toastee("$feedName is bookmarked to your ${collection.title} collection.")
    }
    else {
        Log.d("saveBookmark", "${res.exception?.getMessage()} in saving bookmark")
        context.toastee("Sorry, I cannot bookmark $feedUrl", Duration.LONG)
    }
}


//Note: This is a duplication with FeedActivity
fun addBookmarkOption(context: Activity, feedDateIsParseable: Boolean, bookmark: (BookmarkCollection?) -> Unit) {
    var coll = getBookmarkCollectionFromDb(sortByTitleOrder = SortingOrder.ASC)

    if (!coll.isEmpty()){
        if (feedDateIsParseable){
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("Bookmark to a collection")

            val collectionTitles = coll.map { it.title }.toTypedArray<String>()
            val withInstruction = arrayListOf(context.getString(R.string.skip_collection), *collectionTitles).toTypedArray()

            dialog.setItems(withInstruction, object : DialogInterface.OnClickListener{
                public override fun onClick(p0: DialogInterface, p1: Int) {
                    if (p1 == 0) {
                        //skip adding bookmark to collection. Just add to RSS bookmark
                        bookmark(null)
                    } else {
                        val currentCollection = coll[p1 - 1] //we added one extra item that was "no instruction" so the index must be deducted by 1
                        bookmark(currentCollection)
                    }
                }
            })

            var createdDialog = dialog.create()
            createdDialog.setCanceledOnTouchOutside(true)
            createdDialog.setCancelable(true)
            createdDialog.show()
        }
        else{
            context.toastee("This feed's date cannot be determined so it is bookmarked without collection immediately", Duration.LONG)
            bookmark(null)
        }

    } else {
        //no collection is available so save it straight to bookmark
        bookmark(null)
    }
}