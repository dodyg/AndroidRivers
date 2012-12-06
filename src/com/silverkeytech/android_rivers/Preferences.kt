package com.silverkeytech.android_rivers

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

public class Preferences{
    class object{
        public val VISUAL: String = "PREFERENCE_VISUAL"
        public val VISUAL_LIST_TEXT_SIZE: String = "PREFERENCE_VISUAL_LIST_TEXT_SIZE"
        public val VISUAL_THEME: String = "PREFERENCE_VISUAL_THEME"
    }
}

public class PreferenceDefaults{
    class object {
        public val VISUAL_LIST_TEXT_SIZE: Int = 24
        public val VISUAL_THEME: Int = R.style.Theme_Sherlock_Light_DarkActionBar
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
    public fun switchTheme() {
        var currentTheme = getTheme()

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