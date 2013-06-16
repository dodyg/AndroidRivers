package com.silverkeytech.news_engine.syndications.atom;


public class PersonElement {
    //this violates the specification, but AP feed sucks http://hosted2.ap.org/atom/APDEFAULT/3d281c11a96b4ad082fe88aa0db04305
    public String name;

    public String email;

    public String uri;
}

/*
<author>
  <name>John Doe</name>
  <email>JohnDoe@example.com</email>
  <uri>http://example.com/~johndoe</uri>
</author>
*/