package com.silverkeytech.news_engine.syndications.rss_rdf

public class RdfRssBuilder(){
    private val rdf: Rdf = Rdf()
    public val channel: ChannelBuilder = ChannelBuilder(rdf)

    public fun build(): Rdf {
        return rdf
    }

    public class ChannelBuilder(private val rdf : Rdf){
        {
            rdf.channel = Channel()
        }

        public var item: ItemBuilder = ItemBuilder(Item())
            get() = $item
            set(value) {
                $item = value
            }

        public fun setTitle(title : String){
            rdf.channel.title = title
        }

        public fun setDescription(description : String){
            rdf.channel.description = description
        }

        public fun setLink(link : String){
            rdf.channel.link = link
        }

        public fun setDcPublisher(publisher : String){
            rdf.channel.dc.publisher = publisher
        }

        public fun setDcLanguage(lang : String){
            rdf.channel.dc.language = lang
        }

        public fun setDcRights(rights : String){
            rdf.channel.dc.rights = rights
        }

        public fun setDcTitle(title : String){
            rdf.channel.dc.title = title
        }

        public fun setDcCreator(creator : String){
            rdf.channel.dc.creator = creator
        }

        public fun setDcSource(source : String){
            rdf.channel.dc.source = source
        }

        public fun startItem(){
            item = ItemBuilder(Item())
        }

        public fun endItem(){
            rdf.item.add(item.data)
        }
    }

    public class ItemBuilder(public val data: Item){
        public fun setTitle(title : String){
            data.title = title
        }

        public fun setLink(link : String){
            data.link = link
        }

        public fun setDescription(description : String){
            data.description = description
        }

        public fun setAbout(about : String){
            data.about = about
        }

        public fun setDcDate(date : String){
            data.dc.date = date
        }

        public fun setDcLanguage(lang : String){
            data.dc.language = lang
        }

        public fun setDcRights(rights : String){
            data.dc.rights = rights
        }

        public fun setDcSource(source : String){
            data.dc.source = source
        }

        public fun setDcTitle(title : String){
            data.dc.title = title
        }
    }
}