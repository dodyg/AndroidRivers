package com.silverkeytech.android_rivers

import android.app.Activity
import android.view.View
import android.view.View.OnClickListener
import java.text.SimpleDateFormat
import android.view.Gravity
import android.widget.Toast


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

public fun parseRFC3339DateFormat(dt : String) : java.util.Date? {
    try{
        var formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        var dts = dt.replaceAll("([\\+\\-]\\d\\d):(\\d\\d)","$1$2")
        return formatter.parse(dts)
    }catch (e : Exception) {
        return null;
    }
}

public fun Activity.toastee(text : String, duration : Duration = Duration.QUICK, grav : Int = Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL){

    var t = Toast.makeText(this, text, duration.toInt())
    t!!.setGravity(grav, 0, 0 );
    t!!.show()
}

public enum class Duration {
    QUICK
    AVERAGE
    LONG
    public fun toInt(): Int{
        if (this == Duration.QUICK)
            return 3000
        else if (this == Duration.AVERAGE)
            return 10000
        else if (this == Duration.LONG)
            return 20000
        else
            return 5000
    }
}