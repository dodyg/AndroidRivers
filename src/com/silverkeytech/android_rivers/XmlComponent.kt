package com.silverkeytech.android_rivers

import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.Serializer

public object XmlComponent{
    public val serial: Serializer = Persister()
}