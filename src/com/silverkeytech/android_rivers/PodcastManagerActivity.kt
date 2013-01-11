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

import com.actionbarsherlock.app.SherlockListActivity
import android.os.Bundle
import android.os.Environment
import java.io.File
import android.widget.TextView
import android.view.LayoutInflater
import android.widget.ListView
import android.widget.ArrayAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView
import android.widget.Adapter
import android.content.Intent
import android.net.Uri
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuItem
import android.media.MediaMetadataRetriever
import com.silverkeytech.android_rivers.db.getPodcastsFromDb
import com.silverkeytech.android_rivers.db.Podcast
import com.silverkeytech.android_rivers.db.SortingOrder
import com.actionbarsherlock.internal.widget.IcsAdapterView.OnItemLongClickListener
import com.actionbarsherlock.internal.widget.IcsAdapterView
import android.widget.PopupWindow
import android.widget.ImageView
import android.view.Gravity
import com.silverkeytech.android_rivers.db.removePodcast

public class PodcastManagerActivity : SherlockListActivity()
{
    class object {
        public val TAG: String = javaClass<PodcastManagerActivity>().getSimpleName()
    }

    val podcastDirectory = Environment.getExternalStorageDirectory()!!.getPath() + "/" + Environment.DIRECTORY_PODCASTS

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        setTheme(this.getVisualPref().getTheme())
        super<SherlockListActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.podcasts)


        var actionBar = getSupportActionBar()!!
        actionBar.setDisplayShowHomeEnabled(false) //hide the app icon.
        actionBar.setTitle("Podcasts")
        listPodcasts()
    }


    public override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (menu != null){
            val forward = menu.findItem(SWITCH_FORWARD)
            forward?.setEnabled(false)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, SWITCH_BACKWARD, 0, "<")
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        menu?.add(0, SWITCH_FORWARD, 0, ">")
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        menu?.add(0, EXPLORE, 0, "MORE NEWS")
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        return true
    }

    val SWITCH_BACKWARD: Int = 0
    val SWITCH_FORWARD: Int = 1
    val EXPLORE: Int = 2

    public override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.getItemId()) {
            EXPLORE -> {
                downloadOpml(this, "http://hobieu.apphb.com/api/1/opml/root", "Get more news")
                return false
            }
            SWITCH_BACKWARD -> {
                this.finish()
                return true
            }
            else ->
                return super.onOptionsItemSelected(item)
        }
    }

    public fun listPodcasts(){
        val podcast = getPodcastsFromDb(SortingOrder.DESC)
        renderFileListing(podcast)
    }

    public fun refreshPodcasts(){
        listPodcasts()
    }

    //hold the view data for the list
    public data class ViewHolder (var podcastName: TextView)

    //show and prepare the interaction for each individual news item
    fun renderFileListing(podcasts: List<Podcast>) {
        val textSize = getVisualPref().getListTextSize()

        //now sort it so people always have the latest news first
        var list = findView<ListView>(android.R.id.list)
        var inflater: LayoutInflater = getLayoutInflater()!!

        var adapter = object : ArrayAdapter<Podcast>(this, android.R.layout.simple_list_item_1, android.R.id.text1, podcasts) {
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                var currentView = convertView
                var holder: ViewHolder?

                var currentPodcast = podcasts[position]

                var text = currentPodcast.title

                if (currentView == null){
                    currentView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
                    holder = ViewHolder(currentView!!.findViewById(android.R.id.text1) as TextView)
                    holder!!.podcastName.setText(text)
                    currentView!!.setTag(holder)
                }else{
                    holder = currentView?.getTag() as ViewHolder
                    holder!!.podcastName.setText(text)
                }

                if (text.isNullOrEmpty()){
                    currentView?.setVisibility(View.GONE)
                }   else{
                    currentView?.setVisibility(View.VISIBLE)
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
                showPodcastQuickActionPopup(this@PodcastManagerActivity, currentPodcast, p1!!, list)
                return true
            }
        })
    }
}


fun showPodcastQuickActionPopup(context: PodcastManagerActivity, currentPodcast: Podcast, item: View, list: View) {
    //overlay popup at top of clicked overview position
    val popupWidth = item.getWidth()
    val popupHeight = item.getHeight()

    val x = context.getLayoutInflater()!!.inflate(R.layout.main_feed_quick_actions, null, false)!!
    val pp = PopupWindow(x, popupWidth, popupHeight, true)

    x.setBackgroundColor(android.graphics.Color.LTGRAY)

    x.setOnClickListener {
        pp.dismiss()
    }

    val icon = x.findViewById(R.id.main_feed_quick_action_delete_icon) as ImageView
    icon.setOnClickListener {
        val dlg = createConfirmationDialog(context = context, message = "Are you sure about removing this River bookmark?", positive = {
            try{

                var f = File(currentPodcast.localPath)

                if (f.exists())
                    f.delete()

                val res = removePodcast(currentPodcast.id)
                if (res.isFalse())
                    context.toastee("Error in removing this podcast ${res.exception?.getMessage()}")
                else {
                    context.toastee("Podcast removed")
                    context.refreshPodcasts()
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

