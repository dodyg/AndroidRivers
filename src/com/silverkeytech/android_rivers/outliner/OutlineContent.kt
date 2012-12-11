package com.silverkeytech.android_rivers.outliner

import java.util.HashMap
import java.io.Serializable

public data class OutlineContent (var level : Int, var text : String) : Serializable
{
    private var bag : HashMap<String, Any> = HashMap<String, Any>()

    public fun putAttribute(key : String, obj : Any){
        bag.put(key, obj)
    }

    public fun getAttribute(key : String) : Any? = bag.get(key)

    public fun containsKey(key : String ): Boolean{
        return bag.containsKey(key)
    }
}