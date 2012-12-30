package com.silverkeytech.android_rivers.syndications.atom;

import org.simpleframework.xml.Element;

public class PersonElement {
    @Element(required = false) //this violates the specification, but AP feed sucks http://hosted2.ap.org/atom/APDEFAULT/3d281c11a96b4ad082fe88aa0db04305
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