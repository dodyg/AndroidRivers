package com.silverkeytech.android_rivers.outlines;

import com.silverkeytech.android_rivers.DateHelper;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.Date;


public class Head {
    @Element
    public String title;
    @Element
    public String dateCreated;       //RFC822 time
    @Element
    public String dateModified;      //RFC822 time
    @Element
    public String ownerName;

    public Date getDateCreated() {
        if (dateCreated == null)
            return null;

        try {
            Date date = DateHelper.parseRFC822(dateCreated);
            return date;

        } catch (Exception e) {
            return null;
        }
    }

    public Date getDateModified() {
        if (dateModified == null)
            return null;

        try {
            Date date = DateHelper.parseRFC822(dateModified);
            return date;
        } catch (Exception e) {
            return null;
        }
    }
}
