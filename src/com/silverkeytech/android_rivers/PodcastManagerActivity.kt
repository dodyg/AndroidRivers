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
        listFile()
    }

    fun listFile(){
        val directory = File(podcastDirectory)
        val files = directory.listFiles()!!
        renderFileListing(files.toList())
    }

    //hold the view data for the list
    public data class ViewHolder (var podcastName: TextView)

    //show and prepare the interaction for each individual news item
    fun renderFileListing(files: List<File>) {
        val textSize = getVisualPref().getListTextSize()

        //now sort it so people always have the latest news first
        var list = findView<ListView>(android.R.id.list)
        var inflater: LayoutInflater = getLayoutInflater()!!

        var adapter = object : ArrayAdapter<File>(this, android.R.layout.simple_list_item_1, android.R.id.text1, files) {
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                var currentView = convertView
                var holder: ViewHolder?

                var currentFile = files[position]
                var text = currentFile.name

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
    }
}