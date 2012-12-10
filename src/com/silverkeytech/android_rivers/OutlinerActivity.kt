package com.silverkeytech.android_rivers

import com.actionbarsherlock.app.SherlockActivity
import android.os.Bundle
import com.pl.polidea.treeview.InMemoryTreeStateManager
import com.pl.polidea.treeview.TreeBuilder
import android.R.layout
import com.pl.polidea.treeview.TreeViewList
import com.silverkeytech.android_rivers.outliner.SimpleStandardAdapter
import java.util.HashSet

public class OutlinerActivity() : SherlockActivity()
{
    class object {
        public val TAG: String = javaClass<OutlinerActivity>().getSimpleName()
    }

    private val DEMO_NODES = intArray (0, 0, 1, 1, 1, 2, 2, 1,
            1, 2, 1, 0, 0, 0, 1, 2, 3, 2, 0, 0, 1, 2, 0, 1, 2, 0, 1 )

    var treeView : TreeViewList? = null
    var simpleAdapter : SimpleStandardAdapter ? = null
    val LEVEL_NUMBER : Int = 4

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        setTheme(this.getVisualPref().getTheme())
        super.onCreate(savedInstanceState)

        var manager = InMemoryTreeStateManager<String?>()
        var treeBuilder = TreeBuilder(manager)

        val nodesLength = DEMO_NODES.size - 1
        for(val i in 0..nodesLength){
            treeBuilder.sequentiallyAddNextNode("Hello good morning and this can be done in multiple levels $i", DEMO_NODES[i])
        }

        setContentView(R.layout.outliner)
        treeView = findView<TreeViewList>(R.id.outliner_main_tree)
        simpleAdapter = SimpleStandardAdapter(this, manager, LEVEL_NUMBER)
        treeView!!.setAdapter(simpleAdapter)
    }

    public override fun onBackPressed() {
        super<SherlockActivity>.onBackPressed()
        finish()
    }
}