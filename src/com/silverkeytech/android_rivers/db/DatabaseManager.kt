package com.silverkeytech.android_rivers.db

import android.content.Context
import com.j256.ormlite.dao.Dao

private var db : Database? = null

public object DatabaseManager{
    fun init (context : Context){
        if (db == null)
            db = Database(context)
    }

    public fun getDb() : Database{
        return db!!
    }

    public var bookmark :  Dao<Bookmark, out Int?>? = null
        get() = getDb().getBookmarkDao() as Dao<Bookmark, out Int?>

}