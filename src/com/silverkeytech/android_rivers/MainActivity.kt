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
        handleRiversListing()
    }

    fun handleRiversListing(){
        var list = this.findView<ListView>(R.id.main_rivers_lv)
        var values = array<String>("Android", "iPhone", "Window Phone", "BlackBerry", "WebOS")

        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values)
        list.setAdapter(adapter)

        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long) {
                var t = Toast.makeText(this@MainActivity, "hello World", 300)
                t!!.setGravity(Gravity.TOP or Gravity.CENTER, 0, 0 );
                t!!.show()
            }
        })
    }

    fun handleDownloadBtn(){
        var btn = this.findView<Button>(R.id.main_download_btn)
        btn.setOnClickListener({
            v ->
            var x = DownloadSubscription(this).execute("http://hobieu.apphb.com/api/1/default/riverssubscription")
        })
    }
}
