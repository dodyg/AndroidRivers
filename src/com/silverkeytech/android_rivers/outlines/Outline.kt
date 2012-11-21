package com.silverkeytech.android_rivers.outlines

import java.util.jar.Attributes
import org.simpleframework.xml.Attribute

public class Outline{
    [Attribute] public var text : String? = null
    [Attribute(name = "type")] public var outlineType : String? = null
    [Attribute] public var name : String? = null
    [Attribute] public var url : String? = null
    [Attribute] public var opmlUrl : String? = null
    [Attribute] public var htmlUrl : String? = null
    [Attribute] public var xmlUrl : String? = null
    [Attribute] public var language : String? = null
}