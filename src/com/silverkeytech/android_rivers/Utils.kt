package com.silverkeytech.android_rivers

import android.app.Activity
import android.view.View
import android.view.View.OnClickListener
import java.text.SimpleDateFormat


public fun Activity.findView<T : View>(id : Int) : T{
    return (this.findViewById(id) as T)
}

public fun OnClickListener(action: (View?) -> Unit): OnClickListener {
    return object : OnClickListener {
        public override fun onClick(p0: View?) {
            action(p0)
        }
    }
}

public fun View.setOnClickListener(action: (View?) -> Unit): Unit {
    setOnClickListener(OnClickListener(action))
}

public fun ParseRFC3339DateFormat(dt : String) : java.util.Date? {
    try{
        var formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        var dts = dt.replaceAll("([\\+\\-]\\d\\d):(\\d\\d)","$1$2")
        return formatter.parse(dts)
    }catch (e : Exception) {
        return null;
    }
}

