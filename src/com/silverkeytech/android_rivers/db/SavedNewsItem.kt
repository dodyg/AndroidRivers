package com.silverkeytech.android_rivers.db

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.util.Calendar
import java.util.Date

public val SAVED_NEWS_ITEM_ID: String = "id"
public val SAVED_NEWS_ITEM_TITLE: String = "title"
public val SAVED_NEWS_ITEM_EXCERPT: String = "excerpt"
public val SAVED_NEWS_ITEM_CONTENT: String = "content"
public val SAVED_NEWS_ITEM_URL: String = "url"
public val SAVED_NEWS_ITEM_LANGUAGE: String = "language"
public val SAVED_NEWS_ITEM_KIND: String = "kind"
public val SAVED_NEWS_ITEM_DATE_CREATED: String = "date_created"


@DatabaseTable
public class SavedNewsItem(){

    @DatabaseField(generatedId = true, columnName = "id")
    public var id: Int = 0

    @DatabaseField(canBeNull = false, columnName = "title", width = 255, dataType = DataType.STRING)
    public var title: String = ""

    @DatabaseField(canBeNull = true, columnName = "excerpt", width = 550, dataType = DataType.LONG_STRING)
    public var excerpt: String = ""

    @DatabaseField(canBeNull = true, columnName = "content", width = 550, dataType = DataType.LONG_STRING)
    public var content: String = ""

    @DatabaseField(canBeNull = false, uniqueIndex = true, columnName = "url", width = 550, dataType = DataType.LONG_STRING)
    public var url: String = ""

    @DatabaseField(canBeNull = false, columnName = "language", width = 8, dataType = DataType.STRING)
    public var language: String = "en"

    @DatabaseField(canBeNull = false, columnName = "kind")
    public var kind: String = ""

    @DatabaseField(canBeNull = false, columnName = "date_created", dataType = DataType.DATE)
    public var dateCreated: Date = Calendar.getInstance().getTime()

    override fun toString(): String {
        return title
    }
}
