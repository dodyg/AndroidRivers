package com.silverkeytech.android_rivers

import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.convert.Registry
import org.simpleframework.xml.convert.RegistryStrategy

public object XmlComponent{
    private var persister : Persister? = null
    public val serial: Serializer
        get() : Serializer {
            if(persister == null){
                val registry = Registry()
                val strategy = RegistryStrategy(registry)
                persister = Persister(strategy)
            }

            return persister!!
        }
}