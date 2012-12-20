package com.silverkeytech.android_rivers.syndication

import com.silverkeytech.android_rivers.isNullOrEmpty
import java.util.Date


public data class FeedItem(){
    public var title : String? = null
    public var description : String? = null
    public var link : String? = null
    public var pubDate : Date? = null

    fun hasTitle() : Boolean{
        return title.isNullOrEmpty()
    }

    fun hasDescription() : Boolean{
        return description.isNullOrEmpty()
    }
}