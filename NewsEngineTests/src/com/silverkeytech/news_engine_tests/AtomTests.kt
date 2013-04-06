package com.silverkeytech.news_engine_tests


import org.junit.Test
import org.junit.Assert
import org.junit.runner.RunWith

public class AtomTests(){
    [Test]
    public fun testAssociatedPress(){
        val download = downloadSingleFeed("http://hosted2.ap.org/atom/APDEFAULT/3d281c11a96b4ad082fe88aa0db04305")
        Assert.assertTrue("Download must be true", download.isTrue())
        val feed = download.value!!
        plog("Size of download ${feed.items.size()}")
        Assert.assertTrue(feed.items.size() > 0)
    }
}
