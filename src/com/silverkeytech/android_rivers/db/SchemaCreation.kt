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

import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log

public class SchemaCreation(val db: SQLiteDatabase){
    class object {
        val TAG: String = javaClass<SchemaCreation>().getSimpleName()
    }

    public fun create(version: Int) {
        when(version){
            1 -> create1()
            else -> {
            }
        }
    }

    private fun create1() {
        try{
            Log.d(TAG, "OnCreate(1): Create bookmark Table")

            db.execSQL("""
              CREATE TABLE `bookmark`
              (
                `url` TEXT NOT NULL ,
                `kind` VARCHAR NOT NULL ,
                `language` VARCHAR NOT NULL ,
                `title` VARCHAR NOT NULL ,
                'bookmark_collection_id' INTEGER NULL,
                `id` INTEGER PRIMARY KEY AUTOINCREMENT
              )
            """)

            db.execSQL("""
              CREATE UNIQUE INDEX `bookmark_url_idx` ON `bookmark` ( `url` )
            """)
        }
        catch (e: SQLException){
            Log.d(TAG, "Exception in creating database")
        }
    }
}