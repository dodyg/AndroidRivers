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

import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.pl.polidea.treeview.AbstractTreeViewAdapter
import com.pl.polidea.treeview.TreeNodeInfo
import com.pl.polidea.treeview.TreeStateManager
import com.silverkeytech.android_rivers.asyncs.DownloadOpmlAsync
import com.silverkeytech.android_rivers.activities.Duration
import com.silverkeytech.android_rivers.activities.OutlinerActivity
import com.silverkeytech.android_rivers.R
import com.silverkeytech.android_rivers.activities.getMain
import com.silverkeytech.android_rivers.startFeedActivity
import com.silverkeytech.android_rivers.startOpenBrowserActivity
import com.silverkeytech.android_rivers.startOpenEmailActivity
import com.silverkeytech.android_rivers.startOutlinerActivity
import com.silverkeytech.android_rivers.startRiverActivity
import com.silverkeytech.android_rivers.activities.toastee
import java.util.ArrayList
import com.silverkeytech.news_engine.outliner.OutlineContent
import com.silverkeytech.news_engine.outliner.OutlineType

public open class SimpleAdapter(private val context: OutlinerActivity,
                                private val treeStateManager: TreeStateManager<Long?>,
                                numberOfLevels: Int,
                                val outlines: List<OutlineContent>,
                                val textSize: Int):
AbstractTreeViewAdapter<Long?>(context, treeStateManager, numberOfLevels) {

    companion object {
        public val TAG: String = javaClass<SimpleAdapter>().getSimpleName()
    }

    private fun getDescription(id: Long?): String? {
        var currentOutline = outlines.get(id!!.toInt())
        return when (currentOutline.getType()){
            OutlineType.INCLUDE -> currentOutline.text + " #"
            OutlineType.LINK -> currentOutline.text + " *"
            OutlineType.BLOGPOST -> currentOutline.text + " +"
            OutlineType.RIVER -> currentOutline.text + " ~"
            OutlineType.RSS -> currentOutline.text + " ::"
            else -> currentOutline.text
        }
    }

    public override fun getNewChildView(treeNodeInfo: TreeNodeInfo<Long?>?): View? {
        val viewLayout: LinearLayout? = getActivity()?.getLayoutInflater()?.inflate(R.layout.outliner_list_item, null) as LinearLayout?
        return updateView(viewLayout, treeNodeInfo)
    }

    public override fun updateView(view: View?, treeNodeInfo: TreeNodeInfo<Long?>?): LinearLayout? {
        val viewLayout: LinearLayout? = view as LinearLayout?

        val descriptionView = viewLayout?.findViewById(R.id.outliner_list_item_description)!! as TextView
        descriptionView.setText(getDescription(treeNodeInfo?.getId()))
        descriptionView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())

        descriptionView.setOnClickListener(object : View.OnClickListener{
            public override fun onClick(p0: View) {
                var currentPosition = treeNodeInfo!!.getId()!!.toInt()
                var currentOutline = outlines.get(currentPosition)

                when(currentOutline.getType()){
                    OutlineType.INCLUDE -> handleOpmlInclude(currentOutline, false)
                    OutlineType.BLOGPOST -> handleOpmlInclude(currentOutline, true)
                    OutlineType.LINK -> handleLink(currentOutline)
                    OutlineType.RIVER -> handleRiver(currentOutline)
                    OutlineType.RSS -> handleRss(currentOutline)
                    else -> {
                        val id = treeNodeInfo.getId()!!
                        Log.d(TAG, "Clicked on id $id")
                        if (treeNodeInfo.isExpanded())
                            treeStateManager.collapseChildren(id)
                        else
                            treeStateManager.expandDirectChildren(id)
                    }
                }
            }
        })

        descriptionView.setOnLongClickListener(object : View.OnLongClickListener{
            public override fun onLongClick(p0: View): Boolean {
                var currentPosition = treeNodeInfo!!.getId()!!.toInt()
                var currentOutline = outlines.get(currentPosition)

                return handleOpmlZoom(currentOutline, currentPosition)
            }
        })

        return viewLayout
    }

    fun handleRiver(currentOutline: OutlineContent) {
        var url = currentOutline.getAttribute("url")!!
        var text = currentOutline.text
        var lang = currentOutline.getAttribute("language")!!

        startRiverActivity(context, url, text, lang)
    }

    fun handleRss(currentOutline: OutlineContent) {
        var url = currentOutline.getAttribute("url")!!
        var text = currentOutline.text
        var lang = currentOutline.getAttribute("language")!!

        startFeedActivity(context, url, text, lang)
    }

    fun handleOpmlZoom(currentOutline: OutlineContent, currentPosition: Int): Boolean {
        var idx = currentPosition + 1
        var childList = ArrayList<OutlineContent>()

        //root level
        childList.add(OutlineContent(0, currentOutline.text))
        while (idx < outlines.size()){
            val outline = outlines.get(idx)
            if ((outline.level - currentOutline.level) < 1)
                break

            val newOutline = OutlineContent(outline.level - currentOutline.level, outline.text)
            newOutline.copyAttributes(outline)

            childList.add(newOutline)
            idx++
        }

        //do not launch another activity. It's already alone
        if (outlines.size() == childList.size())
            return true

        startOutlinerActivity(context, childList, currentOutline.text, null, true)
        return true
    }

    fun handleLink(currentOutline: OutlineContent): Boolean {
        var url = currentOutline.getAttribute("url")
        try{
            if (url!!.contains("mailto:")){
                val email = url!!.replace("mailto:", "")
                startOpenEmailActivity(context, email, "About Android Rivers", "")
            }else{
                startOpenBrowserActivity(context, url!!)
            }
            return true
        }
        catch(e: Exception){
            context.toastee("Error in trying to launch ${url}")
            return false
        }
    }

    fun handleOpmlInclude(currentOutline: OutlineContent, expandAll: Boolean): Boolean {
        val url = currentOutline.getAttribute("url")!!

        val cache = context.getMain().getOpmlCache(url)

        if (cache != null){
            startOutlinerActivity(context, cache, currentOutline.text, url, expandAll)
        }
        else {
            DownloadOpmlAsync(context)
                    .executeOnProcessedCompletion({
                res ->
                if (res.isTrue()){
                    var outlines = res.value!!
                    context.getMain().setOpmlCache(url, outlines)
                    startOutlinerActivity(context, outlines, currentOutline.text, url, expandAll)
                }
                else{
                    context.toastee("Downloading url fails because of ${res.exception?.getMessage()}", Duration.LONG)
                }
            }, { outline -> outline.text != "<rules>" })
                    .execute(url)
        }

        return true
    }

    public override fun getItemId(i: Int): Long {
        return getTreeId(i)!!
    }
}
