/*
Android Rivers is an app to read and discover news using RiverJs, RSS and OPML format.
Copyright (C) 2012 Dody Gunawinata (dodyg@silverkeytech.com)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package com.silverkeytech.android_rivers

import org.holoeverywhere.app.Activity
import org.holoeverywhere.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.silverkeytech.android_rivers.syndications.SyndicationFeedItem

//Manage the rendering of each news item in the river list
public class FeedContentRenderer(val context: Activity, val language: String){
    class object {
        public val TAG: String = javaClass<FeedContentRenderer>().getSimpleName()
    }

    //hold the view data for the list
    public data class ViewHolder (var news: TextView, val indicator: TextView)

    //show and prepare the interaction for each individual news item
    fun handleNewsListing(feedTitle: String, feedUrl: String, feedItems: List<SyndicationFeedItem>) {
        val textSize = context.getVisualPref().getListTextSize()

        //now sort it so people always have the latest news first
        var list = context.findView<ListView>(android.R.id.list)

        var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        var adapter = object : ArrayAdapter<SyndicationFeedItem>(context, R.layout.news_item, feedItems) {
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                var currentView = convertView
                var holder: ViewHolder?

                val currentNewsItem = feedItems[position]
                val news = currentNewsItem.toString().trim()

                fun showIndicator() {
                    if (currentNewsItem.enclosure != null){
                        val enclosure = currentNewsItem.enclosure!!
                        if (isSupportedImageMime(enclosure.mimeType)){
                            holder?.indicator?.setBackgroundColor(PreferenceDefaults.STANDARD_NEWS_IMAGE)
                        }
                        else{
                            holder?.indicator?.setBackgroundColor(PreferenceDefaults.STANDARD_NEWS_PODCAST)
                        }
                    }else{
                        holder?.indicator?.setBackgroundColor(PreferenceDefaults.STANDARD_NEWS_COLOR)
                    }
                }

                if (currentView == null){
                    currentView = inflater.inflate(R.layout.news_item, parent, false)

                    holder = ViewHolder(currentView!!.findViewById(R.id.news_item_text_tv) as TextView,
                            currentView!!.findViewById(R.id.news_item_indicator_tv) as TextView)

                    currentView!!.setTag(holder)
                }else{
                    holder = currentView?.getTag() as ViewHolder
                }

                handleForeignTextFont(context, language, holder!!.news, news, textSize.toFloat())

                showIndicator()

                if (news.isNullOrEmpty()){
                    currentView?.setVisibility(View.GONE)
                }   else{
                    currentView?.setVisibility(View.VISIBLE)
                }
                return currentView
            }
        }

        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long) {
                val currentNews = feedItems.get(p2);
                var dialog = AlertDialog.Builder(context)

                var dlg: View = inflater.inflate(R.layout.feed_details, null)!!
                var msg: String

                if (currentNews.description.isNullOrEmpty() && !currentNews.title.isNullOrEmpty())
                    msg = scrubHtml(currentNews.title)
                else
                {
                    if (language == "ar")
                        msg = scrubHtml(currentNews.description.limitText(PreferenceDefaults.CONTENT_BODY_ARABIC_MAX_LENGTH))
                    else
                        msg = scrubHtml(currentNews.description.limitText(PreferenceDefaults.CONTENT_BODY_MAX_LENGTH))
                }

                dlg.setBackgroundColor(context.getStandardDialogBackgroundColor())

                var body = dlg.findViewById(R.id.feed_details_text_tv)!! as TextView
                handleForeignTextFont(context, language, body, msg, textSize.toFloat())
                handleTextColorBasedOnTheme(context, body)

                var source = dlg.findViewById(R.id.feed_details_source_tv)!! as TextView

                dialog.setView(dlg)

                if (currentNews.hasLink()){
                    dialog.setPositiveButton(context.getString(R.string.go), object : DialogInterface.OnClickListener{
                        public override fun onClick(p0: DialogInterface?, p1: Int) {
                            startOpenBrowserActivity(context, currentNews.link!!)
                            p0?.dismiss()
                        }
                    })

                    dialog.setNeutralButton(context.getString(R.string.share), object : DialogInterface.OnClickListener{
                        public override fun onClick(p0: DialogInterface?, p1: Int) {
                            var intent = shareActionIntent(context, currentNews.title!!, currentNews.link!!)
                            context.startActivity(Intent.createChooser(intent, "Share link"))
                        }
                    })
                }

                if (currentNews.isPodcast()){
                    dialog.setNegativeButton(context.getString(R.string.podcast), object : DialogInterface.OnClickListener{
                        public override fun onClick(p0: DialogInterface?, p1: Int) {
                            var messenger = Messenger(object : Handler(){
                                public override fun handleMessage(msg: Message?) {
                                    if (msg != null){
                                        val path = msg.obj as String

                                        if (msg.arg1 == android.app.Activity.RESULT_OK && !path.isNullOrEmpty()){
                                            context.toastee("File is successfully downloaded at $path", Duration.LONG)
                                            MediaScannerWrapper.scanPodcasts(context, path)
                                        }else{
                                            context.toastee("Download failed", Duration.LONG)
                                        }
                                    }else{
                                        Log.d(TAG, "handleMessage returns null, which is very weird")
                                    }
                                }
                            })

                            startDownloadService(context, currentNews.title!!, currentNews.enclosure!!.url,
                                    feedTitle, feedUrl, currentNews.description!!,
                                    messenger)
                        }
                    })
                }

                var createdDialog = dialog.create()!!
                createdDialog.setCanceledOnTouchOutside(true)
                dlg.setOnClickListener {
                    createdDialog.dismiss()
                }

                createdDialog.show()
            }
        })

        list.setAdapter(adapter)
    }

}