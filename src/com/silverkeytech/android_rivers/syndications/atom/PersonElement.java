package com.silverkeytech.android_rivers.syndications.atom;

import org.simpleframework.xml.Element;

public class PersonElement {
    @Element
    public String name;

    @Element(required = false)
    public String email;

    @Element(required = false)
    public String uri;
}

/*
<author>
  <name>John Doe</name>
  <email>JohnDoe@example.com</email>
  <uri>http://example.com/~johndoe</uri>
</author>
*/