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
import java.util.Calendar
import java.util.Date

public val PODCAST_ID: String = "id"
public val PODCAST_TITLE: String = "title"
public val PODCAST_URL: String = "url"
public val PODCAST_SOURCE_TITLE: String = "source_title"
public val PODCAST_SOURCE_URL: String = "source_url"
public val PODCAST_LOCAL_PATH: String = "local_path"
public val PODCAST_MIME_TYPE: String = "mime_type"
public val PODCAST_LENGTH: String = "length"
public val PODCAST_DESCRIPTION: String = "description"
public val PODCAST_DATE_CREATED: String = "date_created"

@DatabaseTable
public class Podcast(){
    @DatabaseField(generatedId = true, columnName = "id")
    public var id: Int = 0

    @DatabaseField(canBeNull = false, columnName = "title", width = 255, dataType = DataType.STRING)
    public var title: String = ""

    @DatabaseField(canBeNull = false, columnName = "url", width = 550, dataType = DataType.LONG_STRING)
    public var url: String = ""

    @DatabaseField(canBeNull = false, columnName = "source_title", width = 255, dataType = DataType.STRING)
    public var sourceTitle: String = ""

    @DatabaseField(canBeNull = false, columnName = "source_url", width = 255, dataType = DataType.STRING)
    public var sourceUrl: String = ""

    @DatabaseField(canBeNull = false, columnName = "local_path", width = 550, dataType = DataType.LONG_STRING)
    public var localPath: String = ""

    @DatabaseField(canBeNull = false, columnName = "mime_type", width = 20, dataType = DataType.STRING)
    public var mimeType: String = ""

    @DatabaseField(canBeNull = false, columnName = "length", dataType = DataType.INTEGER)
    public var length: Int = 0

    @DatabaseField(canBeNull = false, columnName = "description", width = 550, dataType = DataType.LONG_STRING)
    public var description: String = ""

    @DatabaseField(canBeNull = false, columnName = "date_created", dataType = DataType.DATE)
    public var dateCreated: Date = Calendar.getInstance().getTime()

    override fun toString(): String {
        return title
    }
}
