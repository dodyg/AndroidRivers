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

import android.content.DialogInterface
import android.view.View
import android.widget.EditText
import org.holoeverywhere.app.Activity
import org.holoeverywhere.app.AlertDialog
import org.holoeverywhere.app.Dialog
import org.holoeverywhere.widget.Button
import android.view.Window
import android.widget.LinearLayout
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import org.holoeverywhere.widget.TextView
import android.util.TypedValue
import kotlin.dom.addClass
import java.util.ArrayList

public fun dlgClickListener(action: (dlg: DialogInterface?, idx: Int) -> Unit): DialogInterface.OnClickListener {
    return object: DialogInterface.OnClickListener {
        public override fun onClick(p0: DialogInterface?, p1: Int) {
            action(p0, p1)
        }
    }
}

public data class DialogBtn(public val text : String, public val action : (Dialog) -> Unit)

public fun createFlexibleContentDialog(context: Activity, content : View, buttons : Array<DialogBtn>) :  Dialog{

    val dlg: View = context.getLayoutInflater()!!.inflate(R.layout.dialog_flex_content, null)!!
    val contentParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0)

    val dialog = Dialog(context)
    dialog.setContentView(dlg, contentParam)

    val contentLayout = dlg.findViewById(R.id.dialog4_content) as LinearLayout
    contentLayout.addView(content, contentParam)
    contentLayout.setOnClickListener {
        dialog.dismiss()
    }

    val btnParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0)
    btnParam.setMargins(0,0,0,0)

    val buttonLayout = dlg.findViewById(R.id.dialog_flex_content_buttons) as LinearLayout
    for(val e:DialogBtn in buttons.iterator()){
        val b = Button(context)
        b.setText(e.text)
        b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.0)
        b.setOnClickListener {
            e.action(dialog)
        }

        buttonLayout.addView(b, btnParam)
    }

    dialog.setCanceledOnTouchOutside(true)
    return dialog
}

//Create a popup dialog with one text for url submission. Then provide the given url via a callback called 'action'
public fun createSingleInputDialog(context: Activity, title: String, defaultInput: String?, inputHint: String, action: (DialogInterface?, String?) -> Unit): AlertDialog {
    val dlg: View = context.getLayoutInflater()!!.inflate(R.layout.dialog_single_input, null)!!

    //take care of color
    dlg.setDrawingCacheBackgroundColor(context.getStandardDialogBackgroundColor())

    val dialog = AlertDialog.Builder(context)
    dialog.setView(dlg)
    dialog.setTitle(title)

    var inputUrl = dlg.findViewById(R.id.dialog_input)!! as EditText
    inputUrl.setHint(inputHint)

    var default = defaultInput
    if (default == null)
        default = ""

    inputUrl.setText(default)

    dialog.setPositiveButton(context.getString(R.string.ok), dlgClickListener {
        dlg, idx ->
        action(dlg, inputUrl.getText()?.toString())
    })

    dialog.setNegativeButton(context.getString(R.string.cancel), dlgClickListener {
        dlg, idx ->
        dlg?.dismiss()
    })


    val createdDialog = dialog.create()!!
    return createdDialog
}

public fun createFlexibleInputDialog(context: Activity, title: String, inputs : Array<DialogInput>, action: (DialogInterface?, Array<DialogInput>) -> Unit)  : AlertDialog{
    val dlg: View = context.getLayoutInflater()!!.inflate(R.layout.dialog_single_input, null)!!

    val dialog = AlertDialog.Builder(context)
    dialog.setView(dlg)
    dialog.setTitle(title)

    val container = dlg.findViewById(R.id.dialog_flex_input_container) as LinearLayout
    val editParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0)

    val edits = ArrayList<EditText>()

    for(val i in inputs){
        val t = EditText(context)
        t.setHint(i.hint)
        if (!i.defaultInput.isNullOrEmpty())
            t.setText(i.defaultInput)

        edits.add(t)
        container.addView(t, editParam)
    }


    dialog.setPositiveButton(context.getString(R.string.ok), dlgClickListener {
        dlg, idx ->

            //retrieve values
            var i = 0
            for (val inp in inputs){
                val t = edits.get(i)
                inp.value = t.getText().toString()
                i++
            }

            action(dlg, inputs)
    })

    dialog.setNegativeButton(context.getString(R.string.cancel), dlgClickListener {
        dlg, idx ->
        dlg?.dismiss()
    })

    return dialog.create()!!
}

public class DialogInput(val hint : String, val defaultInput : String?, var value : String?){
}


//Create a simple confirmation dialog for dangerous operation such as removal/delete operation. It handles both positive and negative choice.
//The dialog box is automatically closed in either situation
public fun createConfirmationDialog(context: Activity, message: String, positive: () -> Unit, negative: () -> Unit): AlertDialog {
    val dialog = AlertDialog.Builder(context)
    dialog.setTitle(context.getString(R.string.confirmation_dialog_title))
    dialog.setMessage(message)

    dialog.setPositiveButton(context.getString(R.string.yes), dlgClickListener {
        dlg, idx ->
        positive()
        dlg?.dismiss()
    })

    dialog.setNegativeButton(context.getString(R.string.cancel), dlgClickListener {
        dlg, idx ->
        negative()
        dlg?.dismiss()
    })

    val createdDialog = dialog.create()!!
    return createdDialog
}

