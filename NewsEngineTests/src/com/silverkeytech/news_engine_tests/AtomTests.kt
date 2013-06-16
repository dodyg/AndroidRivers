package com.silverkeytech.news_engine_tests


import org.junit.Test
import org.junit.Assert
import org.junit.runner.RunWith

public class AtomTests(){
    [Test]
    public fun testAssociatedPress(){
        //val download = downloadAtomFeed("http://hosted2.ap.org/atom/APDEFAULT/3d281c11a96b4ad082fe88aa0db04305")
        val download = downloadAtomFeed("http://daringfireball.net/index.xml")
        Assert.assertTrue("Download must be true instead of ${download.exception?.getMessage()}", download.isTrue())
        val feed = download.value!!
        plog("id ${feed.id}")
        plog("title ${feed.title}")
        plog("updated ${feed.updated}")

        for (a in feed.author?.iterator()){
            plog("Author ${a.name} - ${a.uri} - ${a.email}")
        }

        for(x in feed.entry?.iterator()){
            for (a in x.author?.iterator()){
                plog("Author ${a.name} - ${a.uri} - ${a.email}")
            }
            plog("${x.id} - ${x.title} - ${x.published}")
            plog("${x.content?.`type`} - ${x.content?.value} - ${x.content?.src}")
        }
    }
}
