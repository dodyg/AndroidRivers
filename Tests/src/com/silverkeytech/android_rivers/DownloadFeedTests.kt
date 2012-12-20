package com.silverkeytech.android_rivers

import android.test.AndroidTestCase
import junit.framework.Assert
import java.io.Console

public class DownloadFeedTests : AndroidTestCase() {
    protected override fun setUp() {
        super<AndroidTestCase>.setUp()
    }

    public fun testStandardRSSFeed(){
        DownloadFeed(this.getContext(), false).executeOnComplete {
            Assert.assertTrue(true)
        }
        .execute("http://static.scripting.com/rss.xml")
    }

}