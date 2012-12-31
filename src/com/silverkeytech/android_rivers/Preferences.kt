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

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

public class Preferences{
    class object{
        public val VISUAL: String = "PREFERENCE_VISUAL"
        public val VISUAL_LIST_TEXT_SIZE: String = "PREFERENCE_VISUAL_LIST_TEXT_SIZE"
        public val VISUAL_THEME: String = "PREFERENCE_VISUAL_THEME"

        public val BOOKMARK_SORTING: String = "PREFERENCE_VISUAL_BOOKMARK_SORTING"
    }
}


public val SORT_DESC : Int = 100
public val SORT_NONE : Int = 101
public val SORT_ASC : Int = 102


public class PreferenceDefaults{
    class object {
        public val VISUAL_LIST_TEXT_SIZE: Int = 24
        public val VISUAL_THEME: Int = R.style.Theme_Sherlock_Light_DarkActionBar
        public val BOOKMARK_SORTING: Int = SORT_NONE
    }
}

fun Activity.getVisualPref(): VisualPreference {
    return VisualPreference(this.getSharedPreferences(Preferences.VISUAL_LIST_TEXT_SIZE, Context.MODE_PRIVATE)!!)
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

    public fun getBookmarkSorting(): Int = pref.getInt(Preferences.BOOKMARK_SORTING, PreferenceDefaults.BOOKMARK_SORTING)
    public fun setBookmarkSorting(sort : Int){
        var edit = pref.edit()!!
        edit.putInt(Preferences.BOOKMARK_SORTING, sort)
        edit.commit()
    }

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