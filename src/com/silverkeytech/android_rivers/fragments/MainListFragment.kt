package com.silverkeytech.android_rivers.fragments


import org.holoeverywhere.app.Activity
import org.holoeverywhere.app.ListFragment
import com.silverkeytech.android_rivers.Bus
import android.os.Bundle

public abstract class MainListFragment : ListFragment () {
    protected var parent: Activity by kotlin.properties.Delegates.notNull()

    public override fun onAttach(activity: Activity?) {
        super<ListFragment>.onAttach(activity)
        parent = activity!!
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super<ListFragment>.onCreate(savedInstanceState)
        Bus.register(this)
    }

    public override fun onDestroy(){
        Bus.unregister(this)
        super<ListFragment>.onDestroy()
    }
}
