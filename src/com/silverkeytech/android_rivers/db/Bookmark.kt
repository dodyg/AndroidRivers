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

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

public val BOOKMARK_ID: String = "id"
public val BOOKMARK_TITLE: String = "title"
public val BOOKMARK_URL: String = "url"
public val BOOKMARK_LANGUAGE: String = "language"
public val BOOKMARK_KIND: String = "kind"
public val BOOKMARK_COLLECTION: String = "bookmark_collection_id"

@DatabaseTable
public class Bookmark(){

    @DatabaseField(generatedId = true, columnName = "id")
    public var id: Int = 0

    @DatabaseField(canBeNull = false, columnName = "title", width = 255, dataType = DataType.STRING)
    public var title: String = ""

    @DatabaseField(canBeNull = false, uniqueIndex = true, columnName = "url", width = 550, dataType = DataType.LONG_STRING)
    public var url: String = ""

    @DatabaseField(canBeNull = false, columnName = "language", width = 8, dataType = DataType.STRING)
    public var language: String = "en"

    @DatabaseField(canBeNull = false, columnName = "kind")
    public var kind: String = ""

    @DatabaseField(canBeNull = true, foreign = true, columnName = "bookmark_collection_id")
    public var collection: BookmarkCollection? = null

    override fun toString(): String {
        return title
    }
}
