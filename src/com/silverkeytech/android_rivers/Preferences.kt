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

import org.holoeverywhere.app.Activity
import android.content.Context
import org.holoeverywhere.preference.SharedPreferences
import org.holoeverywhere.preference.PreferenceManagerHelper

public class Preferences{
    class object{
        public val CONTENT: String = "CONTENT"
        public val CONTENT_RIVER_BOOKMARKS_SORTING: String = "CONTENT_RIVER_BOOKMARKS_SORTING"

        public val SETUP: String = "SETUP"
        public val SETUP_DOWNLOAD_DEFAULT_RIVERS: String = "SETUP_DOWNLOAD_DEFAULT_RIVERS"

        public val VISUAL: String = "PREFERENCE_VISUAL"
        public val VISUAL_LIST_TEXT_SIZE: String = "PREFERENCE_VISUAL_LIST_TEXT_SIZE"
        public val VISUAL_THEME: String = "PREFERENCE_VISUAL_THEME"
    }
}

public class PreferenceValue{
    class object {
        public val SORT_DESC: Int = 100
        public val SORT_NONE: Int = 101
        public val SORT_ASC: Int = 102
    }
}

public class PreferenceDefaults{
    class object {
        public val CONTENT_RIVER_BOOKMARKS_SORTING: Int = PreferenceValue.SORT_NONE
        public val CONTENT_BOOKMARK_COLLECTION_LATEST_DATE_FILTER_IN_DAYS: Int = 30
        public val CONTENT_BOOKMARK_COLLECTION_MAX_ITEMS_FILTER: Int = 12

        public val CONTENT_OUTLINE_HELP_SOURCE: String = "http://hobieu.apphb.com/opml/xml/3618737e-5339-40a1-9eb6-68d09a9a41d2"
        public val CONTENT_OUTLINE_MORE_NEWS_SOURCE: String = "http://hobieu.apphb.com/api/1/opml/root"

        public val CONTENT_BODY_MAX_LENGTH: Int = 280
        public val CONTENT_BODY_ARABIC_MAX_LENGTH: Int = 220

        public val RSS_LATEST_DATE_FILTER_IN_DAYS: Int = 30
        public val RSS_MAX_ITEMS_FILTER: Int = 20

        public val SETUP_DOWNLOAD_DEFAULT_RIVERS: Boolean = true

        public val VISUAL_LIST_TEXT_SIZE: Int = 20
        public val VISUAL_THEME: Int = R.style.Theme_Sherlock_Light_DarkActionBar

        public val LINK_SHARE_TITLE_MAX_LENGTH: Int = 80


        public val STANDARD_NEWS_COLOR: Int = android.graphics.Color.GRAY
        public val STANDARD_NEWS_IMAGE: Int = android.graphics.Color.CYAN
        public val STANDARD_NEWS_PODCAST: Int = android.graphics.Color.MAGENTA
        public val STANDARD_NEWS_SOURCE: Int = android.graphics.Color.BLUE
    }
}

fun Activity.getContentPref(): ContentPreference =
        ContentPreference(this.getSharedPreferences(Preferences.CONTENT, Context.MODE_PRIVATE)!!)

fun Activity.getSetupPref(): SetupPreference =
        SetupPreference(this.getSharedPreferences(Preferences.SETUP, Context.MODE_PRIVATE)!!)

fun Activity.getVisualPref(): VisualPreference =
        VisualPreference(this.getSharedPreferences(Preferences.VISUAL, Context.MODE_PRIVATE)!!)

public class ContentPreference(public val pref: SharedPreferences){
    public fun getRiverBookmarksSorting(): Int = pref.getInt(Preferences.CONTENT_RIVER_BOOKMARKS_SORTING, PreferenceDefaults.CONTENT_RIVER_BOOKMARKS_SORTING)
    public fun setRiverBookmarksSorting(sort: Int) {
        var edit = pref.edit()!!
        edit.putInt(Preferences.CONTENT_RIVER_BOOKMARKS_SORTING, sort)
        edit.commit()
    }
}

public class SetupPreference(public val pref: SharedPreferences){
    public fun getDownloadDefaultRiversIfNecessary(): Boolean =
            pref.getBoolean(Preferences.SETUP_DOWNLOAD_DEFAULT_RIVERS, PreferenceDefaults.SETUP_DOWNLOAD_DEFAULT_RIVERS)

    public fun setDownloadDefaultRivers(yes: Boolean) {
        val edit = pref.edit()!!
        edit.putBoolean(Preferences.SETUP_DOWNLOAD_DEFAULT_RIVERS, yes)
        edit.commit()
    }
}

public class VisualPreference (public val pref: SharedPreferences){
    public fun getListTextSize(): Int = pref.getInt(Preferences.VISUAL_LIST_TEXT_SIZE, PreferenceDefaults.VISUAL_LIST_TEXT_SIZE)
    public fun setListTextSize(size: Int) {
        if (size < 12 || size > 30) //http://developer.android.com/design/style/typography.html
            return

        var edit = pref.edit()!!
        edit.putInt(Preferences.VISUAL_LIST_TEXT_SIZE, size)
        edit.commit()
    }

    public fun getTheme(): Int = pref.getInt(Preferences.VISUAL_THEME, PreferenceDefaults.VISUAL_THEME)

    public fun switchTheme() {
        val currentTheme = getTheme()

        var newTheme = when(currentTheme){
            R.style.Theme_Sherlock -> R.style.Theme_Sherlock_Light_DarkActionBar
            R.style.Theme_Sherlock_Light_DarkActionBar -> R.style.Theme_Sherlock
            else -> R.style.Theme_Sherlock
        }

        var edit = pref.edit()!!
        edit.putInt(Preferences.VISUAL_THEME, newTheme)
        edit.commit()
    }
}