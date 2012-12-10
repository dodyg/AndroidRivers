package com.silverkeytech.android_rivers.outliner;

import java.util.Arrays;
import java.util.Set;

import com.pl.polidea.treeview.AbstractTreeViewAdapter;
import com.pl.polidea.treeview.TreeNodeInfo;
import com.pl.polidea.treeview.TreeStateManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.silverkeytech.android_rivers.OutlinerActivity;
import com.silverkeytech.android_rivers.R;

/**
 * This is a very simple adapter that provides very basic tree view with a
 * checkboxes and simple item description.
 *
 */
public class SimpleStandardAdapter extends AbstractTreeViewAdapter<String> {


    public SimpleStandardAdapter(final OutlinerActivity treeViewListDemo,
                                 final TreeStateManager<String> treeStateManager,
                                 final int numberOfLevels) {
        super(treeViewListDemo, treeStateManager, numberOfLevels);
    }

    private String getDescription(final String id) {
        final Integer[] hierarchy = getManager().getHierarchyDescription(id);
        //return "-> " + id + Arrays.asList(hierarchy);
        return id;
    }

    @Override
    public View getNewChildView(final TreeNodeInfo<String> treeNodeInfo) {
        final LinearLayout viewLayout = (LinearLayout) getActivity()
                .getLayoutInflater().inflate(R.layout.outliner_list_item, null);
        return updateView(viewLayout, treeNodeInfo);
    }

    @Override
    public LinearLayout updateView(final View view,
                                   final TreeNodeInfo<String> treeNodeInfo) {
        final LinearLayout viewLayout = (LinearLayout) view;
        final TextView descriptionView = (TextView) viewLayout
                .findViewById(R.id.outliner_list_item_description);
        final TextView levelView = (TextView) viewLayout
                .findViewById(R.id.outliner_list_item_level);
        descriptionView.setText(getDescription(treeNodeInfo.getId()));
        //levelView.setText(Integer.toString(treeNodeInfo.getLevel()));
        return viewLayout;
    }

    @Override
    public long getItemId(int i) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

}