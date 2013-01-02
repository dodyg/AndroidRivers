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

public class BookmarksRenderer(val context: MainActivity){
    class object {
        public val TAG: String = javaClass<BookmarksRenderer>().getSimpleName()
    }

    public data class ViewHolder (var name: TextView)

    fun handleListing(bookmarks : List<Bookmark>){
        val adapter = object : ArrayAdapter<Bookmark>(context, android.R.layout.simple_list_item_1, android.R.id.text1, bookmarks){
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                var vw = convertView
                var holder: ViewHolder?

                if (vw == null){
                    val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    vw = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)

                    holder = ViewHolder(vw!!.findViewById(android.R.id.text1) as TextView)
                    holder!!.name.setText(bookmarks[position].toString())
                    vw!!.setTag(holder)
                }else{
                    holder = vw!!.getTag() as ViewHolder
                    holder!!.name.setText(bookmarks[position].toString())
                    Log.d(TAG, "List View reused")
                }

                return vw
            }
        }

        val list = context.findView<ListView>(R.id.main_rivers_lv)
        list.setAdapter(adapter)
        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long) {
                val bookmark = bookmarks.get(p2)
                var i = startFeedActivity(context, bookmark.url, bookmark.title, bookmark.language)
                context.startActivity(i)
            }
        })

        list.setOnItemLongClickListener(null)
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

                if (vw == null){
                    val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    vw = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)

                    holder = ViewHolder(vw!!.findViewById(android.R.id.text1) as TextView)
                    holder!!.name.setText(outlines[position].toString())
                    vw!!.setTag(holder)
                }else{
                    holder = vw!!.getTag() as ViewHolder
                    holder!!.name.setText(outlines[position].toString())
                    Log.d(TAG, "List View reused")
                }

                return vw
            }
        }

        val list = context.findView<ListView>(R.id.main_rivers_lv)
        list.setAdapter(adapter)

        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long) {
                val currentOutline = outlines.get(p2)

                val ix = Intent(context, javaClass<RiverActivity>())
                ix.putExtra(Params.RIVER_URL, currentOutline.url)
                ix.putExtra(Params.RIVER_NAME, currentOutline.text)

                if (!currentOutline.language.isNullOrEmpty())
                    ix.putExtra(Params.RIVER_LANGUAGE, currentOutline.language)
                else
                    ix.putExtra(Params.RIVER_LANGUAGE, "en")

                context.startActivity(ix)
            }
        })

        list.setOnItemLongClickListener(object : AdapterView.OnItemLongClickListener{
            public override fun onItemLongClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long): Boolean {
                val currentOutline = outlines.get(p2)

                //overlay popup at top of clicked overview position
                val item = p1!!
                val popupWidth = item.getWidth()
                val popupHeight = item.getHeight()

                val loc = intArray(0, 1)
                item.getLocationOnScreen(loc)
                val popupX = loc[0]
                val popupY = loc[1]

                val inflater = context.getLayoutInflater()!!
                val x = inflater.inflate(R.layout.river_quick_actions, null, false)!!
                val pp = PopupWindow(x, popupWidth, popupHeight, true)

                x.setBackgroundColor(android.graphics.Color.LTGRAY)

                x.setOnClickListener {
                    pp.dismiss()
                }

                val icon = x.findViewById(R.id.river_quick_action_delete_icon) as ImageView
                icon.setOnClickListener {
                    try{
                        val res = DatabaseManager.cmd().bookmark().deleteByUrl(currentOutline.url!!)
                        if (res.isFalse())
                            context.toastee("Error in removing this bookmark ${res.exception?.getMessage()}")
                        else {
                            context.toastee("Bookmark removed")
                            context.refreshRiverBookmarks()
                        }
                    }
                    catch(e: Exception){
                        context.toastee("Error in trying to remove this bookmark ${e.getMessage()}")
                    }
                    pp.dismiss()
                }

                pp.showAtLocation(list, Gravity.TOP or Gravity.LEFT, popupX, popupY)
                return true
            }
        })
    }
}