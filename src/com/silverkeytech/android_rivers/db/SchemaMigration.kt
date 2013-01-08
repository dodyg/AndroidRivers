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

public class SchemaMigration(val db: SQLiteDatabase){
    class object {
        val TAG: String = javaClass<SchemaMigration>().getSimpleName()
    }

    public fun migrate(version: Int): Boolean {
        return when(version){
            1 -> migrate1()
            2 -> {
                migrate1()
                migrate2()
            }
            else -> {
                false
            }
        }
    }

    private fun migrate2(): Boolean {
        try{
            Log.d(TAG, "OnUpgrade(2): Create podcast Table")
            db.execSQL("""
                      CREATE TABLE `podcast`
                      (
                        `title` VARCHAR NOT NULL,
                        `url` VARCHAR NOT NULL,
                        `source_title` VARCHAR NOT NULL,
                        `source_url` VARCHAR NOT NULL,
                        `mime_type` VARCHAR NOT NULL,
                        `length` INTEGER NOT NULL DEFAULT 0,
                        `description` VARCHAR NOT NULL,
                        `date_created` DATETIME NOT NULL DEFAULT current_timestamp,
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT
                      )
                        """)
            return true
        }catch (e: SQLException){
            Log.d(TAG, "Exception on Upgrade(2) ${e.getMessage()}")
            return false
        }
    }

    private fun migrate1(): Boolean {
        try{
            Log.d(TAG, "OnUpgrade(1): Create collection Table")
            db.execSQL("""
                          CREATE TABLE `bookmark_collection`
                          (
                            `title` VARCHAR NOT NULL ,
                            `kind` VARCHAR NOT NULL ,
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT
                          )
                        """)

            Log.d(TAG, "OnUpgrade(1): Add foreign key column bookmark_collection_id to bookmark Table")
            db.execSQL("""
                           ALTER TABLE `bookmark` ADD COLUMN
                            'bookmark_collection_id' INTEGER NULL
                        """)
            return true
        }catch (e: SQLException){
            Log.d(TAG, "Exception on Upgrade(1) ${e.getMessage()}")
            return false
        }
    }
}