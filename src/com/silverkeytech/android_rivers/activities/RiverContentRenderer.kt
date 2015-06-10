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

package com.silverkeytech.android_rivers.activities

import android.content.Context
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
import com.silverkeytech.news_engine.transformFeedOpmlToOpml
import com.silverkeytech.android_rivers.traverse
import com.silverkeytech.news_engine.riverjs.RiverItemMeta
import org.holoeverywhere.app.Activity
import java.util.ArrayList
import com.silverkeytech.android_rivers.PreferenceDefaults
import com.silverkeytech.android_rivers.R
import com.silverkeytech.android_rivers.handleForeignText
import com.silverkeytech.android_rivers.handleForeignTextStyle
import com.silverkeytech.android_rivers.scrubHtml
import com.silverkeytech.android_rivers.isSupportedImageMime
import com.silverkeytech.android_rivers.limitText
import com.silverkeytech.android_rivers.DialogBtn
import com.silverkeytech.android_rivers.getVisualPref
import com.silverkeytech.android_rivers.asyncs.DownloadImageAsync
import com.silverkeytech.android_rivers.startDownloadService
import com.silverkeytech.android_rivers.MediaScannerWrapper
import com.silverkeytech.android_rivers.handleTextColorBasedOnTheme
import com.silverkeytech.android_rivers.startOpenBrowserActivity
import com.silverkeytech.android_rivers.shareActionIntent
import com.silverkeytech.android_rivers.startOutlinerActivity
import com.silverkeytech.android_rivers.createFlexibleContentDialog
import android.app.Dialog
import android.text.method.ScrollingMovementMethod
import com.silverkeytech.android_rivers.ScrollMotionDetector
import com.silverkeytech.android_rivers.findView

//Manage the rendering of each news item in the river list
public class RiverContentRenderer(val context: Activity, val language: String){
    companion object {
        public val TAG: String = javaClass<RiverContentRenderer>().getSimpleName()
    }

    //hold the view data for the list
    public data class ViewHolder (val news: TextView, val source: TextView, val indicator: TextView)

    //show and prepare the interaction for each individual news item
    fun handleNewsListing(sortedNewsItems: List<RiverItemMeta>) {
        val textSize = context.getVisualPref().listTextSize

        //now sort it so people always have the latest news first

        var list = context.findView<ListView>(android.R.id.list)

        var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        var adapter = object : ArrayAdapter<RiverItemMeta>(context, R.layout.news_item, sortedNewsItems) {
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                var currentView = convertView
                var holder: ViewHolder?

                var currentNewsItem = sortedNewsItems[position]
                var news = currentNewsItem.item.toString()?.trim()

                if (news == null)
                    news = ""

                fun showIndicator() {
                    if (currentNewsItem.item.hasEnclosure()!!){
                        var enclosure = currentNewsItem.item.enclosure!!.get(0)
                        if (isSupportedImageMime(enclosure.`type`!!)){
                            holder?.indicator?.setBackgroundColor(PreferenceDefaults.STANDARD_NEWS_IMAGE)
                        }
                        else{
                            holder?.indicator?.setBackgroundColor(PreferenceDefaults.STANDARD_NEWS_PODCAST)
                        }
                    }
                    else if (currentNewsItem.item.hasSource()!!){
                        holder?.indicator?.setBackgroundColor(PreferenceDefaults.STANDARD_NEWS_SOURCE)
                    }else{
                        holder?.indicator?.setBackgroundColor(PreferenceDefaults.STANDARD_NEWS_COLOR)
                    }
                }

                if (currentView == null){
                    currentView = inflater.inflate(R.layout.news_item, parent, false)

                    holder = ViewHolder(currentView!!.findView<TextView>(R.id.news_item_text_tv),
                            currentView!!.findView<TextView>(R.id.news_item_source_tv),
                            currentView!!.findView<TextView>(R.id.news_item_indicator_tv))

                    handleForeignTextStyle(context, language, holder!!.news, textSize.toFloat())
                    handleForeignTextStyle(context, language, holder!!.source, 11f)

                    currentView!!.setTag(holder)
                }else{
                    holder = currentView?.getTag() as ViewHolder
                    Log.d(TAG, "List View reused")
                }

                handleForeignText(language, holder!!.news, news!!)
                handleForeignText(language, holder!!.source, currentNewsItem.source.title!!)

                showIndicator()

                if (news.isNullOrEmpty()){
                    currentView?.setVisibility(View.GONE)
                }   else{
                    currentView?.setVisibility(View.VISIBLE)
                }
                return currentView!!
            }
        }

        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>, p1: View, p2: Int, p3: Long) {
                val currentNews = sortedNewsItems.get(p2);

                var dlg: View = inflater.inflate(R.layout.news_details, null)!!
                var msg: String

                if (currentNews.item.body.isNullOrEmpty() && !currentNews.item.title.isNullOrEmpty())
                    msg = scrubHtml(currentNews.item.title)
                else {
                    if (language == "ar")
                        msg = scrubHtml(currentNews.item.body.limitText(PreferenceDefaults.CONTENT_BODY_ARABIC_MAX_LENGTH))
                    else
                        msg = scrubHtml(currentNews.item.body.limitText(PreferenceDefaults.CONTENT_BODY_MAX_LENGTH))
                }

                //take care of color
                dlg.setBackgroundColor(context.getStandardDialogBackgroundColor())


                var body = dlg.findView<TextView>(R.id.news_details_text_tv)
                body.setMovementMethod(ScrollingMovementMethod())

                handleForeignText(language, body, msg)
                handleForeignTextStyle(context, language, body, textSize.toFloat())
                handleTextColorBasedOnTheme(context, body)


                var createdDialog : Dialog? = null //important to be declared here because the content click must have access to the future to be created dialog box to close it

                val detector = ScrollMotionDetector()
                val listener = detector.attach(onClickEvent = { ->
                    createdDialog?.dismiss()
                }, onMoveEvent = null)

                body.setOnTouchListener(listener)


                var source = dlg.findView<TextView>(R.id.news_details_source_tv)
                handleForeignText(language, body, msg)
                handleForeignTextStyle(context, language, body, textSize.toFloat())
                handleTextColorBasedOnTheme(context, body)

                val currentLink = currentNews.item.link
                val currentNewsLinkAvailable: Boolean = !currentLink.isNullOrEmpty() && currentLink!!.trim().indexOf("http") == 0

                val buttons = ArrayList<DialogBtn>()
                //check for go link
                if (currentNewsLinkAvailable){
                    buttons.add(DialogBtn(context.getString(R.string.go), { dlg ->
                        startOpenBrowserActivity(context, currentNews.item.link!!)
                        dlg.dismiss()
                    }))

                    buttons.add(DialogBtn(context.getString(R.string.share), { dlg ->
                        var intent = shareActionIntent(currentNews.item.title!!, currentNews.item.link!!)
                        context.startActivity(Intent.createChooser(intent, "Share link"))
                    }))
                }

                //check for image enclosure
                if (currentNews.item.hasEnclosure()!!){
                    var enclosure = currentNews.item.enclosure!!.get(0)
                    if (isSupportedImageMime(enclosure.`type`!!)){
                        buttons.add(DialogBtn("Image", { dlg ->
                            Log.d(TAG, "I am downloading a ${enclosure.url} with type ${enclosure.`type`}")
                            DownloadImageAsync(context).execute(enclosure.url)
                        }))
                    }
                    //assume podcast
                    else {
                        buttons.add(DialogBtn(context.getString(R.string.podcast), { dlg ->
                            var messenger = Messenger(object : Handler(){
                                public override fun handleMessage(msg: Message) {
                                    val path = msg.obj as String

                                    if (msg.arg1 == android.app.Activity.RESULT_OK && !path.isNullOrBlank()){
                                        context.toastee("File is successfully downloaded at $path", Duration.LONG)
                                        MediaScannerWrapper.scanPodcasts(context, path)
                                    }else{
                                        context.toastee("Sorry, I cannot download podcast ${currentNews.item.title}", Duration.LONG)
                                    }
                                }
                            })

                            startDownloadService(context, currentNews.item.title!!, enclosure.url!!,
                                    currentNews.source.title!!, currentNews.source.uri!!, currentNews.item.body!!,
                                    messenger)

                            dlg.dismiss()
                        }))
                    }
                }
                else if (currentNews.item.hasSource()!!){
                    buttons.add(DialogBtn("Outlines", { dlg ->
                        try{
                            val feedOpml = currentNews.item.source!!.get(0).opml!!

                            var opml = transformFeedOpmlToOpml(feedOpml)

                            if (opml.isTrue()){
                                val outlines = opml.value!!.traverse()
                                val title = if (opml.value!!.head!!.title.isNullOrEmpty())
                                    "Opml Comment"
                                else
                                    opml.value!!.head!!.title

                                startOutlinerActivity(context, outlines, title!!, null, true)
                            }
                            else{
                                Log.d(TAG, "Error in transformation feedopml to opml ${opml.exception?.getMessage()}")
                            }
                        }
                        catch(e: Exception){
                            context.toastee("Error in processing opml source ${e.getMessage()}")
                        }
                    }))
                }

                //add blog button
                //                buttons.add(DialogBtn("Blog", {
                //                    dlg ->
                //                    showBlogConfigurationDialog(context){
                //                        res ->
                //                        showPostBlogDialogWithContent(context, "${currentNews.item.title}"){
                //                            res2 ->
                //                            val username = res.get(1).value!!
                //                            val password = res.get(2).value!!
                //                            val post = res2.get(0).value!!
                //                            Log.d(TAG, "Username $username - Password $password - Post content $post")
                //
                //                            val contentMap =  hashMapOf(Params.POST_CONTENT to post)
                //                            if (currentNewsLinkAvailable)
                //                                contentMap.put(Params.POST_LINK, currentNews.item.link!!)
                //
                //                            startBlogPostingService(context,
                //                                    hashMapOf(Params.BLOG_SERVER to "https://androidrivers.wordpress.com/xmlrpc.php",
                //                                            Params.BLOG_USERNAME to username, Params.BLOG_PASSWORD to password),
                //                                    contentMap)
                //                        }
                //                    }
                //                }))

                createdDialog = createFlexibleContentDialog(context = context, content = dlg,
                        dismissOnTouch = true, buttons =  buttons.toArray(arrayOf<DialogBtn>()))
                createdDialog!!.show()
            }
        })

        list.setAdapter(adapter)
    }
}