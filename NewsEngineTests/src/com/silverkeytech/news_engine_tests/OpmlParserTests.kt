package com.silverkeytech.news_engine_tests

import org.junit.Test
import org.junit.Assert
import com.silverkeytech.news_engine.outlines.Outline
import java.util.ArrayList
import com.silverkeytech.news_engine.transformXmlToOpml
import com.silverkeytech.news_engine.outlines.OpmlParser

public class OpmlParserTests {
    @Test
    public fun testHeadParsing(){
        com.silverkeytech.news_engine.log = { t, s -> println("$t => $s") }

        val rawXml = downloadRawFeed("http://hobieu.apphb.com/api/1/opml/root")
        //val rawXml = downloadRawFeed("http://smallpicture.com/feed.opml")
        Assert.assertTrue("Raw xml must exists", rawXml.length() > 0)
        val res = transformXmlToOpml(rawXml)

        println("Error message : ${res.exception?.getMessage()}")

        assert(res.isTrue()) { "transform operation must be true not ${res.exception?.getMessage()}" }
        val opml = res.value!!
        assert(!opml.head!!.title!!.isEmpty()) { "Must have title" }
        assert(!opml.head!!.dateModified!!.isEmpty()) { "Must have date modified" }
        assert(!opml.head!!.ownerName!!.isEmpty()) { "Must have owner name" }
        assert(!opml.head!!.ownerEmail!!.isEmpty()) { "Must have owner email" }
        assert(!opml.body!!.outline!!.get(0).text!!.isEmpty()) { "Must have text " }

        println("-----------------------------------")

        //check first level
        traverse(opml.body!!.outline!!, 0)
    }

    fun traverse(outlines : ArrayList<Outline>, level : Int){
        for(o in outlines){
            var spaces = ""

            for (i in  0..level)
                spaces += " "

            println("$spaces$level - ${o.text}")
            traverse(o.outline!!, level + 1)
        }
    }
}