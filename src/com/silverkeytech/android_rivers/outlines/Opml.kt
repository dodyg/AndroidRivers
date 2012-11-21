package com.silverkeytech.android_rivers.outlines

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

[Root(strict=false)]
public class Opml() {
    [Element] public var head: Head? = null
    [Element] public var body: Body? = null
    [Attribute] public var version: String? = null
}
