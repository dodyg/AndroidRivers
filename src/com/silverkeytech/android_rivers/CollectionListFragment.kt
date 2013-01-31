package com.silverkeytech.android_rivers

import com.actionbarsherlock.app.SherlockListFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View

public class CollectionListFragment : SherlockListFragment() {
    public override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val vw = inflater!!.inflate(R.layout.collection_list_fragment, container, false)

        return vw
    }
}