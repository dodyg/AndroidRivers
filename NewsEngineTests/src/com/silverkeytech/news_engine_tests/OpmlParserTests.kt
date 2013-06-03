package com.silverkeytech.news_engine_tests

import org.junit.Test
import org.junit.Assert

public class OpmlParserTests {
    [Test]
    public fun testHeadParsing(){
        val rawXml = downloadRawFeed("http://hobieu.apphb.com/api/1/opml/root")
        Assert.assertTrue("Raw xml must exists", rawXml.length() > 0)
        val res = transformXmlToOpml(rawXml)

        assert(res.isTrue(), "transform operation must be true not ${res.exception?.getMessage()}")
        val opml = res.value!!
        assert(!opml.head!!.title!!.isEmpty(), "Must have title")
        assert(!opml.head!!.dateModified!!.isEmpty(), "Must have date modified")
        assert(!opml.head!!.ownerName!!.isEmpty(), "Must have owner name")
        assert(!opml.head!!.ownerEmail!!.isEmpty(), "Must have owner email")
    }
}