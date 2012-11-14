package com.silverkeytech.android_rivers

import android.app.Activity
import android.os.Bundle

public open class MainActivity(): Activity() {
    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
    }
}
