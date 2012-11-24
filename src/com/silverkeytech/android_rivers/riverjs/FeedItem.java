package com.silverkeytech.android_rivers.riverjs;

import android.text.TextUtils;
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

    public Boolean isPublicationDate (){
        return pubDate != null && pubDate != "";
    }

    public Date getPublicationDate(){
        try
        {
            return DateHelper.parseRFC822(pubDate);
        }
        catch (ParseException e){
            return null;
        }
    }

    @Override
    public String toString() {
        if (title != null && title.length() > 0)
            return android.text.Html.fromHtml(title).toString();
        else
            return android.text.Html.fromHtml(body).toString();
    }
}
