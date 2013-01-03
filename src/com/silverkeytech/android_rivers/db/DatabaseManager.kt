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

import android.content.Context
import com.j256.ormlite.dao.Dao

private var db: Database? = null

public object DatabaseManager{
    fun init (context: Context) {
        if (db == null)
            db = Database(context)
    }

    public fun getDb(): Database {
        return db!!
    }

    public var bookmark: Dao<Bookmark, out Int?>? = null
        get() = getDb().getBookmarkDao() as Dao<Bookmark, out Int?>

    public var bookmarkCollection: Dao<BookmarkCollection, out Int?>? = null
        get() = getDb().getBookmarkCollectionDao() as Dao<BookmarkCollection, out Int?>

    public fun query(): Query = Query(bookmark, bookmarkCollection)
    public fun cmd(): Command = Command(bookmark, bookmarkCollection)
}

public class Query (private val bookmark: Dao<Bookmark, out Int?>?,
                    private val bookmarkCollection: Dao<BookmarkCollection, out Int?>?){
    public fun bookmark(): BookmarkQuery = BookmarkQuery(bookmark!!)
    public fun bookmarkCollection(): BookmarkCollectionQuery = BookmarkCollectionQuery(bookmarkCollection!!)
}

public class Command(private val bookmark: Dao<Bookmark, out Int?>?,
                     private val bookmarkCollection: Dao<BookmarkCollection, out Int?>?){
    public fun bookmark(): BookmarkCommand = BookmarkCommand(bookmark!!)
    public fun bookmarkCollection(): BookmarkCollectionCommand = BookmarkCollectionCommand(bookmarkCollection!!)
}