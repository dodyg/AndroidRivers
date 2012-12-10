package com.silverkeytech.android_rivers.riverjs;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.List;

public class FeedOpmlOutline {
    public String text;
    public String outlineType;
    public String name;
    public String url;
    public String opmlUrl;
    public String htmlUrl;
    public String xmlUrl;
    public String language;
    public String value;

    @ElementList(inline = true, required = false)
    public List<FeedOpmlOutline> outline = new ArrayList<FeedOpmlOutline>();

    @Override
    public String toString() {
        return text;
    }
}
