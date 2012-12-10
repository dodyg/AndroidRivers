package com.silverkeytech.android_rivers.outlines;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.List;

public class Outline {
    @Attribute(required = true)
    public String text;
    @Attribute(name = "type", required = false)
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

    @ElementList(inline = true, required = false)
    public List<Outline> outline = new ArrayList<Outline>();

    @Override
    public String toString() {
        return text;
    }
}