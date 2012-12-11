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
        return outlines.get(id!!.toInt()).text
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
                var idx = currentPosition + 1

                var level = 0
                var childList = ArrayList<OutlineContent>()

                //root level
                childList.add(OutlineContent(0, currentOutline.text))
                do{
                    val outline = outlines.get(idx)
                    if ((outline.level - currentOutline.level) < 1)
                        break

                    val newOutline = OutlineContent(outline.level - currentOutline.level, outline.text)
                    childList.add(newOutline)
                    idx++
                }while(idx < outlines.size)

                launchAnotherOutline(childList)
                //context.toastee("Size of child list is ${childList.size}", Duration.LONG)
                return true
            }
        })

        return viewLayout
    }

    public override fun getItemId(i: Int): Long {
        return getTreeId(i)!!
    }
}
