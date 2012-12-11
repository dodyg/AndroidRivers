package com.silverkeytech.android_rivers.outliner

import java.util.HashMap
import java.io.Serializable

public enum class OutlineType{
    NONE
    INCLUDE
    LINK
    BLOGPOST
}

public data class OutlineContent (var level : Int, var text : String) : Serializable
{
    private var bag : HashMap<String, String> = HashMap<String, String>()

    public fun putAttribute(key : String, obj : String){
        bag.put(key, obj)
    }

    public fun getAttribute(key : String) : String? = bag.get(key)

    public fun containsKey(key : String ): Boolean{
        return bag.containsKey(key)
    }

    public fun copyAttributes(outline : OutlineContent){
        bag = outline.bag
    }

    public fun getType() : OutlineType{
        if (containsKey("type")){
            val tp = getAttribute("type")
            return when(tp){
                "include" -> OutlineType.INCLUDE
                "link" -> OutlineType.LINK
                "blogpost" -> OutlineType.BLOGPOST
                else -> OutlineType.NONE
            }
        }
        else
            return OutlineType.NONE
    }
}