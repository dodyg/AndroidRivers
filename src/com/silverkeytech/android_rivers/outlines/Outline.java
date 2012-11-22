package com.silverkeytech.android_rivers.outlines;


import org.simpleframework.xml.Attribute;

public class Outline {
    @Attribute(required = true)
    public String text;
    @Attribute(name = "type")
    public String outlineType;
    @Attribute(required = false)
    public String name;
    @Attribute(required = false)
    public String url;
    @Attribute(required = false)
    public String opmlUrl;
    @Attribute(required = false)
    public String htmlUrl;
    @Attribute(required = false)
    public String xmlUrl;
    @Attribute(required = false)
    public String language;

    @Override
    public String toString() {
        return text;
    }
}