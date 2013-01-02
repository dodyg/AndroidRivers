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
import android.content.Intent
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
import com.silverkeytech.android_rivers.db.DatabaseManager
import com.silverkeytech.android_rivers.outlines.Opml
import com.silverkeytech.android_rivers.outlines.Outline
import java.util.ArrayList
import com.silverkeytech.android_rivers.db.Bookmark
import com.silverkeytech.android_rivers.db.BookmarkCollection

public class BookmarksRenderer(val context: MainActivity){
    class object {
        public val TAG: String = javaClass<BookmarksRenderer>().getSimpleName()
    }

    public data class ViewHolder (var name: TextView)

    fun handleCollection(coll : List<BookmarkCollection>){
        val adapter = object : ArrayAdapter<BookmarkCollection>(context, android.R.layout.simple_list_item_1, android.R.id.text1, coll){
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                var vw = convertView
                var holder: ViewHolder?

                val text = coll[position].toString()
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
        }

        val list = context.findView<ListView>(R.id.main_rivers_lv)
        list.setAdapter(adapter)
        list.setOnItemClickListener(null)
        list.setOnItemLongClickListener(null)
    }

    fun handleListing(bookmarks : List<Bookmark>){
        val adapter = object : ArrayAdapter<Bookmark>(context, android.R.layout.simple_list_item_1, android.R.id.text1, bookmarks){
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                var vw = convertView
                var holder: ViewHolder?

                val text = bookmarks[position].toString()
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
        }

        val list = context.findView<ListView>(R.id.main_rivers_lv)
        list.setAdapter(adapter)
        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long) {
                val bookmark = bookmarks.get(p2)
                var i = startFeedActivityIntent(context, bookmark.url, bookmark.title, bookmark.language)
                context.startActivity(i)
            }
        })

        list.setOnItemLongClickListener(object : AdapterView.OnItemLongClickListener{
            public override fun onItemLongClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long): Boolean {
                val currentBookmark = bookmarks.get(p2)
                showBookmarkListingQuickActionPopup(context, currentBookmark, p1!!, list)
                return true
            }
        })
    }

    fun handleRiversListing(opml: Opml) {
        var outlines = ArrayList<Outline>()

        opml.body?.outline?.forEach {
            outlines.add(it)
        }

        val adapter = object : ArrayAdapter<Outline>(context, android.R.layout.simple_list_item_1, android.R.id.text1, outlines){
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                var vw = convertView
                var holder: ViewHolder?

                val text = outlines[position].toString()
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

                val ix = startRiverActivityIntent(context,currentOutline.url!!, currentOutline.text!!, lang )

                context.startActivity(ix)
            }
        })

        list.setOnItemLongClickListener(object : AdapterView.OnItemLongClickListener{
            public override fun onItemLongClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long): Boolean {
                val currentOutline = outlines.get(p2)
                showRiverListingQuickActionPopup(context, currentOutline, p1!!, list)
                return true
            }
        })
    }

    fun inflater() : LayoutInflater{
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater
    }
}

