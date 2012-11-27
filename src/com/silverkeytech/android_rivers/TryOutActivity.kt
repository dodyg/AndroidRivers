package com.silverkeytech.android_rivers

import android.app.Activity
import android.os.Bundle

public class TryOutActivity() : Activity()
{
    class object {
        public val TAG: String = javaClass<TryOutActivity>().getSimpleName()!!
    }

    val DEFAULT_SUBSCRIPTION_LIST = "http://hobieu.apphb.com/api/1/default/riverssubscription"

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tryout)
    }

    public override fun onBackPressed() {
        super<Activity>.onBackPressed()
        finish()
    }
}