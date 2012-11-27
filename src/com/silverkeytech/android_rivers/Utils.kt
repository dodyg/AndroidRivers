package com.silverkeytech.android_rivers

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import java.text.SimpleDateFormat
import org.apache.http.conn.ConnectTimeoutException
import java.net.UnknownHostException
import java.net.SocketException

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

public data class ConnectivityErrorMessage(val timeoutException :String, val socketException : String, val otherException : String)

public fun Activity.handleConnectivityError(e: Exception?, message : ConnectivityErrorMessage){
    if (e is ConnectTimeoutException)
        this.toastee(message.timeoutException, Duration.AVERAGE)
    else if (e is UnknownHostException || e is SocketException)
        this.toastee(message.socketException, Duration.AVERAGE)
    else
        this.toastee(message.otherException, Duration.AVERAGE)

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


fun imageMimeTypeToFileExtension (mimeType : String) : String{
    return when(mimeType){
        "image/gif" -> ".gif"
        "image/jpeg" -> ".jpg"
        "image/pjpeg" -> ".jpg"
        "image/png" -> ".png"
        "image/tiff" -> ".tiff"
        else -> ""
    }
}

fun isSupportedImageMime(val mimeType : String) : Boolean{
    return when (mimeType){
        "image/gif" -> true
        "image/jpeg" -> true
        "image/pjpeg" -> true
        "image/png" -> true
        else -> false
    }
}
