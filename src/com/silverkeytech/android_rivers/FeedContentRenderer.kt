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

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
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
import go.goyalla.dict.arabicDictionary.file.ArabicReshape

//Manage the rendering of each news item in the river list
public class FeedContentRenderer(val context: Activity, val language: String){
    class object {
        val STANDARD_NEWS_COLOR = android.graphics.Color.GRAY
        val STANDARD_NEWS_IMAGE = android.graphics.Color.CYAN
        val STANDARD_NEWS_PODCAST = android.graphics.Color.MAGENTA
        val STANDARD_NEWS_SOURCE = android.graphics.Color.BLUE

        public val TAG: String = javaClass<FeedContentRenderer>().getSimpleName()
    }

    //hold the view data for the list
    public data class ViewHolder (var news: TextView, val indicator: TextView)

    //show and prepare the interaction for each individual news item
    fun handleNewsListing(feedTitle : String, feedUrl : String, feedItems: List<SyndicationFeedItem>) {
        val textSize = context.getVisualPref().getListTextSize()

        //now sort it so people always have the latest news first
        var list = context.findView<ListView>(android.R.id.list)

        var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        var adapter = object : ArrayAdapter<SyndicationFeedItem>(context, R.layout.news_item, feedItems) {
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                var currentView = convertView
                var holder: ViewHolder?

                var currentNewsItem = feedItems[position]
                var news = currentNewsItem.toString().trim()

                if (currentView == null){
                    currentView = inflater.inflate(R.layout.news_item, parent, false)

                    holder = ViewHolder(currentView!!.findViewById(R.id.news_item_text_tv) as TextView,
                            currentView!!.findViewById(R.id.news_item_indicator_tv) as TextView)

                    currentView!!.setTag(holder)
                }else{
                    holder = currentView?.getTag() as ViewHolder
                }

                handleForeignTextFont(context, language, holder!!.news, news, textSize.toFloat())

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
                    msg = scrubHtml(currentNews.description)

                dlg.setBackgroundColor(context.getStandardDialogBackgroundColor())

                var body = dlg.findViewById(R.id.feed_details_text_tv)!! as TextView
                handleForeignTextFont(context, language, body, msg, textSize.toFloat())
                handleTextColorBasedOnTheme(context, body)

                var source = dlg.findViewById(R.id.feed_details_source_tv)!! as TextView

                dialog.setView(dlg)

                if (currentNews.hasLink()){
                    dialog.setPositiveButton(context.getString(R.string.go), object : DialogInterface.OnClickListener{
                        public override fun onClick(p0: DialogInterface?, p1: Int) {
                            var i = Intent("android.intent.action.VIEW", Uri.parse(currentNews.link))
                            context.startActivity(i)
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

                                        if (msg.arg1 == Activity.RESULT_OK && !path.isNullOrEmpty()){
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