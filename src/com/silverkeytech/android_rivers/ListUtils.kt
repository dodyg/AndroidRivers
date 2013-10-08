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

import android.widget.TextView
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater

//A view holder for UI list with one element called name of type TextView
public data class TextViewHolder (public var name: TextView)

fun currentTextViewItem(text: String,
                        convertView: View?,
                        parent: ViewGroup?,
                        textSize: Float,
                        inCollection: Boolean,
                        inflater: LayoutInflater): View {
    var holder: TextViewHolder?

    var vw: View? = convertView

    if (vw == null){
        vw = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
        holder = TextViewHolder(vw!!.findView<TextView>(android.R.id.text1))
        vw!!.setTag(holder)
    }else{
        holder = vw!!.getTag() as TextViewHolder
    }

    if (inCollection)
        handleFontResize(holder!!.name, "$text \u2248", textSize)
    else
        handleFontResize(holder!!.name, text, textSize)

    return vw!!
}
