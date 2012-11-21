package com.silverkeytech.android_rivers.outlines;

import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.List;

public class Body {
    @ElementList(inline = true)
    public List<Outline> outline = new ArrayList<Outline>();
}
