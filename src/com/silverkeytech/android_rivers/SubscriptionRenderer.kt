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

public class SubscriptionRenderer(val context: MainActivity){
    class object {
        public val TAG: String = javaClass<SubscriptionRenderer>().getSimpleName()
    }

    public data class ViewHolder (var riverName: TextView)

    fun handleRiversListing(outlines: Opml) {
        var list = context.findView<ListView>(R.id.main_rivers_lv)

        var vals = ArrayList<Outline>()

        outlines.body?.outline?.forEach {
            vals.add(it!!)
        }

        var values = vals

        var adapter = object : ArrayAdapter<Outline>(context, android.R.layout.simple_list_item_1, android.R.id.text1, values){
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                var vw = convertView
                var holder: ViewHolder?

                if (vw == null){
                    var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    vw = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)

                    holder = ViewHolder(vw!!.findViewById(android.R.id.text1) as TextView)
                    holder!!.riverName.setText(vals[position].toString())
                    vw!!.setTag(holder)
                }else{
                    holder = vw!!.getTag() as ViewHolder
                    holder!!.riverName.setText(vals[position].toString())
                    Log.d(TAG, "List View reused")
                }

                return vw
            }
        }

        list.setAdapter(adapter)

        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long) {
                val currentOutline = values.get(p2)

                var i = Intent(context, javaClass<RiverActivity>())
                i.putExtra(Params.RIVER_URL, currentOutline.url)
                i.putExtra(Params.RIVER_NAME, currentOutline.text)

                if (!currentOutline.language.isNullOrEmpty())
                    i.putExtra(Params.RIVER_LANGUAGE, currentOutline.text)
                else
                    i.putExtra(Params.RIVER_LANGUAGE, "en")

                context.startActivity(i)

            }
        })

        list.setOnItemLongClickListener(object : AdapterView.OnItemLongClickListener{
            public override fun onItemLongClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long): Boolean {
                val currentOutline = values.get(p2)

                //overlay popup at top of clicked overview position
                var item = p1!!
                val popupWidth = item.getWidth()
                val popupHeight = item.getHeight()

                val loc = intArray(0, 1)
                item.getLocationOnScreen(loc)
                val popupX = loc[0]
                val popupY = loc[1]

                var inflater = context.getLayoutInflater()!!
                var x = inflater.inflate(R.layout.river_quick_actions, null, false)!!
                var pp = PopupWindow(x, popupWidth, popupHeight, true)

                x.setBackgroundColor(android.graphics.Color.YELLOW)

                x.setOnClickListener {
                    pp.dismiss()
                }

                var icon = x.findViewById(R.id.river_quick_action_delete_icon) as ImageView
                icon.setOnClickListener {
                    try{
                        var res = DatabaseManager.cmd().bookmark().deleteByUrl(currentOutline.url!!)
                        if (res.isFalse())
                            context.toastee("Error in removing this bookmark ${res.exception?.getMessage()}")
                        else {
                            context.toastee("Bookmark removed")
                            context.refreshBookmarks()
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