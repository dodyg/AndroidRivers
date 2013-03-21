package com.silverkeytech.news_engine.syndications.rss_rdf

import com.silverkeytech.news_engine.syndications.rss_rdf.RdfRssBuilder
import java.io.InputStream
import com.thebuzzmedia.sjxp.XMLParser
import com.silverkeytech.news_engine.xml.textRule

public class RdfRssParser{
    public fun parse(input: InputStream, rss: RdfRssBuilder) {
        var parser = XMLParser<RdfRssBuilder>(channelTitle)
        parser.parse(input, "UTF-8", rss)
    }
}

private val channelTitle = textRule<RdfRssBuilder>("/rdf/channel/title", {(text, rss) ->
})

val channelLink =  textRule<RdfRssBuilder>("/rdf/channel/link", {(text, rss) ->
})

val channelDescription = textRule<RdfRssBuilder>("/rdf/channel/description", {(text, rss) ->
})

