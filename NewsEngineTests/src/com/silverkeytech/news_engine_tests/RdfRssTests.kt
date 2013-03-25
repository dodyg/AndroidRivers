
package com.silverkeytech.news_engine_tests

import org.junit.Test
import org.junit.Assert

public class RdfRssTest{
    [Test]
    public fun testCraigsList(){
        val download = downloadSingleFeed("http://bloomington.craigslist.org/apa/index.rss")
        Assert.assertTrue("Download must be true", download.isTrue())
        val feed = download.value!!
        plog("Size of download ${feed.items.size()}")
        Assert.assertTrue(feed.items.size() > 0)
    }

    [Test]
    public fun testBasicParsing(){
        val rawXml = downloadRawFeed("http://bloomington.craigslist.org/apa/index.rss").replace("""<?xml version="1.0" encoding="iso-8859-1"?>""", "")
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

        //Assert.assertTrue("RDF must have content", rdf.item.size() > 0)
    }
}