package com.silverkeytech.news_engine.syndications.rss_rdf

import com.silverkeytech.news_engine.syndications.rss_rdf.RdfRssBuilder
import java.io.InputStream
import com.thebuzzmedia.sjxp.XMLParser
import com.silverkeytech.news_engine.xml.textRule

public class RdfRssParser{
    public fun parse(input: InputStream, rss: RdfRssBuilder) {
        XMLParser.DEBUG = true
        XMLParser.ENABLE_NAMESPACES = false
        var parser = XMLParser<RdfRssBuilder>(channelTitle, channelLink, channelDescription, channelDcLanguage,
                channelDcRights, channelDcPublisher, channelDcCreator, channelDcSource, channelDcTitle)
        parser.parse(input, "UTF-8", rss)
    }
}

val rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
val ns = "http://purl.org/rss/1.0/"
val dc = "http://purl.org/dc/elements/1.1/"

val channelTitle = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]channel/[$ns]title", {(text, rss) ->
    rss.channel.setTitle(text)
})

val channelLink =  textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]channel/[$ns]link", {(text, rss) ->
    rss.channel.setLink(text)
})

val channelDescription = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]channel/[$ns]description", {(text, rss) ->
    rss.channel.setDescription(text)
})

val channelDcLanguage = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]channel/[$dc]language",  {(text, rss) ->
    rss.channel.setDcLanguage(text)
})

val channelDcRights = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]channel/[$dc]rights",  {(text, rss) ->
    rss.channel.setDcRights(text)
})

val channelDcPublisher = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]channel/[$dc]publisher",  {(text, rss) ->
    rss.channel.setDcPublisher(text)
})

val channelDcCreator = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]channel/[$dc]creator",  {(text, rss) ->
    rss.channel.setDcCreator(text)
})

val channelDcSource = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]channel/[$dc]source",  {(text, rss) ->
    rss.channel.setDcSource(text)
})

val channelDcTitle = textRule<RdfRssBuilder>("/[$rdf]RDF/[$ns]channel/[$dc]title",  {(text, rss) ->
    rss.channel.setDcTitle(text)
})





