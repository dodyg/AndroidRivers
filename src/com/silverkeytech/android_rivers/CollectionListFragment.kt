package com.silverkeytech.android_rivers

import com.actionbarsherlock.app.SherlockListFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.util.Log

public class CollectionListFragment : SherlockListFragment() {
    class object {
        public val TAG: String = javaClass<CollectionListFragment>().getSimpleName()
    }

    public override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val vw = inflater!!.inflate(R.layout.collection_list_fragment, container, false)

        return vw
    }

    public override fun onHiddenChanged(hidden: Boolean) {
        Log.d(TAG, "OnHiddenChanged $hidden")
        super<SherlockListFragment>.onHiddenChanged(hidden)
    }

    public override fun onResume() {
        Log.d(TAG, "OnResume")
        super<SherlockListFragment>.onResume()
    }

    public override fun onPause() {
        Log.d(TAG, "OnPause")
        super<SherlockListFragment>.onPause()
    }
}