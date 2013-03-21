package com.silverkeytech.news_engine.syndications

import android.util.Log
import com.silverkeytech.news_engine.syndications.atom.Feed
import java.util.Date
import com.silverkeytech.news_engine.syndications.rss.Rss


public enum class ParsedDateFormat{
    RFC822
    UNKNOWN
    MISSING
    ISO8601_NOMS
    NO_SPACES
}

public data class RssDate(public val status : ParsedDateFormat, public val date : Date?){
    public val isValid : Boolean
        get() = status != ParsedDateFormat.UNKNOWN && status != ParsedDateFormat.MISSING
}

//verify that this rss feed ate are parseable. Thsi is necessary for merging syndication date
public fun verifyRssFeedForDateFitness(r: Rss): Pair<Boolean, ParsedDateFormat?> {
    try
    {
        if (r.channel?.item == null || r.channel!!.item!!.size() == 0)
            return Pair(false, null)

        val i = r.channel!!.item!!.get(0)

        val pubDate = i.getPubDate()!!

        if (pubDate.isValid)
            return Pair(true, pubDate.status)
        else
            return Pair(false, null)
    }
    catch (e: Exception){
        return Pair(false, null)
    }
}

//verify that this atom feed date are parseable. This is necessary for merging syndication date
public fun verifyAtomFeedForDateFitness(f: Feed): Boolean {
    try
    {
        if (f.getUpdated() == null)
            return false

        if (f.entry == null || f.entry!!.size() == 0)
            return false

        val e = f.entry!!.get(0)

        if (e.getUpdated() == null)
            return false

        return true
    }
    catch (e: Exception){
        return false
    }
}

