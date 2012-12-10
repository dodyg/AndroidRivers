package com.silverkeytech.android_rivers

import com.actionbarsherlock.app.SherlockActivity
import android.os.Bundle
import com.pl.polidea.treeview.InMemoryTreeStateManager
import com.pl.polidea.treeview.TreeBuilder
import android.R.layout
import com.pl.polidea.treeview.TreeViewList
import com.silverkeytech.android_rivers.outliner.SimpleStandardAdapter
import java.util.HashSet
import com.silverkeytech.android_rivers.outlines.Opml
import com.github.kevinsawicki.http.HttpRequest
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import android.util.Log
import java.util.ArrayList
import com.silverkeytech.android_rivers.outlines.Outline
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister

public class OutlinerActivity() : SherlockActivity()
{
    class object {
        public val TAG: String = javaClass<OutlinerActivity>().getSimpleName()
    }

    private val DEMO_NODES = intArray (0, 0, 1, 1, 1, 2, 2, 1,
            1, 2, 1, 0, 0, 0, 1, 2, 3, 2, 0, 0, 1, 2, 0, 1, 2, 0, 1 )

    var treeView : TreeViewList? = null
    var simpleAdapter : SimpleStandardAdapter ? = null
    val LEVEL_NUMBER : Int = 6


    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        setTheme(this.getVisualPref().getTheme())
        super.onCreate(savedInstanceState)

        var manager = InMemoryTreeStateManager<String?>()
        var treeBuilder = TreeBuilder(manager)

        val nodesLength = DEMO_NODES.size - 1
        var counter = 0
        for(val e in generateData()){
            Log.d(TAG, "${e.first} - ${e.second}")
            treeBuilder.sequentiallyAddNextNode(e.second + " $counter", e.first)
            counter++
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


    fun generateData() : List<Pair<Int, String>>{
        var req: String? = ""
        val url = "http://opmlviewer.com/Content/Directories.opml"

        try{
            req = HttpRequest.get(url)?.body()
        }
        catch(e: HttpRequestException){
            var ex = e.getCause()
            Log.d(TAG, "Error in downloading OPML $url")
            toastee("Error in downloading OPML from $url")
        }

        Log.d(TAG, "Text : $req")

        val opml = transformFromXml(req?.replace("<?xml version=\"1.0\" encoding=\"utf-8\" ?>",""))

        if(opml.isTrue()){
            val sorted = traverse(opml.value!!)
            toastee("Opml parsing is Great ${sorted.count()}")
            return sorted
        }   else{
            Log.d(TAG, "Error in parsing opml  ${opml.exception?.getMessage()}")
            toastee("Error in parsing opml ${opml.exception?.getMessage()}")
            return ArrayList<Pair<Int,String>>()
        }
    }


    fun traverse (opml : Opml) : ArrayList<Pair<Int, String>>{
        var list = ArrayList<Pair<Int,String>>()

        var level = 0
        for (val o in opml.body?.outline?.iterator())    {
            traverseOutline(level, o, list)
        }
        return list
    }

    fun traverseOutline(level : Int, outline : Outline?, list : ArrayList<Pair<Int, String>>){
        if (outline != null){
            list.add(Pair(level, outline.text!!))

            var lvl = level
            lvl++

            for(val o in outline.outline?.iterator()){
                traverseOutline(lvl, o, list)
            }
        }
    }


    fun transformFromXml(xml: String?): Result<Opml> {
        var serial: Serializer = Persister()

        try{
            val opml: Opml? = serial.read(javaClass<Opml>(), xml, false)
            Log.d(TAG, "OPML ${opml?.head?.title} created on ${opml?.head?.getDateCreated()} and modified on ${opml?.head?.getDateModified()}")
            return Result.right(opml)
        }
        catch (e: Exception){
            Log.d(TAG, "Exception ${e.getMessage()}")
            return Result.wrong(e)
        }
    }

}