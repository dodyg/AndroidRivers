package com.silverkeytech.android_rivers.activities

import org.holoeverywhere.app.Activity
import android.os.Bundle
import com.silverkeytech.android_rivers.R
import com.actionbarsherlock.app.ActionBar
import com.silverkeytech.android_rivers.getVisualPref

//This is the main interface for importing and exporting the content of the current database
public class ImportExportActivity : Activity(){
    class object {
        public val TAG: String = javaClass<ImportExportActivity>().getSimpleName()
    }

    var currentTheme: Int? = null

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        currentTheme = this.getVisualPref().theme
        setTheme(currentTheme!!)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_with_fragments)

        val actionBar: ActionBar = getSupportActionBar()!!
        actionBar.setDisplayShowHomeEnabled(false)
    }
}