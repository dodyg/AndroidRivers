package com.silverkeytech.android_rivers.riverjs;

import com.silverkeytech.android_rivers.DateHelper;

import java.util.Date;

public class FeedOpmlHead {
    public String title;
    public String dateCreated;       //RFC822 time
    public String dateModified;      //RFC822 time
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
