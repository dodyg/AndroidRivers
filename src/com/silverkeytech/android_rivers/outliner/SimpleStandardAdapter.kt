package com.silverkeytech.android_rivers.outliner

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.pl.polidea.treeview.AbstractTreeViewAdapter
import com.pl.polidea.treeview.TreeNodeInfo
import com.pl.polidea.treeview.TreeStateManager
import com.silverkeytech.android_rivers.OutlinerActivity
import com.silverkeytech.android_rivers.R

public open class SimpleStandardAdapter(context: OutlinerActivity?, treeStateManager: TreeStateManager<Long?>?, numberOfLevels: Int, val outlines : List<Pair<Int, String>>):
    AbstractTreeViewAdapter<Long?>(context, treeStateManager, numberOfLevels) {

    private open fun getDescription(id: Long?): String? {
        //val hierarchy: Array<Int?>? = getManager()?.getHierarchyDescription(id)
        return outlines.get(id!!.toInt()).second
    }

    public override fun getNewChildView(treeNodeInfo: TreeNodeInfo<Long?>?): View? {
        val viewLayout: LinearLayout? = getActivity()?.getLayoutInflater()?.inflate(R.layout.outliner_list_item, null) as LinearLayout?
        return updateView(viewLayout, treeNodeInfo)
    }

    public override fun updateView(view: View?, treeNodeInfo: TreeNodeInfo<Long?>?): LinearLayout? {
        val viewLayout: LinearLayout? = view as LinearLayout?
        val descriptionView: TextView? = viewLayout?.findViewById(R.id.outliner_list_item_description) as TextView?
        val levelView: TextView? = viewLayout?.findViewById(R.id.outliner_list_item_level) as TextView?
        descriptionView?.setText(getDescription(treeNodeInfo?.getId()))
        return viewLayout
    }

    public override fun getItemId(i: Int): Long {
        return getTreeId(i)!!
    }
}
