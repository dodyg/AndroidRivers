package com.silverkeytech.android_rivers

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
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
import com.actionbarsherlock.app.SherlockListFragment
import com.silverkeytech.android_rivers.db.Bookmark
import com.silverkeytech.android_rivers.db.BookmarkCollection
import com.silverkeytech.android_rivers.db.BookmarkKind
import com.silverkeytech.android_rivers.db.DatabaseManager
import com.silverkeytech.android_rivers.db.SortingOrder
import com.silverkeytech.android_rivers.db.getBookmarkCollectionFromDb
import com.silverkeytech.android_rivers.db.getBookmarksFromDb
import com.silverkeytech.android_rivers.db.removeItemByUrlFromBookmarkDb
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuInflater
import com.silverkeytech.android_rivers.db.saveBookmarkToDb
import com.actionbarsherlock.view.MenuItem
import com.silverkeytech.android_rivers.db.Podcast
import com.silverkeytech.android_rivers.db.SortingOrder
import com.silverkeytech.android_rivers.db.getPodcastsFromDb
import com.silverkeytech.android_rivers.db.removePodcast
import java.io.File
import android.content.Intent
import android.net.Uri

public class PodcastListFragment() : SherlockListFragment() {
    class object {
        public val TAG: String = javaClass<PodcastListFragment>().getSimpleName()
    }

    var parent : Activity? = null
    var lastEnteredUrl: String? = ""

    public override fun onAttach(activity: Activity?) {
        super<SherlockListFragment>.onAttach(activity)
        parent = activity
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super<SherlockListFragment>.onCreate(savedInstanceState)
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

        super<SherlockListFragment>.onResume()
    }

    public override fun onHiddenChanged(hidden: Boolean) {
        Log.d(TAG, "OnHiddenChanged $hidden")
        if (!hidden){
            displayPodcasts()
        }
        super<SherlockListFragment>.onHiddenChanged(hidden)
    }


    public override fun onPause() {
        Log.d(TAG, "OnPause")
        super<SherlockListFragment>.onPause()
    }


    public fun displayPodcasts() {
        val podcast = getPodcastsFromDb(SortingOrder.DESC)
        val msg = getView()!!.findViewById(R.id.podcast_list_fragment_message_tv) as TextView

        if (podcast.size == 0)
            msg.setText("All the podcasts you have downloaded will be listed here.")
        else
            msg.setText("")

        renderFileListing(podcast)
    }

    //hold the view data for the list
    public data class ViewHolder (var podcastName: TextView)

    //show and prepare the interaction for each individual news item
    fun renderFileListing(podcasts: List<Podcast>) {
        val textSize = parent!!.getVisualPref().getListTextSize()

        //now sort it so people always have the latest news first
        var list = getView()!!.findViewById(android.R.id.list) as ListView
        var inflater: LayoutInflater = parent!!.getLayoutInflater()!!

        var adapter = object : ArrayAdapter<Podcast>(parent!!, android.R.layout.simple_list_item_1, android.R.id.text1, podcasts) {
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