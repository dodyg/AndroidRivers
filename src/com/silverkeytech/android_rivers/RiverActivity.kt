package com.silverkeytech.android_rivers

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.os.Bundle
import android.app.ListActivity
import android.widget.TextView

//Responsible of downloading, caching and viewing a news river content
public class RiverActivity() : ListActivity()
{
    class object {
        public val TAG: String = javaClass<RiverActivity>().getSimpleName()!!
    }

    var riverUrl : String = ""
    var riverName : String = ""

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.river)

        var i = getIntent()!!
        riverUrl = i.getStringExtra(Params.RIVER_URL)!!
        riverName = i.getStringExtra(Params.RIVER_NAME)!!

        var msg = findView<TextView>(R.id.river_message_tv)

        msg.setText("River $riverName with url $riverUrl")
    }
}