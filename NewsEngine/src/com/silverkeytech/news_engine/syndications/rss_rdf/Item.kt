package com.silverkeytech.news_engine.syndications.rss_rdf

import java.util.Date


public class Item(){
    public var title : String? = null
    public var description : String? = null
    public var link : String? = null
    public var about : String? = null
    public var dc : DublinCore = DublinCore()
}
