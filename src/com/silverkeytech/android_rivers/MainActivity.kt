package com.silverkeytech.android_rivers

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import roboguice.inject.InjectView
import roboguice.activity.RoboActivity
import com.github.kevinsawicki.http
import com.github.kevinsawicki.http.HttpRequest
import android.util.Log

public open class MainActivity(): Activity() {
    class object {
        public val TAG: String = javaClass<MainActivity>().getSimpleName()!!
    }

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main)
        load()
    }

    fun load(){
        Log.d(TAG, "Starting to load")
        var req = HttpRequest.get("http://hobieu.apphb.com/api/1/default/riverssubscription")?.body()
        Log.d(TAG, "Loading content ${req?.length()}")
    }
}
