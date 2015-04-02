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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.silverkeytech.android_rivers.getVisualPref
import com.silverkeytech.android_rivers.startFeedActivity
import com.silverkeytech.android_rivers.handleFontResize
import com.silverkeytech.android_rivers.findView

public class RiverSourcesRenderer(val context: RiverSourcesActivity, val language: String){
    companion object {
        public val TAG: String = javaClass<RiverSourcesRenderer>().getSimpleName()
    }

    fun handleListing(sourcesTitles: List<String>, sourcesUris: List<String>) {
        val textSize = context.getVisualPref().listTextSize
        val inflater = inflater()

        val adapter = object : ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, sourcesTitles){
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val text = sourcesTitles[position].toString()
                return currentListItem(inflater, text, convertView, parent, textSize.toFloat())
            }
        }

        val list = context.findView<ListView>(android.R.id.list)
        list.setAdapter(adapter)
        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>, p1: View, p2: Int, p3: Long) {
                val uri = sourcesUris.get(p2)
                val title = sourcesTitles.get(p2)
                startFeedActivity(context, uri, title, language)
            }
        })
    }

    public data class ViewHolder (var name: TextView)

    fun currentListItem(inflater: LayoutInflater, text: String, convertView: View?, parent: ViewGroup?, textSize: Float): View {
        var holder: ViewHolder?

        var vw: View? = convertView

        if (vw == null){
            vw = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            holder = ViewHolder(vw!!.findView<TextView>(android.R.id.text1))
            vw!!.setTag(holder)
        }else{
            holder = vw!!.getTag() as ViewHolder
        }

        handleFontResize(holder!!.name, text, textSize)

        return vw!!
    }

    fun inflater(): LayoutInflater {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater
    }
}
