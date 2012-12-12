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

import android.os.Bundle
import android.util.Log
import com.actionbarsherlock.app.SherlockActivity
import com.pl.polidea.treeview.InMemoryTreeStateManager
import com.pl.polidea.treeview.TreeBuilder
import com.pl.polidea.treeview.TreeViewList
import com.silverkeytech.android_rivers.outliner.OutlineContent
import com.silverkeytech.android_rivers.outliner.SimpleAdapter
import java.util.ArrayList

public class OutlinerActivity(): SherlockActivity()
{
    class object {
        public val TAG: String = javaClass<OutlinerActivity>().getSimpleName()
        public val OUTLINES_DATA: String = "OUTLINES_DATA"
        public val OUTLINES_TITLE: String = "OUTLINES_TITLE"
    }

    val LEVEL_NUMBER: Int = 12

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        setTheme(this.getVisualPref().getTheme())
        super.onCreate(savedInstanceState)

        var actionBar = getSupportActionBar()!!
        actionBar.setDisplayShowHomeEnabled(false) //hide the app icon.

        val extras = getIntent()?.getExtras()

        if (extras != null){
            val title = extras.getString(OUTLINES_TITLE)
            if (title != null)
                setTitle(title)

            val outlines = extras.getSerializable(OUTLINES_DATA)!! as ArrayList<OutlineContent>

            Log.d(TAG, "Length of outlines ${outlines.size}")

            var manager = InMemoryTreeStateManager<Long?>()
            var treeBuilder = TreeBuilder(manager)

            var counter = 0
            for(val e in outlines){
                treeBuilder.sequentiallyAddNextNode(counter.toLong(), e.level)
                counter++
            }

            setContentView(R.layout.outliner)
            var treeView = findView<TreeViewList>(R.id.outliner_main_tree)

            val textSize = getVisualPref().getListTextSize()

            var simpleAdapter = SimpleAdapter(this, manager, LEVEL_NUMBER, outlines, textSize)
            treeView.setAdapter(simpleAdapter)
        }
        else
            toastee("There is no data to be displayed in outline mode ", Duration.LONG)
    }

    public override fun onBackPressed() {
        super<SherlockActivity>.onBackPressed()
        finish()
    }

}