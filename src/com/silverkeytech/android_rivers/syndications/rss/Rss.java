package com.silverkeytech.android_rivers.syndications.rss;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class Rss {
    @Element
    public Channel channel;

    @Attribute
    public String version;
}
