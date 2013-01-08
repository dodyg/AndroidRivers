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
package com.silverkeytech.android_rivers.db

import com.silverkeytech.android_rivers.None
import com.silverkeytech.android_rivers.Result
import com.silverkeytech.android_rivers.isNullOrEmpty
import com.silverkeytech.android_rivers.outlines.Body
import com.silverkeytech.android_rivers.outlines.Opml
import com.silverkeytech.android_rivers.outlines.Outline

public fun removePodcast (id : Int): Result<None>{
    return DatabaseManager.cmd().podcast().deleteById(id)
}

public fun getPodcastsFromDb(sortByTitle : SortingOrder) : List<Podcast>{
    val podcasts = DatabaseManager.query().podcast().all(sortByTitle)

    if (podcasts.exist)
        return podcasts.values!!
    else
        return arrayListOf<Podcast>()
}

public fun savePodcastToDb(
        title : String, url : String, sourceTitle : String, sourceUrl : String, localPath : String,
        description : String, mimeType : String, length : Int) : Result<Podcast>{
    try{
        val p = Podcast()
        p.title = title
        p.url = url
        p.sourceTitle = sourceTitle
        p.sourceUrl = sourceUrl
        p.localPath = localPath
        p.description = description
        p.mimeType = mimeType
        p.length = length

        DatabaseManager.podcast!!.create(p)
        return Result.right(p)
    }
    catch(e : Exception){
        return Result.wrong<Podcast>(null)
    }
}

public fun addNewCollection(title: String, kind: BookmarkCollectionKind): Result<BookmarkCollection> {
    try{
        val coll = BookmarkCollection()
        coll.title = title
        coll.kind = kind.toString()

        DatabaseManager.bookmarkCollection!!.create(coll)
        return Result.right(coll)
    }
    catch(e: Exception){
        return Result.wrong<BookmarkCollection>(null)
    }
}


public fun clearBookmarksFromCollection(collectionId: Int): Result<None> {
    try{
        val bookmarks = getBookmarksFromDbByCollection(collectionId)
        if (bookmarks.count() > 0){
            for(val bk in bookmarks){
                val b = DatabaseManager.query().bookmark().byUrl(bk.url)
                //double check to make sure that the bookmark really exists
                if (b.exists){
                    b.value!!.collection = null
                    DatabaseManager.bookmark!!.update(b.value!!) //now it is removed
                }
            }
        }
        return Result.right(None())
    }
    catch(e: Exception){
        return Result.wrong<None>(e)
    }
}

public fun removeBookmarkFromCollection(collectionId: Int, bookmarkId: Int): Result<None> {
    try{
        val bookmarks = getBookmarksFromDbByCollection(collectionId)
        if (bookmarks.count() > 0){
            var bookmark = bookmarks.filter { it.id == bookmarkId }

            if (bookmark.count() == 1){
                val bk = bookmark.get(0)
                val b = DatabaseManager.query().bookmark().byUrl(bk.url)
                //double check to make sure that the bookmark really exists
                if (b.exists){
                    b.value!!.collection = null
                    DatabaseManager.bookmark!!.update(b.value!!) //now it is removed
                }
            }
        }
        return Result.right(None())
    }
    catch(e: Exception){
        return Result.wrong<None>(e)
    }
}


public fun getBookmarkCollectionFromDb(sortByTitleOrder : SortingOrder): List<BookmarkCollection> {
    val coll = DatabaseManager.query().bookmarkCollection().all(sortByTitleOrder)

    if (coll.exist)
        return coll.values!!
    else
        return arrayListOf<BookmarkCollection>()
}

public fun getBookmarksFromDbByCollection(collectionId: Int): List<Bookmark> {
    val bookmarks = DatabaseManager.query().bookmark().byCollectionId(collectionId)

    if (bookmarks.exist)
        return bookmarks.values!!
    else
        return arrayListOf<Bookmark>()
}

public fun getBookmarksUrlsFromDbByCollection(collectionId : Int): Array<String?>{
    val bookmarks = getBookmarksFromDbByCollection(collectionId)
    val urls = bookmarks.map { it.url }.toArray(array<String?>())

    return urls
}

public fun checkIfUrlAlreadyBookmarked(url: String): Boolean {
    return DatabaseManager.query().bookmark().byUrl(url).exists
}

public fun saveBookmarkToDb(title: String,
                            url: String,
                            kind: BookmarkKind,
                            lang: String,
                            collection: BookmarkCollection?): Result<None> {
    try{
        val bk = Bookmark()
        bk.title = title
        bk.url = url
        bk.kind = kind.toString()
        bk.language = lang
        bk.collection = collection

        DatabaseManager.bookmark!!.create(bk)

        return Result.right(None())
    }
    catch(e: Exception){
        return Result.wrong<None>(e)
    }
}

public fun getBookmarksFromDb(kind: BookmarkKind, sortByTitleOrder: SortingOrder): List<Bookmark> {
    val bookmarks = DatabaseManager.query().bookmark().byKind(kind, sortByTitleOrder)

    if (bookmarks.exist)
        return bookmarks.values!!
    else
        return arrayListOf<Bookmark>()
}

//get bookmarks from db and return the data in opml format
public fun getBookmarksFromDbAsOpml(kind: BookmarkKind, sortByTitleOrder : SortingOrder): Opml {
    val opml = Opml()
    opml.body = Body()

    var bookmarks = DatabaseManager.query().bookmark().byKind(kind, sortByTitleOrder)

    if (bookmarks.exist){
        for(val b in bookmarks.values?.iterator()){
            var o = Outline()
            o.text = b.title
            o.url = b.url
            o.language = b.language
            opml.body!!.outline!!.add(o)
        }
    }
    return opml
}

public fun saveOpmlAsBookmarks(opml: Opml): Result<Opml> {
    try{
        val bkDao = DatabaseManager.bookmark!!

        for(val o in opml.body!!.outline!!.iterator()){
            val b = Bookmark()
            b.title = o.text!!
            b.url = o.url!!
            if (!o.language!!.isNullOrEmpty())
                b.language = o.language!!

            b.kind = BookmarkKind.RIVER.toString()

            bkDao.create(b)
        }

        return Result.right(opml)
    }
    catch (e: Exception)
    {
        return Result.wrong(e)
    }
}

public fun removeItemByUrlFromBookmarkDb(url : String) : Result<None>{
    return  DatabaseManager.cmd().bookmark().deleteByUrl(url)
}