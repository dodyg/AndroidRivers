package com.silverkeytech.android_rivers

import android.os.Bundle
import android.util.Log
import com.actionbarsherlock.app.SherlockActivity
import com.github.kevinsawicki.http.HttpRequest
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import com.pl.polidea.treeview.InMemoryTreeStateManager
import com.pl.polidea.treeview.TreeBuilder
import com.pl.polidea.treeview.TreeViewList
import com.silverkeytech.android_rivers.outliner.SimpleStandardAdapter
import com.silverkeytech.android_rivers.outliner.transformXmlToOpml
import com.silverkeytech.android_rivers.outliner.traverse
import java.util.ArrayList
import com.silverkeytech.android_rivers.outliner.OutlineContent

public class OutlinerActivity(): SherlockActivity()
{
    class object {
        public val TAG: String = javaClass<OutlinerActivity>().getSimpleName()
        public val OUTLINES_DATA : String = "OUTLINES_DATA"
    }

    val LEVEL_NUMBER: Int = 6

    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        setTheme(this.getVisualPref().getTheme())
        super.onCreate(savedInstanceState)

        val extras = getIntent()?.getExtras()

        if (extras != null){
            val outlines = extras.getSerializable(OUTLINES_DATA)!! as List<OutlineContent>

            var manager = InMemoryTreeStateManager<Long?>()
            var treeBuilder = TreeBuilder(manager)

            var counter = 0
            for(val e in outlines){
                treeBuilder.sequentiallyAddNextNode(counter.toLong(), e.level)
                counter++
            }

            setContentView(R.layout.outliner)
            var treeView = findView<TreeViewList>(R.id.outliner_main_tree)
            var simpleAdapter = SimpleStandardAdapter(this, manager, LEVEL_NUMBER, outlines)
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