package com.silverkeytech.android_rivers.syndications.atom;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

public class TextElement {
    @Attribute(required = false)
    public String type;
    @Text
    public String value;
}
