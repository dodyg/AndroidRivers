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

import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
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
import org.holoeverywhere.widget.Button
import org.holoeverywhere.widget.SeekBar
import android.os.Handler
import android.os.Message
import android.os.Bundle
import android.widget.LinearLayout

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

    public override fun onStart() {
        super<ListFragment>.onStart()
        Log.d(TAG, "OnStart")
        doBindService()
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

    public override fun onStop() {
        super<ListFragment>.onStop()
        doBindService()
    }

    fun showMessage(msg: String) {
        val txt = getView()!!.findViewById(R.id.podcast_list_fragment_message_tv) as TextView
        if (msg.isNullOrEmpty()){
            txt.setVisibility(View.INVISIBLE)
            txt.setText("")
        }
        else {
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
        val list = getView()!!.findViewById(android.R.id.list) as ListView
        val inflater: LayoutInflater = parent!!.getLayoutInflater()!!

        val textSize = parent!!.getVisualPref().getListTextSize()

        val adapter = object : ArrayAdapter<Podcast>(parent!!, android.R.layout.simple_list_item_1, android.R.id.text1, podcasts) {
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                var currentView = convertView
                var holder: ViewHolder?

                val currentPodcast = podcasts[position]
                val text = currentPodcast.title

                if (currentView == null){
                    currentView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
                    holder = ViewHolder(currentView!!.findViewById(android.R.id.text1) as TextView)
                    currentView!!.setTag(holder)
                }
                else {
                    holder = currentView?.getTag() as ViewHolder
                }

                if (text.isNullOrEmpty()){
                    currentView?.setVisibility(View.GONE)
                }
                else {
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

                Log.d(TAG, "Is podcast player paused ${isPodcastPlayerPaused()}")

                if (!isPodcastPlayerPlaying() && !isPodcastPlayerPaused()){
                    val i = Intent(getActivity(), javaClass<PodcastPlayerService>())
                    i.putExtra(Params.PODCAST_TITLE, currentPodcast.title)
                    i.putExtra(Params.PODCAST_PATH, Uri.fromFile(File(currentPodcast.localPath)).toString())
                    getSupportActivity()!!.startService(i)

                    val dlg = createPodcastPlayerDialog(
                            title = currentPodcast.title.limitText(this@PodcastListFragment.getResources()!!.getInteger(R.integer.podcast_player_title_limit)),
                            isNewTrack = true, currentPosition = null, podcastLength = null)
                    dlg.show()

                } else {
                    val dlg = createPodcastPlayerDialog(
                            title = currentlyPlayedPodcastTitle().limitText(this@PodcastListFragment.getResources()!!.getInteger(R.integer.podcast_player_title_limit)),
                            isNewTrack = false, currentPosition = player?.getCurrentPosition(),
                            podcastLength = player?.getPodcastLength())
                    dlg.show()
                }
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

    fun isPodcastPlayerPlaying(): Boolean {
        if (player != null && player!!.isPlaying()){
            return true
        } else
            return false
    }

    fun isPodcastPlayerPaused(): Boolean {
        if (player != null && player!!.isPaused())
            return true
        else
            return false
    }

    fun currentlyPlayedPodcastTitle(): String? {
        if (player != null && (player!!.isPlaying() || player!!.isPaused())){
            return player!!.podcastTitle
        } else
            return null
    }

    private var isPlayerBound: Boolean = false
    private var player: PodcastPlayerService? = null

    val serviceConnection = object : ServiceConnection{
        public override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            player = ( p1 as PodcastPlayerService.ServiceBinder ).getService()
            isPlayerBound = true
            Log.d(TAG, "Player Service connected")
        }

        public override fun onServiceDisconnected(p0: ComponentName?) {
            player = null
            isPlayerBound = false
            Log.d(TAG, "Player Service disconnected")
        }
    }

    fun doBindService() {
        val i = Intent(getActivity(), javaClass<PodcastPlayerService>())
        getActivity()!!.bindService(i, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun doUnbindService() {
        if (isPlayerBound){
            getActivity()!!.unbindService(serviceConnection)
        }
    }

    public fun createPodcastPlayerDialog(title: String, isNewTrack: Boolean, currentPosition: Int?, podcastLength: Int?): Dialog {
        val dlg: View = this.getSupportActivity()!!.getLayoutInflater()!!.inflate(R.layout.dialog_podcast_player, null)!!
        val contentParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0)

        val dialog = Dialog(this.getSupportActivity())
        dialog.setContentView(dlg, contentParam)
        dialog.setTitle(rightPadding(title, this.getResources()!!.getInteger(R.integer.podcast_player_title_limit)))

        val close = dialog.findViewById(R.id.dialog_podcast_player_stop_btn) as Button
        close.setOnClickListener {
            v ->
            if (player != null)
                player!!.stopMusic()

            dialog.dismiss()
        }

        val multi = dialog.findViewById(R.id.dialog_podcast_player_multi_btn) as Button
        multi.setOnClickListener {
            v ->
            if (player != null) {
                if (player!!.isPlaying()){
                    player!!.pauseMusic()
                    multi.setText(this@PodcastListFragment.getString(R.string.play))
                }
                else {
                    player!!.resumeMusic()
                    multi.setText(this@PodcastListFragment.getString(R.string.pause))
                }
            }
        }


        if (isNewTrack)
            multi.setText(this@PodcastListFragment.getString(R.string.pause))
        else
            if (player != null && player!!.isPlaying())
                multi.setText(this@PodcastListFragment.getString(R.string.pause))
            else
                multi.setText(this@PodcastListFragment.getString(R.string.play))

        val progressBar = dialog.findViewById(R.id.dialog_podcast_player_progress_sb) as SeekBar
        val progressText = dialog.findViewById(R.id.dialog_podcast_player_progress_tv) as TextView

        if (currentPosition != null && podcastLength != null){
            val prg = calculateProgress(currentPosition, podcastLength)
            progressText.setText("$prg")
            progressBar.setProgress(prg)
        }

        player!!.progressHandler = createHandler(progressBar, progressText)

        progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            public override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //dg: not implemented by design
            }

            public override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //dg: not implemented by design
            }

            public override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    val duration = player?.getPodcastLength()
                    val pos = player?.getCurrentPosition()

                    if (isPodcastPlayerPlaying()){
                        player!!.pauseMusic()
                    }

                    if (duration != null && pos != null){
                        val positionAtPlayer = (progress * duration) div 100
                        progressText.setText("$progress")
                        player!!.seekToPosition(positionAtPlayer)
                        player!!.resumeMusic()
                        multi.setText(this@PodcastListFragment.getString(R.string.pause))
                        Log.d(TAG, "Resume position at $positionAtPlayer from tottal $duration at progress $progress")
                    }
                }
            }
        })

        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    fun createHandler(progressBar : SeekBar, progressText : TextView): Handler{
        val h = object: Handler() {
            public override fun handleMessage(msg: Message?) {
                if (msg != null){
                    val data = msg.getData()!!
                    val duration = data.getInt(PodcastPlayerService.TOTAL_DURATION)
                    val currentPosition = data.getInt(PodcastPlayerService.CURRENT_POSITION)
                    val progress = calculateProgress(currentPosition, duration)
                    progressBar.setProgress(progress)
                    progressText.setText("$progress")
                    Log.d(TAG, "Progress Handler $progress")
                }
            }
        }
        return h
    }
}

fun calculateProgress(current : Int, total : Int) : Int{
    return (current * 100) div total
}
