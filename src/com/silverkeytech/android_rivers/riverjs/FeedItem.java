package com.silverkeytech.android_rivers.riverjs;

import com.silverkeytech.android_rivers.DateHelper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class FeedItem {
    public String id;
    public String title;
    public String body;
    public String permaLink;
    public String pubDate;
    public String link;
    public String comments;
    public ArrayList<FeedImage> thumbnail;
    public ArrayList<FeedEnclosure> enclosure;
    public ArrayList<FeedSource> source;

    public Boolean isPublicationDate() {
        return pubDate != null && pubDate != "";
    }

    public Date getPublicationDate() {
        try {
            return DateHelper.parseRFC822(pubDate);
        } catch (ParseException e) {
            return null;
        }
    }

    public Boolean containsEnclosure() {
        return enclosure != null && !enclosure.isEmpty();
    }

    @Override
    public String toString() {
        if (title != null && title.length() > 0)
            return android.text.Html.fromHtml(title).toString();
        else
            return android.text.Html.fromHtml(body).toString();
    }
}
