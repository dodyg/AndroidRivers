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
import com.silverkeytech.android_rivers.db.Bookmark
import com.silverkeytech.android_rivers.db.BookmarkCollection
import com.silverkeytech.android_rivers.outlines.Opml
import com.silverkeytech.android_rivers.outlines.Outline
import com.silverkeytech.android_rivers.outlines.sortOutlineAsc
import com.silverkeytech.android_rivers.outlines.sortOutlineDesc
import java.util.ArrayList

public class BookmarksRenderer(val context: MainActivity){
    class object {
        public val TAG: String = javaClass<BookmarksRenderer>().getSimpleName()
    }

    fun showMessage(msg: String) {
        val txt = context.findView<TextView>(R.id.main_message_tv)
        if (msg.isNullOrEmpty()){
            txt.setVisibility(View.INVISIBLE)
            txt.setText("")
        }
        else{
            txt.setVisibility(View.VISIBLE)
            txt.setText(msg)
        }
    }

    fun handleCollection(coll: List<BookmarkCollection>) {
        if (coll.count() == 0){
            showMessage("""Collection is useful to group your feeds from 'more news'. Each collection presents the news in one river of news. Use menu option to add a new collection.""")
        }
        else
            showMessage("")

        val adapter = object : ArrayAdapter<BookmarkCollection>(context, android.R.layout.simple_list_item_1, android.R.id.text1, coll){
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                val text = coll[position].toString()
                return currentListItem(text, convertView, parent)
            }
        }

        val list = context.findView<ListView>(R.id.main_rivers_lv)
        list.setAdapter(adapter)
        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long) {
                val current = coll[p2]
                //make the local url and delegate to riveractivity to figure out whether
                //to use this collection data from cache or perform the arduous task of
                //downloading and transforming rss feeds into river
                val localUrl = makeLocalUrl(current.id)
                startRiverActivity(context, localUrl, current.title, "en")
            }
        })

        list.setOnItemLongClickListener(object : AdapterView.OnItemLongClickListener{
            public override fun onItemLongClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long): Boolean {
                val current = coll[p2]
                showCollectionQuickActionPopup(context, current, p1!!, list)
                return true
            }
        })
    }

    fun handleRssListing(bookmarks: List<Bookmark>) {
        if (bookmarks.count() == 0){
            showMessage(context.getString(R.string.empty_rss_items_list)!!)
        }
        else
            showMessage("")

        val adapter = object : ArrayAdapter<Bookmark>(context, android.R.layout.simple_list_item_1, android.R.id.text1, bookmarks){
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                val text = bookmarks[position].toString()
                return currentListItem(text, convertView, parent)
            }
        }

        val list = context.findView<ListView>(R.id.main_rivers_lv)
        list.setAdapter(adapter)
        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long) {
                val bookmark = bookmarks.get(p2)
                startFeedActivity(context, bookmark.url, bookmark.title, bookmark.language)
            }
        })

        list.setOnItemLongClickListener(object : AdapterView.OnItemLongClickListener{
            public override fun onItemLongClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long): Boolean {
                val currentBookmark = bookmarks.get(p2)
                showRssBookmarkQuickActionPopup(context, currentBookmark, p1!!, list)
                return true
            }
        })
    }

    fun handleRiversListing(opml: Opml, sort: Int) {
        var outlines = ArrayList<Outline>()

        opml.body?.outline?.forEach {
            outlines.add(it)
        }

        if (outlines.count() == 0){
            showMessage(context.getString(R.string.empty_river_items_list)!!)
        } else
            showMessage("")

        //perform sorting in the outlines (three choices, asc, desc and none)
        if (sort == PreferenceValue.SORT_ASC){
            outlines = sortOutlineAsc(outlines) as ArrayList<Outline>
        } else if (sort == PreferenceValue.SORT_DESC){
            outlines = sortOutlineDesc(outlines) as ArrayList<Outline>
        }

        val adapter = object : ArrayAdapter<Outline>(context, android.R.layout.simple_list_item_1, android.R.id.text1, outlines){
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                val text = outlines[position].toString()
                return currentListItem(text!!, convertView, parent)
            }
        }

        val list = context.findView<ListView>(R.id.main_rivers_lv)
        list.setAdapter(adapter)

        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long) {
                val currentOutline = outlines.get(p2)

                var lang =
                        if (!currentOutline.language.isNullOrEmpty())
                            currentOutline.language!!
                        else
                            "en"

                startRiverActivity(context, currentOutline.url!!, currentOutline.text!!, lang)
            }
        })

        list.setOnItemLongClickListener(object : AdapterView.OnItemLongClickListener{
            public override fun onItemLongClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long): Boolean {
                val currentOutline = outlines.get(p2)
                showRiverBookmarksQuickActionPopup(context, currentOutline, p1!!, list)
                return true
            }
        })
    }

    public data class ViewHolder (var name: TextView)

    fun currentListItem(text: String, convertView: View?, parent: ViewGroup?): View? {
        var holder: ViewHolder?

        var vw: View? = convertView

        if (vw == null){
            vw = inflater().inflate(android.R.layout.simple_list_item_1, parent, false)

            holder = ViewHolder(vw!!.findViewById(android.R.id.text1) as TextView)
            holder!!.name.setText(text)
            vw!!.setTag(holder)
        }else{
            holder = vw!!.getTag() as ViewHolder
            holder!!.name.setText(text)
        }

        return vw
    }

    fun inflater(): LayoutInflater {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater
    }
}