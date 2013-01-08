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
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.support.ConnectionSource

public class Database (context: Context): OrmLiteSqliteOpenHelper(context, Database.DATABASE_NAME, null, Database.DATABASE_VERSION) {
    class object {
        val TAG: String = javaClass<Database>().getSimpleName()
        public val DATABASE_VERSION: Int = 3
        public val DATABASE_NAME: String = "AndroidRivers.sqlite"
    }

    public override fun onCreate(p0: SQLiteDatabase?, p1: ConnectionSource?) {
        SchemaCreation(p0!!).create(3)
    }

    public override fun onUpgrade(p0: SQLiteDatabase?, p1: ConnectionSource?, p2: Int, p3: Int) {
        val oldVersion = p2
        val newVersion = p3

        Log.d(TAG, "Old version $oldVersion and new version $newVersion")
        if (newVersion > oldVersion){
            var migrator = SchemaMigration(p0!!)
            for(val current in oldVersion..(newVersion - 1)){
                val res = migrator.migrate(current)
                if (res)
                    Log.d(TAG, "Performing upgrade for version $current successful")
                else
                    Log.d(TAG, "Performing upgrade for version $current fails")
            }
        }
    }

    var bookmarkDao: Dao<Bookmark, out Int?>? = null

    fun getBookmarkDao(): Any {
        if (bookmarkDao == null){
            bookmarkDao = super<OrmLiteSqliteOpenHelper>.getDao(javaClass<Bookmark>())
        }
        return bookmarkDao!!
    }

    var bookmarkCollectionDao: Dao<BookmarkCollection, out Int?>? = null

    fun getBookmarkCollectionDao(): Any {
        if (bookmarkCollectionDao == null){
            bookmarkCollectionDao = super<OrmLiteSqliteOpenHelper>.getDao(javaClass<BookmarkCollection>())
        }
        return bookmarkCollectionDao!!
    }

    var podcastDao: Dao<Podcast, out Int?>? = null
    public fun getPodcastDao(): Any{
        if (podcastDao == null){
            podcastDao = super<OrmLiteSqliteOpenHelper>.getDao(javaClass<Podcast>())
        }
        return podcastDao!!
    }
}