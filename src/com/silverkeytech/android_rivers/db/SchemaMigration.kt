package com.silverkeytech.android_rivers.db

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.database.SQLException


public class SchemaMigration(val db : SQLiteDatabase){
    class object {
        val TAG: String = javaClass<SchemaMigration>().getSimpleName()
    }

    public fun migrate(version : Int) : Boolean{
        return when(version){
            1 -> migrate1()
            else -> { false }
        }
    }

    private fun migrate1() : Boolean{
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
        }catch (e : SQLException){
            Log.d(TAG, "Exception on Upgrade(1) ${e.getMessage()}")
            return false
        }

    }
}