package com.silverkeytech.android_rivers

import com.actionbarsherlock.app.SherlockActivity
import android.os.Bundle

public class OutlinerActivity() : SherlockActivity()
{
    class object {
        public val TAG: String = javaClass<OutlinerActivity>().getSimpleName()
    }

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        setTheme(this.getVisualPref().getTheme())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.outliner)
    }

    public override fun onBackPressed() {
        super<SherlockActivity>.onBackPressed()
        finish()
    }
}