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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import com.silverkeytech.android_rivers.db.Podcast
import com.silverkeytech.android_rivers.db.SortingOrder
import com.silverkeytech.android_rivers.db.getPodcastsFromDb
import com.silverkeytech.android_rivers.db.removePodcast
import java.io.File
import org.holoeverywhere.LayoutInflater
import org.holoeverywhere.app.Activity
import org.holoeverywhere.app.ListFragment

public class PodcastListFragment(): ListFragment() {
    class object {
        public val TAG: String = javaClass<PodcastListFragment>().getSimpleName()
    }

    var parent: Activity? = null
    var lastEnteredUrl: String? = ""

    public override fun onAttach(activity: Activity?) {
        super<ListFragment>.onAttach(activity)
        parent = activity
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super<ListFragment>.onCreate(savedInstanceState)
    }

    public override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val vw = inflater!!.inflate(R.layout.podcast_list_fragment, container, false)
        return vw
    }

    public override fun onResume() {
        Log.d(TAG, "OnResume")

        if (getUserVisibleHint()){
            Log.d(TAG, "OnResume - RssListFragment visible")
            displayPodcasts()
        }

        super<ListFragment>.onResume()
    }

    public override fun onHiddenChanged(hidden: Boolean) {
        Log.d(TAG, "OnHiddenChanged $hidden")
        if (!hidden){
            displayPodcasts()
        }
        super<ListFragment>.onHiddenChanged(hidden)
    }


    public override fun onPause() {
        Log.d(TAG, "OnPause")
        super<ListFragment>.onPause()
    }

    fun showMessage(msg: String) {
        val txt = getView()!!.findViewById(R.id.podcast_list_fragment_message_tv) as TextView
        if (msg.isNullOrEmpty()){
            txt.setVisibility(View.INVISIBLE)
            txt.setText("")
        }
        else{
            val textSize = parent!!.getVisualPref().getListTextSize()
            txt.setVisibility(View.VISIBLE)
            handleFontResize(txt, msg, textSize.toFloat())
        }
    }

    public fun displayPodcasts() {
        val podcast = getPodcastsFromDb(SortingOrder.DESC)

        if (podcast.size == 0)
            showMessage("All the podcasts you have downloaded will be listed here.")
        else
            showMessage("")

        renderFileListing(podcast)
    }

    //hold the view data for the list
    public data class ViewHolder (var podcastName: TextView)

    //show and prepare the interaction for each individual news item
    fun renderFileListing(podcasts: List<Podcast>) {
        //now sort it so people always have the latest news first
        var list = getView()!!.findViewById(android.R.id.list) as ListView
        var inflater: LayoutInflater = parent!!.getLayoutInflater()!!

        val textSize = parent!!.getVisualPref().getListTextSize()

        var adapter = object : ArrayAdapter<Podcast>(parent!!, android.R.layout.simple_list_item_1, android.R.id.text1, podcasts) {
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                var currentView = convertView
                var holder: ViewHolder?

                var currentPodcast = podcasts[position]

                var text = currentPodcast.title

                if (currentView == null){
                    currentView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
                    holder = ViewHolder(currentView!!.findViewById(android.R.id.text1) as TextView)
                    currentView!!.setTag(holder)
                }else{
                    holder = currentView?.getTag() as ViewHolder
                }

                if (text.isNullOrEmpty()){
                    currentView?.setVisibility(View.GONE)
                }   else{
                    currentView?.setVisibility(View.VISIBLE)
                    handleFontResize(holder!!.podcastName, text, textSize.toFloat())
                }

                return currentView
            }
        }

        list.setAdapter(adapter)

        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long) {
                val currentPodcast = podcasts[p2]

                var playIntent = Intent()
                playIntent.setAction(android.content.Intent.ACTION_VIEW);
                playIntent.setDataAndType(Uri.fromFile(File(currentPodcast.localPath)), "audio/*");
                startActivity(playIntent)
            }
        })

        list.setOnItemLongClickListener(object : AdapterView.OnItemLongClickListener{
            public override fun onItemLongClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long): Boolean {
                val currentPodcast = podcasts[p2]
                showPodcastQuickActionPopup(parent!!, currentPodcast, p1!!, list)
                return true
            }
        })
    }

    fun showPodcastQuickActionPopup(context: Activity, currentPodcast: Podcast, item: View, list: View) {
        //overlay popup at top of clicked overview position
        val popupWidth = item.getWidth()
        val popupHeight = item.getHeight()

        val x = context.getLayoutInflater()!!.inflate(R.layout.podcast_quick_actions, null, false)!!
        val pp = PopupWindow(x, popupWidth, popupHeight, true)

        x.setBackgroundColor(android.graphics.Color.LTGRAY)

        x.setOnClickListener {
            pp.dismiss()
        }

        val icon = x.findViewById(R.id.podcast_quick_action_delete_icon) as ImageView
        icon.setOnClickListener {
            val dlg = createConfirmationDialog(context = context, message = "Are you sure about deleting this podcast?", positive = {
                try{
                    var f = File(currentPodcast.localPath)

                    if (f.exists())
                        f.delete()

                    val res = removePodcast(currentPodcast.id)
                    if (res.isFalse())
                        context.toastee("Error in removing this podcast ${res.exception?.getMessage()}")
                    else {
                        context.toastee("Podcast removed")
                        displayPodcasts()
                    }
                }
                catch(e: Exception){
                    context.toastee("Error in trying to remove this bookmark ${e.getMessage()}")
                }
                pp.dismiss()
            }, negative = {
                pp.dismiss()
            })

            dlg.show()
        }

        val itemLocation = getLocationOnScreen(item)
        pp.showAtLocation(list, Gravity.TOP or Gravity.LEFT, itemLocation.x, itemLocation.y)
    }

}