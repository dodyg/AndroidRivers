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

import com.actionbarsherlock.view.ActionMode
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuItem
import org.holoeverywhere.app.Activity
import com.silverkeytech.android_rivers.activities.restart

public interface WithVisualModificationPanel{
    open fun refreshContent()
    open fun getActivity(): Activity
}

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
public class ResizeTextActionMode (private val parent: WithVisualModificationPanel, private var mode: ActionMode?): ActionMode.Callback{
    val INCREASE_SIZE = 1
    val DECREASE_SIZE = 2
    val SWITCH_THEME: Int = 3

    //Get the display text size from preference
    fun increaseTextSize() {
        var pref = parent.getActivity().getVisualPref()
        var textSize = pref.listTextSize
        textSize += 2
        pref.listTextSize = textSize
    }

    fun decreaseTextSize() {
        var pref = parent.getActivity().getVisualPref()
        var textSize = pref.listTextSize
        textSize -= 2
        pref.listTextSize = textSize
    }

    public override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when(item!!.getItemId()){
            INCREASE_SIZE -> {
                increaseTextSize()
                parent.refreshContent()
            }
            DECREASE_SIZE -> {
                decreaseTextSize()
                parent.refreshContent()
            }
            SWITCH_THEME -> {
                parent.getActivity().getVisualPref().switchTheme()
                parent.getActivity().restart()
                return true
            }
            else -> {
                if (mode != null)
                    mode.finish()
            }
        }

        return true
    }

    public override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    public override fun onDestroyActionMode(mode: ActionMode?) {

    }

    public override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {

        menu?.add(0, DECREASE_SIZE, 0, "Decrease Text Size")
        ?.setIcon(android.R.drawable.btn_minus)
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        menu?.add(0, INCREASE_SIZE, 0, "Increase Text Size")
        ?.setIcon(android.R.drawable.btn_plus)
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        val switchText = when(parent.getActivity().getVisualPref().theme){
            R.style.Theme_Sherlock -> "Light Theme"
            R.style.Theme_Sherlock_Light_DarkActionBar -> "Dark Theme"
            R.style.Holo_Theme -> "Light Theme"
            R.style.Holo_Theme_Light -> "Dark Theme"
            else -> ""
        }

        menu?.add(0, SWITCH_THEME, 0, switchText)
        ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)

        return true
    }
}