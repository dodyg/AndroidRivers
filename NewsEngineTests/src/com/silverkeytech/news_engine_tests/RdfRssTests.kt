
package com.silverkeytech.news_engine_tests

import org.junit.Test
import org.junit.Assert
import com.silverkeytech.news_engine.transformXmlToRdfRss

public class RdfRssTest{
    @Test
    public fun testCraigsList(){
        val download = downloadSingleFeed("http://bloomington.craigslist.org/apa/index.rss")
        Assert.assertTrue("Download must be true", download.isTrue())
        val feed = download.value!!
        plog("Size of download ${feed.items.size()}")
        Assert.assertTrue(feed.items.size() > 0)
        plog("Is date parseable ${feed.isDateParseable}")
        plog("Date of first item ${feed.items.get(0).pubDate}")
    }

    @Test
    public fun testUNDPJobs(){
        val rawXml = downloadRawFeed("http://jobs.undp.org/rss_feeds/RAF.xml")
        Assert.assertTrue("Raw xml must exists", rawXml.length() > 0)
        val res = transformXmlToRdfRss(rawXml)

        Assert.assertTrue("Transformation to RDF must be true", res.isTrue())
        val rdf = res.value!!

        Assert.assertTrue("Must have channel title", !rdf.channel.title!!.isEmpty())
        Assert.assertTrue("Must have channel link", !rdf.channel.link!!.isEmpty())
        Assert.assertTrue("Must have channel description", !rdf.channel.link!!.isEmpty())
        Assert.assertTrue("Must have items", rdf.item.size() > 0)

        rdf.item.forEach {
            Assert.assertTrue(!it.title!!.isEmpty())
            Assert.assertTrue(!it.link!!.isEmpty())
        }
    }

    @Test
    public fun testBasicParsing(){
        val rawXml = downloadRawFeed("http://bloomington.craigslist.org/apa/index.rss")
        Assert.assertTrue("Raw xml must exists", rawXml.length() > 0)
        val res = transformXmlToRdfRss(rawXml)

        Assert.assertTrue("Transformation to RDF must be true", res.isTrue())
        val rdf = res.value!!
        Assert.assertTrue("Must have channel title", !rdf.channel.title!!.isEmpty())
        Assert.assertTrue("Must have channel link", !rdf.channel.link!!.isEmpty())
        Assert.assertTrue("Must have channel description", !rdf.channel.link!!.isEmpty())
        Assert.assertTrue("Must have channel language", !rdf.channel.dc.language!!.isEmpty())
        Assert.assertTrue("Must have channel rights", !rdf.channel.dc.rights!!.isEmpty())
        Assert.assertTrue("Must have channel publisher", !rdf.channel.dc.publisher!!.isEmpty())
        Assert.assertTrue("Must have channel creator", !rdf.channel.dc.creator!!.isEmpty())
        Assert.assertTrue("Must have channel source", !rdf.channel.dc.source!!.isEmpty())
        Assert.assertTrue("Must have channel title", !rdf.channel.dc.title!!.isEmpty())
        Assert.assertTrue("Must have items", rdf.item.size() > 0)
        rdf.item.forEach {
            Assert.assertTrue(!it.title!!.isEmpty())
            Assert.assertTrue(!it.link!!.isEmpty())
            //do not test for description
            Assert.assertTrue(!it.dc.date!!.isEmpty())
            Assert.assertTrue(!it.dc.language!!.isEmpty())
            Assert.assertTrue(!it.dc.rights!!.isEmpty())
            Assert.assertTrue(!it.dc.source!!.isEmpty())
            Assert.assertTrue(!it.dc.title!!.isEmpty())
        }

        //Assert.assertTrue("RDF must have content", rdf.item.size() > 0)
    }
}