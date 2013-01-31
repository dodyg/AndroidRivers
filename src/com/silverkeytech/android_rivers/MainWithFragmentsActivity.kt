package com.silverkeytech.android_rivers

import com.actionbarsherlock.app.SherlockFragmentActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction

public open class MainWithFragmentsActivity(): SherlockFragmentActivity() {
    class object {
        public val TAG: String = javaClass<MainWithFragmentsActivity>().getSimpleName()
    }

    val DEFAULT_SUBSCRIPTION_LIST = "http://hobieu.apphb.com/api/1/default/riverssubscription"

    var mode: MainActivityMode = MainActivityMode.RIVER

    var currentTheme: Int? = null
    var isOnCreate: Boolean = true

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        currentTheme = this.getVisualPref().getTheme()
        isOnCreate = true
        setTheme(currentTheme!!)
        getMain().flags.reset()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_with_fragments)

        showAndHide(showRiver = true, showRss = false, showCollection = false)
    }

    fun showAndHide(showRiver : Boolean, showRss : Boolean, showCollection : Boolean){
        val riverFragment = this.findFragmentById(R.id.main_rivers_fragment)
        val rssFragment = this.findFragmentById(R.id.main_rss_fragment)
        val collectionFragment = this.findFragmentById(R.id.main_collection_fragment)

        val transaction = this.beginFragmentTransaction()

        if (showRiver) transaction.show(riverFragment) else transaction.hide(riverFragment)
        if (showRss) transaction.show(rssFragment) else transaction.hide(rssFragment)
        if (showCollection) transaction.show(collectionFragment) else transaction.hide(collectionFragment)

        transaction.commit()
    }
}

fun SherlockFragmentActivity.findFragmentById(id : Int) : Fragment{
    return this.getSupportFragmentManager()!!.findFragmentById(id)!!
}

fun SherlockFragmentActivity.beginFragmentTransaction() : FragmentTransaction {
    return this.getSupportFragmentManager()!!.beginTransaction()!!
}