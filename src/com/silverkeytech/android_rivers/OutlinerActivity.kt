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

public class OutlinerActivity() : SherlockActivity()
{
    class object {
        public val TAG: String = javaClass<OutlinerActivity>().getSimpleName()
    }

    var treeView : TreeViewList? = null
    var simpleAdapter : SimpleStandardAdapter ? = null
    val LEVEL_NUMBER : Int = 6


    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        setTheme(this.getVisualPref().getTheme())
        super.onCreate(savedInstanceState)

        var manager = InMemoryTreeStateManager<Long?>()
        var treeBuilder = TreeBuilder(manager)

        var counter = 0
        var outlines = generateData()
        for(val e in outlines){
            treeBuilder.sequentiallyAddNextNode(counter.toLong(), e.first)
            counter++
        }

        setContentView(R.layout.outliner)
        treeView = findView<TreeViewList>(R.id.outliner_main_tree)
        simpleAdapter = SimpleStandardAdapter(this, manager, LEVEL_NUMBER, outlines)
        treeView!!.setAdapter(simpleAdapter)
    }

    public override fun onBackPressed() {
        super<SherlockActivity>.onBackPressed()
        finish()
    }

    fun generateData() : List<Pair<Int, String>>{
        var req: String? = ""
        //val url = "http://opmlviewer.com/Content/Directories.opml"
        val url = "http://static.scripting.com/denver/wo/dave/2012/11/22/archive018.opml"
        try{
            req = HttpRequest.get(url)?.body()
        }
        catch(e: HttpRequestException){
            var ex = e.getCause()
            Log.d(TAG, "Error in downloading OPML $url")
            toastee("Error in downloading OPML from $url")
        }

        Log.d(TAG, "Text : $req")

        val opml = transformXmlToOpml(req?.replace("<?xml version=\"1.0\" encoding=\"utf-8\" ?>",""))

        if(opml.isTrue()){
            val sorted = opml.value!!.traverse()
            return sorted
        }   else{
            Log.d(TAG, "Error in parsing opml  ${opml.exception?.getMessage()}")
            toastee("Error in parsing opml ${opml.exception?.getMessage()}")
            return ArrayList<Pair<Int,String>>()
        }
    }
}