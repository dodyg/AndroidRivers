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
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils

public class Database (context: Context): OrmLiteSqliteOpenHelper(context, Database.DATABASE_NAME, null, Database.DATABASE_VERSION) {
    class object {
        val TAG: String = javaClass<Database>().getSimpleName()
        public val DATABASE_VERSION: Int = 1
        public val DATABASE_NAME: String = "AndroidRivers.sqlite"
    }

    public override fun onCreate(p0: SQLiteDatabase?, p1: ConnectionSource?) {
        try{
            TableUtils.createTable(p1, javaClass<Bookmark>())
        }
        catch (e: SQLException){
            Log.d(TAG, "Exception in creating database")
        }
    }

    public override fun onUpgrade(p0: SQLiteDatabase?, p1: ConnectionSource?, p2: Int, p3: Int) {
        when(p2){
            1 -> {
            }
            else -> {
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
}