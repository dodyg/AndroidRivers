package com.silverkeytech.android_rivers.syndications.rss;


import org.simpleframework.xml.Attribute;

public class Cloud{
    @Attribute
    public String domain;
    @Attribute
    public Integer port;
    @Attribute
    public String path;
    @Attribute
    public String registerProcedure;
    @Attribute
    public String protocol;
}