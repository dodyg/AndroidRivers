package com.silverkeytech.news_engine_tests


import org.junit.Test
import org.junit.Assert

public class RssTests{
    @Test
    public fun testScriptingCom(){
        val download = downloadSingleFeed("http://scripting.com/rss.xml")
        Assert.assertTrue("Download must be true", download.isTrue())
        val feed = download.value!!
        plog("Size of download ${feed.items.size()}")
        Assert.assertTrue(feed.items.size() > 0)
    }
}