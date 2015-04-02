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
package com.silverkeytech.android_rivers

public class Params(){
    companion object {
        val RIVER_URL = "RIVER_URL"
        val RIVER_NAME = "RIVER_NAME"
        val RIVER_LANGUAGE = "RIVER_LANGUAGE"
        var MESSENGER = "MESSENGER"

        val FEED_URL = "FEED_URL"
        val FEED_NAME = "FEED_NAME"
        val FEED_LANGUAGE = "FEED_LANGUAGE"

        val OUTLINES_DATA: String = "OUTLINES_DATA"
        val OUTLINES_TITLE: String = "OUTLINES_TITLE"
        val OUTLINES_URL: String = "OUTLINES_URL"
        val OUTLINES_EXPAND_ALL: String = "OUTLINES_EXPAND_ALL"

        val DOWNLOAD_URL: String = "downloadUrl"
        val DOWNLOAD_TITLE: String = "downloadTitle"
        val DOWNLOAD_SOURCE_TITLE: String = "downloadSourceTitle"
        val DOWNLOAD_SOURCE_URL: String = "downloadSourceUrl"
        val DOWNLOAD_DESCRIPTION: String = "downloadDescription"
        val DOWNLOAD_LOCATION_PATH: String = "downloadLocationPath"

        val RIVERS_DOWNLOAD_PARCEL: String = "RIVERS_DOWNLOAD_PARCEL"
        val RIVERS_DOWNLOAD_TITLE: String = "RIVERS_DOWNLOAD_TITLE"
        val RIVERS_DOWNLOAD_URLS: String = "RIVERS_DOWNLOAD_URLS"

        val COLLECTION_TITLE = "COLLECTION_TITLE"
        val COLLECTION_ID = "COLLECTION_ID"

        val RIVER_SOURCES_RIVER_TITLE = "RIVER_SOURCES_RIVER_TITLE"
        val RIVER_SOURCES_RIVER_URI = "RIVER_SOURCES_RIVER_URI"
        val RIVER_SOURCES_TITLES = "RIVERS_SOURCES_TITLES"
        val RIVER_SOURCES_URIS = "RIVERS_SOURCES_URIS"

        val OPML_SUBSCRIPTION_LIST_URI = "OPML_SUBSCRIPTION_LIST_URI"

        val BUILT_IN_BROWSER_URI = "BUILT_IN_BROWSER_URI"

        val BLOG_CONFIGURATION = "BLOG_CONFIGURATION"
        val BLOG_PAYLOAD = "BLOG_POST_PAYLOAD"

        val BLOG_SERVER = "BLOG_SERVER"
        val BLOG_USERNAME = "BLOG_USERNAME"
        val BLOG_PASSWORD = "BLOG_PASSWORD"

        val POST_CONTENT = "POST_CONTENT"
        val POST_TITLE = "POST_TITLE"
        val POST_LINK = "POST_LINK"

        val PODCAST_TITLE = "PODCAST_TITLE"
        val PODCAST_PATH = "PODCAST_PATH"
    }
}