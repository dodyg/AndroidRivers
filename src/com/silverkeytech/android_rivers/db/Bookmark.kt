package com.silverkeytech.android_rivers.db

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

[DatabaseTable(tableName = "bookmarks")]
public class Bookmark(){

    [DatabaseField(generatedId = true)]
    public var id: Int = 0

    [DatabaseField(canBeNull = false)]
    public var title: String = ""

    [DatabaseField(canBeNull = false)]
    public var url: String = ""
}