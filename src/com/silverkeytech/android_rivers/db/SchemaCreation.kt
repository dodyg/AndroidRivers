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
    companion object {
        val TAG: String = javaClass<SchemaCreation>().getSimpleName()
    }

    public fun create(version: Int) {
        when(version){
            1 -> create1()
            2 -> create2()
            3 -> create3()
            else -> {
            }
        }
    }

    private fun create3(): Boolean {
        create2()
        try{
            Log.d(TAG, "OnCreate(3): Create podcast Table")
            db.execSQL("""
                          CREATE TABLE `podcast`
                          (
                            `title` VARCHAR NOT NULL,
                            `url` VARCHAR NOT NULL,
                            `source_title` VARCHAR NOT NULL,
                            `source_url` VARCHAR NOT NULL,
                            `local_path` VARCHAR NOT NULL,
                            `mime_type` VARCHAR NOT NULL,
                            `length` INTEGER NOT NULL DEFAULT 0,
                            `description` VARCHAR NOT NULL,
                            `date_created` DATETIME NOT NULL DEFAULT current_timestamp,
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT
                          )
                        """)
            return true
        }catch (e: SQLException){
            Log.d(TAG, "OnCreate(3) ${e.getMessage()}")
            return false
        }
    }

    private fun create2(): Boolean {
        create1()
        try{
            Log.d(TAG, "OnCreate(2): Create bookmark_collection Table")
            db.execSQL("""
                          CREATE TABLE `bookmark_collection`
                          (
                            `title` VARCHAR NOT NULL ,
                            `kind` VARCHAR NOT NULL ,
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT
                          )
                        """)
            return true
        }catch (e: SQLException){
            Log.d(TAG, "OnCreate(2) ${e.getMessage()}")
            return false
        }
    }

    private fun create1(): Boolean {
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

            return true
        }
        catch (e: SQLException){
            Log.d(TAG, "OnCreate(1) Exception in creating database")
            return false
        }
    }

}