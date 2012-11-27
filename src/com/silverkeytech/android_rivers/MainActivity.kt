package com.silverkeytech.android_rivers

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.content.Intent

public open class MainActivity(): Activity() {
    class object {
        public val TAG: String = javaClass<MainActivity>().getSimpleName()!!
    }

    val DEFAULT_SUBSCRIPTION_LIST = "http://hobieu.apphb.com/api/1/default/riverssubscription"

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        DownloadSubscription(this).execute(DEFAULT_SUBSCRIPTION_LIST)
    }

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater = getMenuInflater()!!
        inflater.inflate(R.menu.subscription_menu, menu)
        return true
    }

    public override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.getItemId()){
            R.id.subscription_menu_refresh -> {
                DownloadSubscription(this).execute(DEFAULT_SUBSCRIPTION_LIST)
                return true
            }
            R.id.river_menu_tryout -> {
                var i = Intent(this, javaClass<TryOutActivity>())
                startActivity(i)
                return true
            }
            else ->
                return super.onOptionsItemSelected(item)
        }
    }
}
