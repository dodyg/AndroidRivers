package com.silverkeytech.android_rivers.db

import android.database.sqlite.SQLiteDatabase
import com.j256.ormlite.support.ConnectionSource
import android.util.Log
import android.database.SQLException

public class SchemaCreation(val db : SQLiteDatabase){
    class object {
        val TAG: String = javaClass<SchemaCreation>().getSimpleName()
    }

    public fun create(version : Int){
        when(version){
            1 -> create1()
            else -> {}
        }
    }

    private fun create1(){
        try{
            Log.d(TAG, "OnCreate: Create bookmark Table")

            db.execSQL("""
              CREATE TABLE `bookmark`
              (
                `url` TEXT NOT NULL ,
                `kind` VARCHAR NOT NULL ,
                `language` VARCHAR NOT NULL ,
                `title` VARCHAR NOT NULL ,
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