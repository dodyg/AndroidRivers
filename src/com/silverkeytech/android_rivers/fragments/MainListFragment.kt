package com.silverkeytech.android_rivers.fragments

import org.holoeverywhere.app.Activity
import org.holoeverywhere.app.ListFragment
import com.silverkeytech.android_rivers.Bus
import android.os.Bundle
import com.silverkeytech.android_rivers.MessageEvent
import android.util.Log

public abstract class MainListFragment : ListFragment () {
    companion object {
        public open val TAG: String = javaClass<MainListFragment>().getSimpleName()
    }

    protected var parent: Activity by kotlin.properties.Delegates.notNull()

    public override fun onAttach(activity: Activity?) {
        super<ListFragment>.onAttach(activity)
        parent = activity!!
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super<ListFragment>.onCreate(savedInstanceState)
        Bus.register(this)
    }

    public open fun onEvent(msg : MessageEvent){
        Log.d(TAG, "Event Bus ${msg.message}")
    }

    public override fun onDestroy(){
        Bus.unregister(this)
        super<ListFragment>.onDestroy()
    }
}
