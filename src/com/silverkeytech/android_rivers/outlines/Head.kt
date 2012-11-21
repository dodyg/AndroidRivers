package com.silverkeytech.android_rivers.outlines

import org.simpleframework.xml.Element
import hirondelle.date4j.DateTime
import com.silverkeytech.android_rivers.DateHelper

public class Head() {
    [Element] public var title: String? = null
    [Element] public var dateCreated: String? = null       //RFC822 time
    [Element] public var dateModified: String? = null      //RFC822 time
    [Element] public var ownerName: String? = null

    public fun getDateCreated() : java.util.Date?{
        if (dateCreated == null)
            return null

        var date = DateHelper.parseRFC822(dateCreated)

        return date
    }

    public fun getDateModified() : java.util.Date?{
        if (dateModified == null)
            return null

        var date = DateHelper.parseRFC822(dateModified)
        return date
    }
}
