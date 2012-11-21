package com.silverkeytech.android_rivers.outlines;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class Opml {
    @Element
    public Head head;
    @Element
    public Body body;
    @Attribute
    public String version;
}
