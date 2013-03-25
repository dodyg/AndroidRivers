package com.silverkeytech.news_engine.syndications.rss_rdf

import java.util.Date

import com.silverkeytech.news_engine.syndications.RssDate
import com.silverkeytech.news_engine.syndications.ParsedDateFormat
import com.silverkeytech.news_engine.DateHelper
import com.silverkeytech.news_engine.log
import com.silverkeytech.news_engine.syndications.parseDate
import com.silverkeytech.news_engine.syndications.getDateInFormat

public class DublinCore(){
    public var date: String? = null
    public var language: String? = null
    public var rights: String? = null
    public var source: String? = null
    public var title: String? = null
    public var publisher: String? = null
    public var creator: String? = null
    public var subject: String? = null

    public fun getDate() : RssDate {
        return parseDate(date)
    }
    public fun geDateInFormat(status : ParsedDateFormat) : Date? {
        return getDateInFormat(status, date!!)
    }
}