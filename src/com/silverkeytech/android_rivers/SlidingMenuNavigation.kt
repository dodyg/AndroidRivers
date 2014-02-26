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
import java.util.ArrayList
import android.util.TypedValue

data class NavItem (val id: Int, val text: String, val icon: Int)

public data class ViewHolder (var name: TextView)

public val SLIDE_MENU_READ: Int = 0
public val SLIDE_MENU_WRITE: Int = 1
public val SLIDE_MENU_DONATE: Int = 2
public val SLIDE_MENU_PRAISE: Int = 3
public val SLIDE_MENU_TRY_OUT: Int = 4
public val SLIDE_MENU_GOOGLE_NEWS: Int = 5
public val SLIDE_MENU_KAYAK_DEALS: Int = 6
public val SLIDE_MENU_FEEDBACK: Int = 7
public val SLIDE_MENU_CRAIGSLIST_LISTING: Int = 8

fun getMainNavigationItems(): ArrayList<NavItem> {
    val navs = arrayListOf(
            //NavItem(id = SLIDE_MENU_READ, text = "Read", icon = 0),
            NavItem(id = SLIDE_MENU_GOOGLE_NEWS, text = "Google News", icon = 0),
            NavItem(id = SLIDE_MENU_KAYAK_DEALS, text = "Flight Deals", icon = 0),
            NavItem(id = SLIDE_MENU_CRAIGSLIST_LISTING, text = "Craigslist", icon = 0),
            //NavItem(id = SLIDE_MENU_WRITE, text = "Write", icon = 0),
            //NavItem(id = SLIDE_MENU_DONATE, text = "Donate", icon = 0),
            NavItem(id = SLIDE_MENU_PRAISE, text = "Spread Android Rivers", icon = 0),
            NavItem(id = SLIDE_MENU_FEEDBACK, text = "Make This Better!", icon = 0)
            //NavItem(id = SLIDE_MENU_TRY_OUT, text = "Try Out", icon = 0)
    )
    return navs
}

fun fillSlidingMenuNavigation(navs: List<NavItem>, view: View, onClick: (NavItem) -> Unit) {

    val inflater: LayoutInflater = view.getContext()!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    val adapter = object : ArrayAdapter<NavItem>(view.getContext()!!, R.layout.slide_menu_item, navs){
        public override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val text = navs[position].text
            return currentListItem(inflater, text, convertView, parent, 18f)
        }
    }

    val list = view.findView<ListView>(R.id.main_slide_menu_navigation)
    list.setAdapter(adapter)
    list.setOnItemClickListener(object : OnItemClickListener{
        public override fun onItemClick(p0: AdapterView<out Adapter?>, p1: View, p2: Int, p3: Long) {
            val item = navs.get(p2)
            onClick(item)
        }
    })
}

private fun currentListItem(inflater: LayoutInflater, text: String, convertView: View?, parent: ViewGroup?, textSize: Float): View {
    var holder: ViewHolder?

    var vw: View? = convertView

    if (vw == null){
        vw = inflater.inflate(R.layout.slide_menu_item, parent, false)
        holder = ViewHolder(vw!!.findView<TextView>(R.id.slide_menu_item_text_tv))
        vw!!.setTag(holder)
    }else{
        holder = vw!!.getTag() as ViewHolder
    }

    holder!!.name.setText(text)
    holder!!.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)

    return vw!!
}
