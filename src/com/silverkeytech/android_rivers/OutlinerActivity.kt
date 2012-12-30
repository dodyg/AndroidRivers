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
import com.actionbarsherlock.app.SherlockActivity
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuItem
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
        public val OUTLINES_URL: String = "OUTLINES_URL"
        public val OUTLINES_EXPAND_ALL: String = "OUTLINES_EXPAND_ALL"
    }

    val LEVEL_NUMBER: Int = 12

    var outlinesUrl: String? = null
    var expandAll: Boolean = false

    var outlinesData: ArrayList<OutlineContent>? = null
    var treeManager = InMemoryTreeStateManager<Long?>()

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        setTheme(this.getVisualPref().getTheme())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.outliner)

        var actionBar = getSupportActionBar()!!
        actionBar.setDisplayShowHomeEnabled(false) //hide the app icon.

        val extras = getIntent()?.getExtras()

        if (extras != null){
            val title = extras.getString(OUTLINES_TITLE)
            if (title != null)
                setTitle(title)

            val url = extras.getString(OUTLINES_URL)
            if (url != null)
                outlinesUrl = url

            expandAll = extras.getBoolean(OUTLINES_EXPAND_ALL)
            outlinesData = extras.getSerializable(OUTLINES_DATA)!! as ArrayList<OutlineContent>

            displayOutlines(treeManager, outlinesData!!, expandAll)
        }
        else
            toastee("There is no data to be displayed in outline mode ", Duration.LONG)
    }

    fun displayOutlines(manager : InMemoryTreeStateManager<Long?>, outlines: ArrayList<OutlineContent>, expandAllInitially: Boolean) {
        manager.clear()
        manager.setVisibleByDefault(expandAllInitially)

        var treeBuilder = TreeBuilder(manager)

        var counter = 0
        for(val e in outlines){
            treeBuilder.sequentiallyAddNextNode(counter.toLong(), e.level)
            counter++
        }

        var treeView = findView<TreeViewList>(R.id.outliner_main_tree)
        val textSize = getVisualPref().getListTextSize()

        var simpleAdapter = SimpleAdapter(this, treeManager, LEVEL_NUMBER, outlines, textSize)
        treeView.setAdapter(simpleAdapter)
    }

    fun collapseOutlines(manager : InMemoryTreeStateManager<Long?>, outlines: ArrayList<OutlineContent>){
        var counter = 0
        for(val e in outlines){
            if (e.level == 0){
                manager.collapseChildren(counter.toLong())
            }
            counter++
        }
    }

    fun expandOutlines(manager : InMemoryTreeStateManager<Long?>, outlines: ArrayList<OutlineContent>){
        var counter = 0
        for(val e in outlines){
            if (e.level == 0){
                manager.expandEverythingBelow(counter.toLong())
            }
            counter++
        }
    }

    val REFRESH: Int = 1

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = getSupportMenuInflater()!!
        inflater.inflate(R.menu.outliner_menu, menu)

        if (outlinesUrl != null){
            menu?.add(0, REFRESH, 0, "Refresh")
            ?.setIcon(R.drawable.ic_menu_refresh)
            ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }

        return true
    }

    internal fun refreshContent(url: String) {
        DownloadOpml(this)
        .executeOnProcessedCompletion({
            res ->
            if (res.isTrue()){
                displayOutlines(treeManager, res.value!!, expandAll)
                this.getApplication().getMain().setOpmlCache(url, res.value!!)
            }
            else{
                toastee("Downloading url fails becaue of ${res.exception?.getMessage()}", Duration.LONG)
            }
        }, { outline -> outline.text != "<rules>" })
        .execute(url)
    }

    public override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.getItemId()){
            REFRESH -> {
                refreshContent(outlinesUrl!!)
                return true
            }
            R.id.outliner_menu_collapse -> {
                collapseOutlines(treeManager, outlinesData!!)
                return true
            }
            R.id.outliner_menu_expand ->{
                expandOutlines(treeManager, outlinesData!!)
                return true
            }
            else ->
                return super.onOptionsItemSelected(item)
        }
    }

    public override fun onBackPressed() {
        super<SherlockActivity>.onBackPressed()
        finish()
    }
}