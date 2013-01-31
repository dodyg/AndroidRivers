package com.silverkeytech.android_rivers

import com.actionbarsherlock.app.SherlockListFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.util.Log
import com.silverkeytech.android_rivers.db.SortingOrder
import com.silverkeytech.android_rivers.db.saveOpmlAsBookmarks
import android.app.Activity
import com.silverkeytech.android_rivers.db.getBookmarksFromDbAsOpml
import com.silverkeytech.android_rivers.db.BookmarkKind

public class RiverListFragment() : SherlockListFragment() {
    class object {
        public val TAG: String = javaClass<RiverListFragment>().getSimpleName()
    }

    public override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val vw = inflater!!.inflate(R.layout.river_list_fragment, container, false)

        Log.d(TAG, "We are being created")
        return vw
    }

    var parent : Activity? = null

    public override fun onAttach(activity: Activity?) {
        super<SherlockListFragment>.onAttach(activity)
        parent = activity
        displayRiverBookmarks(activity!!, true)
    }

    private fun displayRiverBookmarks(activity : Activity, retrieveDefaultFromInternet: Boolean) {
        Log.d(TAG, "We are attached")
    }
}