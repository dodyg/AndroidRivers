/*
Android Rivers is an app to read and discover news using RiverJs, RSS and OPML format.
Copyright (C) 2012 Dody Gunawinata (dodyg@silverkeytech.com)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package com.silverkeytech.news_engine.syndications.atom

import java.io.InputStream
import com.thebuzzmedia.sjxp.XMLParser
import com.silverkeytech.news_engine.xml.textRule
import com.silverkeytech.news_engine.xml.tagRule
import com.silverkeytech.news_engine.xml.attributeRule

public class AtomParser{
    public fun parse(input: InputStream, atom: AtomBuilder) {
        var parser = XMLParser<AtomBuilder>(feedId, feedTitle, feedUpdated, feedIcon, feedLogo,
        feedAuthorTag, feedAuthorName, feedAuthorEmail, feedAuthorUri, feedSubtitle,
        feedLinkTag, feedLinkAttributes,
        entryTag, entryId, entryTitle, entryUpdated, entryPublished,
        entryAuthorTag, entryAuthorName, entryAuthorEmail, entryAuthorUri,
        entryContentTag, entryContentValue, entryContentAttributes,
        entrySummaryTag, entrySummaryValue, entrySummaryAttributes,
        entryLinkTag, entryLinkAttributes)
        parser.parse(input, "UTF-8", atom)
    }
}

val NS = "http://www.w3.org/2005/Atom"

val feedId = textRule<AtomBuilder>("/[$NS]feed/[$NS]id", { text, atom ->
    atom.setId(text)
})

val feedTitle = textRule<AtomBuilder>("/[$NS]feed/[$NS]title", { text, atom ->
    atom.setTitle(text)
})

val feedUpdated = textRule<AtomBuilder>("/[$NS]feed/[$NS]updated", { text, atom ->
    atom.setUpdated(text)
})

val feedLinkTag = tagRule<AtomBuilder>("/[$NS]feed/[$NS]link", { isStartTag, atom ->
    if (isStartTag)
        atom.link.startItem()
    else
        atom.link.endItem()
})

val feedLinkAttributes = attributeRule<AtomBuilder>("/[$NS]feed/[$NS]link", { attrName, attrValue, atom ->
    when(attrName){
        "href" -> atom.link.setHref(attrValue)
        "rel" -> atom.link.setRel(attrValue)
        "hreflang" -> atom.link.setHrefLang(attrValue)
        "type" -> atom.link.setType(attrValue)
        "title" -> atom.link.setTitle(attrValue)
        "length" -> atom.link.setLength(attrValue.toInt())
        else -> {
        }
    }

}, "href", "rel", "hreflang", "type", "title", "length")


val feedAuthorTag = tagRule<AtomBuilder>("/[$NS]feed/[$NS]author", { isStartTag, atom ->
    if (isStartTag)
        atom.author.startItem()
    else
        atom.author.endItem()
})

val feedAuthorEmail = textRule<AtomBuilder>("/[$NS]feed/[$NS]author/[$NS]email", { text, atom ->
    atom.author.setEmail(text)
})

val feedAuthorName = textRule<AtomBuilder>("/[$NS]feed/[$NS]author/[$NS]name", { text, atom ->
    atom.author.setName(text)
})

val feedAuthorUri = textRule<AtomBuilder>("/[$NS]feed/[$NS]author/[$NS]uri", { text, atom ->
    atom.author.setUri(text)
})

val feedIcon = textRule<AtomBuilder>("/[$NS]feed/[$NS]icon", { text , atom ->
    atom.setIcon(text)
})

val feedLogo = textRule<AtomBuilder>("/[$NS]feed/[$NS]logo", { text, atom ->
    atom.setLogo(text)
})

val feedSubtitle = textRule<AtomBuilder>("/[$NS]feed/[$NS]subtitle", { text, atom ->
    atom.setSubtitle(text)
})

val entryTag = tagRule<AtomBuilder>("/[$NS]feed/[$NS]entry", { isStartTag, atom ->
    if (isStartTag)
        atom.entry.startItem()
    else
        atom.entry.endItem()
})


val entryId = textRule<AtomBuilder>("/[$NS]feed/[$NS]entry/[$NS]id", { text, atom ->
    atom.entry.setId(text)
})

val entryTitle = textRule<AtomBuilder>("/[$NS]feed/[$NS]entry/[$NS]title", { text, atom ->
    atom.entry.setTitle(text)
})

val entryUpdated = textRule<AtomBuilder>("/[$NS]feed/[$NS]entry/[$NS]updated", { text, atom ->
    atom.entry.setUpdated(text)
})

val entryPublished = textRule<AtomBuilder>("/[$NS]feed/[$NS]entry/[$NS]published", { text, atom ->
    atom.entry.setPublished(text)
})

val entryContentTag = tagRule<AtomBuilder>("/[$NS]feed/[$NS]entry/[$NS]content", { isStartTag, atom ->
    if (isStartTag)
        atom.entry.startContent()
})

val entryContentValue = textRule<AtomBuilder>("/[$NS]feed/[$NS]entry/[$NS]content", { text, atom ->
    atom.entry.content.setValue(text)
})

val entryContentAttributes = attributeRule<AtomBuilder>("/[$NS]feed/[$NS]entry/[$NS]content", { attrName, attrValue, atom ->
    when(attrName){
        "type" -> atom.entry.content.setType(attrValue)
        "src" -> atom.entry.content.setSource(attrValue)
        else -> {
        }
    }

}, "type", "src")


val entrySummaryTag = tagRule<AtomBuilder>("/[$NS]feed/[$NS]entry/[$NS]summary", { isStartTag, atom ->
    if (isStartTag)
        atom.entry.startSummary()
})

val entrySummaryValue = textRule<AtomBuilder>("/[$NS]feed/[$NS]entry/[$NS]summary", { text, atom ->
    atom.entry.summary.setValue(text)
})

val entrySummaryAttributes = attributeRule<AtomBuilder>("/[$NS]feed/[$NS]entry/[$NS]summary", { attrName, attrValue, atom ->
    when(attrName){
        "type" -> atom.entry.summary.setType(attrValue)
        "src" -> atom.entry.summary.setSource(attrValue)
        else -> {
        }
    }

}, "type", "src")



val entryAuthorTag = tagRule<AtomBuilder>("/[$NS]feed/[$NS]entry/[$NS]author", { isStartTag, atom ->
    if (isStartTag)
        atom.entry.author.startItem()
    else
        atom.entry.author.endItem()
})

val entryAuthorEmail = textRule<AtomBuilder>("/[$NS]feed/[$NS]entry/[$NS]author/[$NS]email", { text, atom ->
    atom.entry.author.setEmail(text)
})

val entryAuthorName = textRule<AtomBuilder>("/[$NS]feed/[$NS]entry/[$NS]author/[$NS]name", { text, atom ->
    atom.entry.author.setName(text)
})

val entryAuthorUri = textRule<AtomBuilder>("/[$NS]feed/[$NS]entry/[$NS]author/[$NS]uri", { text, atom ->
    atom.entry.author.setUri(text)
})


val entryLinkTag = tagRule<AtomBuilder>("/[$NS]feed/[$NS]entry/[$NS]link", { isStartTag, atom ->
    if (isStartTag)
        atom.entry.link.startItem()
    else
        atom.entry.link.endItem()
})

val entryLinkAttributes = attributeRule<AtomBuilder>("/[$NS]feed/[$NS]entry/[$NS]link", { attrName, attrValue, atom ->
    when(attrName){
        "href" -> atom.entry.link.setHref(attrValue)
        "rel" -> atom.entry.link.setRel(attrValue)
        "hreflang" -> atom.entry.link.setHrefLang(attrValue)
        "type" -> atom.entry.link.setType(attrValue)
        "title" -> atom.entry.link.setTitle(attrValue)
        "length" -> atom.entry.link.setLength(attrValue.toInt())
        else -> {
        }
    }

}, "href", "rel", "hreflang", "type", "title", "length")
