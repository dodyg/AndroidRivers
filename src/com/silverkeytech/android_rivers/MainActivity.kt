package com.silverkeytech.android_rivers

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import roboguice.inject.InjectView
import roboguice.activity.RoboActivity
import com.github.kevinsawicki.http
import com.github.kevinsawicki.http.HttpRequest
import android.util.Log
import android.widget.Button
import android.view.View
import android.view.View.OnClickListener
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import android.os.AsyncTask
import android.app.ProgressDialog
import android.content.Context
import android.widget.ListView
import android.widget.ArrayAdapter
import android.widget.AdapterView.OnItemClickListener
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.Toast
import android.view.Gravity


public open class MainActivity(): Activity() {
    class object {
        public val TAG: String = javaClass<MainActivity>().getSimpleName()!!
    }

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        handleDownloadBtn()
    }


    fun handleDownloadBtn(){
        var btn = this.findView<Button>(R.id.main_download_btn)
        btn.setOnClickListener({
            v ->
            var x = DownloadSubscription(this).execute("http://hobieu.apphb.com/api/1/default/riverssubscription")
        })
    }
}
