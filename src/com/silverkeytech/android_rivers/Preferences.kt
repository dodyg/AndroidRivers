package com.silverkeytech.android_rivers


import android.app.Activity
import android.content.SharedPreferences
import android.content.Context


public class Preferences{
    class object{
        public val VISUAL: String = "PREFERENCE_VISUAL"
        public val VISUAL_LIST_TEXT_SIZE:  String = "PREFERENCE_VISUAL_LIST_TEXT_SIZE"
        public val VISUAL_THEME : String = "PREFERENCE_VISUAL_THEME"
    }
}

public class PreferenceDefaults{
    class object {
        public val VISUAL_LIST_TEXT_SIZE: Int = 24
        public val VISUAL_THEME : String = "LIGHT"
    }
}

fun Activity.getVisualPref() : VisualPreference {
    return VisualPreference(this.getSharedPreferences(Preferences.VISUAL_LIST_TEXT_SIZE, Context.MODE_PRIVATE)!!)
}

public class VisualPreference (public val pref : SharedPreferences){
    public fun getListTextSize() : Int = pref.getInt(Preferences.VISUAL_LIST_TEXT_SIZE, PreferenceDefaults.VISUAL_LIST_TEXT_SIZE)
    public fun setListTextSize(size : Int){
        if (size < 8 && size > 40) //you can see less or more than this size
            return

        var edit = pref.edit()!!
        edit.putInt(Preferences.VISUAL_LIST_TEXT_SIZE, size)
        edit.commit()
    }
}