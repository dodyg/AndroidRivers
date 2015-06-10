package com.silverkeytech.news_engine_tests


import org.junit.Test
import org.junit.Assert
import com.silverkeytech.news_engine.syndications.parseDate

public class RssDateTests{
    @Test
    public fun testDates(){
        com.silverkeytech.news_engine.log = { tag, str -> plog(str) }
        val date = "2013-03-25T10:11:45-04:00"
        val res = parseDate(date)

        Assert.assertTrue(res.isValid)
        plog("Type ${res.status}")
    }
}
