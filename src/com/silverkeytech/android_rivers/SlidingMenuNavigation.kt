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

data class NavItem (val id : Int, val text : String, val icon : Int)

public data class ViewHolder (var name: TextView)

public val SLIDE_MENU_TRY_OUT : Int = 4
fun getMainNavigationItems() : ArrayList<NavItem> {
    val navs = arrayListOf(
            NavItem(id = 0, text = "Home", icon = 0),
            NavItem(id = 1, text = "Write", icon = 0),
            NavItem(id = 2, text = "Donate", icon = 0),
            NavItem(id = 3, text = "Praise", icon = 0),
            NavItem(id = SLIDE_MENU_TRY_OUT, text = "Try Out", icon = 0)
    )
    return navs
}

fun fillSlidingMenuNavigation(navs : List<NavItem>, view : View, onClick : (NavItem) -> Unit){

    val inflater: LayoutInflater = view.getContext()!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    val adapter = object : ArrayAdapter<NavItem>(view.getContext()!!, R.layout.slide_menu_item, navs){
        public override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            val text = navs[position].text
            return currentListItem(inflater, text, convertView, parent, 14.0)
        }
    }

    val list = view.findViewById(R.id.main_slide_menu_navigation) as ListView
    list.setAdapter(adapter)
    list.setOnItemClickListener(object : OnItemClickListener{
        public override fun onItemClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long) {
            val item = navs.get(p2)
            onClick(item)
        }
    })
}

private fun currentListItem(inflater : LayoutInflater, text: String, convertView: View?, parent: ViewGroup?, textSize : Float): View? {
    var holder: ViewHolder?

    var vw: View? = convertView

    if (vw == null){
        vw = inflater.inflate(R.layout.slide_menu_item, parent, false)
        holder = ViewHolder(vw!!.findViewById(R.id.slide_menu_item_text_tv) as TextView)
        vw!!.setTag(holder)
    }else{
        holder = vw!!.getTag() as ViewHolder
    }

    holder!!.name.setText(text)

    return vw
}
