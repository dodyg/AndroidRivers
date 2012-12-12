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

package com.silverkeytech.android_rivers.outliner

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.pl.polidea.treeview.AbstractTreeViewAdapter
import com.pl.polidea.treeview.TreeNodeInfo
import com.pl.polidea.treeview.TreeStateManager
import com.silverkeytech.android_rivers.OutlinerActivity
import com.silverkeytech.android_rivers.R
import android.util.TypedValue
import com.silverkeytech.android_rivers.toastee
import com.silverkeytech.android_rivers.Duration
import com.pl.polidea.treeview.InMemoryTreeStateManager
import com.pl.polidea.treeview.TreeBuilder
import com.pl.polidea.treeview.TreeViewList
import android.content.Intent
import java.util.ArrayList
import android.util.Log
import com.silverkeytech.android_rivers.DownloadOpml
import com.silverkeytech.android_rivers.getMain
import android.net.Uri

public open class SimpleStandardAdapter(private var context: OutlinerActivity,
                                        treeStateManager: TreeStateManager<Long?>,
                                        numberOfLevels: Int,
                                        val outlines : List<OutlineContent>,
                                        val textSize : Int):
    AbstractTreeViewAdapter<Long?>(context, treeStateManager, numberOfLevels) {

    class object {
        public val TAG: String = javaClass<SimpleStandardAdapter>().getSimpleName()
    }

    private open fun getDescription(id: Long?): String? {
        //val hierarchy: Array<Int?>? = getManager()?.getHierarchyDescription(id)
        var currentOutline = outlines.get(id!!.toInt())
        if (currentOutline.getType() == OutlineType.INCLUDE)
            return currentOutline.text + " #"
        else if (currentOutline.getType() == OutlineType.LINK)
            return currentOutline.text + " ->"
        else if (currentOutline.getType() == OutlineType.BLOGPOST)
            return currentOutline.text + " +"
        else
            return currentOutline.text
    }

    public override fun getNewChildView(treeNodeInfo: TreeNodeInfo<Long?>?): View? {
        val viewLayout: LinearLayout? = getActivity()?.getLayoutInflater()?.inflate(R.layout.outliner_list_item, null) as LinearLayout?
        return updateView(viewLayout, treeNodeInfo)
    }


    fun launchAnotherOutline(outl : ArrayList<OutlineContent>){
        var intent = Intent(Intent.ACTION_MAIN)
        intent.setClass(context, javaClass<OutlinerActivity>())
        intent.putExtra(OutlinerActivity.OUTLINES_DATA, outl )

        Log.d(TAG, "Launch another outline ${outl.size}")
        context.startActivity(intent)
    }

    public override fun updateView(view: View?, treeNodeInfo: TreeNodeInfo<Long?>?): LinearLayout? {
        val viewLayout: LinearLayout? = view as LinearLayout?
        val levelView = viewLayout?.findViewById(R.id.outliner_list_item_level) as TextView

        val descriptionView = viewLayout?.findViewById(R.id.outliner_list_item_description)!! as TextView
        descriptionView.setText(getDescription(treeNodeInfo?.getId()))
        descriptionView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())

        descriptionView.setOnLongClickListener( object : View.OnLongClickListener {

            public override fun onLongClick(p0: View?): Boolean {

                var currentPosition = treeNodeInfo!!.getId()!!.toInt()
                var currentOutline = outlines.get(currentPosition)

                if (currentOutline.getType() == OutlineType.INCLUDE || currentOutline.getType() == OutlineType.BLOGPOST){

                    val url = currentOutline.getAttribute("url")!!

                    val cache = context.getApplication().getMain().getOpmlCache(url)

                    if (cache != null){
                        var intent = Intent(Intent.ACTION_MAIN)
                        intent.setClass(context, javaClass<OutlinerActivity>())
                        var outlines = cache
                        intent.putExtra(OutlinerActivity.OUTLINES_DATA, outlines)

                        context.startActivity(intent)
                    }
                    else {

                        var opml = DownloadOpml(context)
                        opml.setProcessedCompletedCallback( {
                            res ->
                            if (res.isTrue()){
                                var intent = Intent(Intent.ACTION_MAIN)
                                intent.setClass(context, javaClass<OutlinerActivity>())
                                var outlines = res.value!!
                                intent.putExtra(OutlinerActivity.OUTLINES_DATA, outlines)

                                context.getApplication().getMain().setOpmlCache(url, outlines)

                                context.startActivity(intent)
                            }
                            else{
                                context.toastee("Downloading url fails becaue of ${res.exception?.getMessage()}" , Duration.LONG)
                            }
                        }, { outline -> outline.text != "<rules>" })
                        opml.execute(url)
                    }

                    return true
                }
                else if (currentOutline.getType() == OutlineType.LINK){
                    var url = currentOutline.getAttribute("url")
                    try{
                        var i = Intent("android.intent.action.VIEW", Uri.parse(url))
                        context.startActivity(i)
                        return true
                    }
                    catch(e : Exception){
                        context.toastee("Error in trying to launch ${url}")
                        return false
                    }
                }
                else {
                    var idx = currentPosition + 1

                    var level = 0
                    var childList = ArrayList<OutlineContent>()

                    //root level
                    childList.add(OutlineContent(0, currentOutline.text))
                    while (idx < outlines.size){
                        val outline = outlines.get(idx)
                        if ((outline.level - currentOutline.level) < 1)
                            break

                        val newOutline = OutlineContent(outline.level - currentOutline.level, outline.text)
                        newOutline.copyAttributes(outline)

                        childList.add(newOutline)
                        idx++
                    }

                    //do not launch another activity. It's already alone
                    if (outlines.size == childList.size)
                        return true

                    launchAnotherOutline(childList)
                    return true
                }
            }
        })

        return viewLayout
    }

    public override fun getItemId(i: Int): Long {
        return getTreeId(i)!!
    }
}
