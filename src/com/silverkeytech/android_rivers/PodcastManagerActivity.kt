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
        var files = directory.listFiles()!!.toList().sort(
            comparator {(p1: File, p2: File) ->
                val date1 = p1.lastModified()
                val date2 = p2.lastModified()

                date1.compareTo(date2) * -1
            })
        renderFileListing(files)
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

        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long) {
                val currentFile = files[p2]

                var playIntent = Intent()
                playIntent.setAction(android.content.Intent.ACTION_VIEW);
                playIntent.setDataAndType(Uri.fromFile(currentFile), "audio/*");
                startActivity(playIntent)
            }
        })
    }
}