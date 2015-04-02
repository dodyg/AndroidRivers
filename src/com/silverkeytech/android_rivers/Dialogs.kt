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
import android.util.TypedValue
import java.util.ArrayList
import android.text.TextWatcher
import android.text.Editable
import android.text.InputType
import android.view.Gravity
import com.silverkeytech.android_rivers.activities.getStandardDialogBackgroundColor

public val PASSWORD_INPUT: Int = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
public val NORMAL_INPUT: Int = InputType.TYPE_TEXT_VARIATION_NORMAL
public val MULTI_LINE_INPUT: Int = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE

public fun textValidator(action: (String?) -> Unit): TextWatcher {
    return object :  TextWatcher {
        public override fun afterTextChanged(p0: Editable?) {
            action(p0.toString())
        }

        public override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        public override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
        }
    }
}

public fun dlgClickListener(action: (dlg: DialogInterface?, idx: Int) -> Unit): DialogInterface.OnClickListener {
    return object: DialogInterface.OnClickListener {
        public override fun onClick(p0: DialogInterface, p1: Int) {
            action(p0, p1)
        }
    }
}

public data class DialogBtn(public val text: String, public val action: (Dialog) -> Unit)

public fun createFlexibleContentDialog(context: Activity, content: View, dismissOnTouch : Boolean, buttons: Array<DialogBtn>): Dialog {

    val dlg: View = context.getLayoutInflater().inflate(R.layout.dialog_flex_content, null)!!
    val contentParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f)

    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(dlg, contentParam)

    val contentLayout = dlg.findView<LinearLayout>(R.id.dialog4_content)
    contentLayout.addView(content, contentParam)
    contentLayout.setOnClickListener {
        if (dismissOnTouch)
            dialog.dismiss()
    }

    val btnParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
    btnParam.setMargins(0, 0, 0, 0)

    val buttonLayout = dlg.findView<LinearLayout>(R.id.dialog_flex_content_buttons)
    for(e:DialogBtn in buttons.iterator()){
        val b = Button(context)
        b.setText(e.text)
        b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        b.setOnClickListener {
            e.action(dialog)
        }

        buttonLayout.addView(b, btnParam)
    }

    dialog.setCanceledOnTouchOutside(true)
    return dialog
}


//This is a holder for AlertDialog with support for a neutral button that does not dismiss automatically
public class AlertDialogWithNeutralButton (val dlg: AlertDialog, val btnTitle: String, val resetAction: (() -> Unit)) {
    public fun show() {
        dlg.show()

        val neutral = dlg.getButton(DialogInterface.BUTTON_NEUTRAL)!!
        neutral.setText(btnTitle)
        neutral.setOnClickListener(onClickListener {
            resetAction()
        })
    }
}

//Create a popup dialog with one text for url submission. Then provide the given url via a callback called 'action'
public fun createSingleInputDialog(context: Activity, title: String, defaultInput: String?, inputHint: String, action: (DialogInterface?, String?) -> Unit): AlertDialogWithNeutralButton {
    val dlg: View = context.getLayoutInflater().inflate(R.layout.dialog_single_input, null)!!

    //take care of color
    dlg.setDrawingCacheBackgroundColor(context.getStandardDialogBackgroundColor())

    val dialog = AlertDialog.Builder(context)
    dialog.setView(dlg)
    dialog.setTitle(title)

    var inputUrl = dlg.findView<EditText>(R.id.dialog_input)
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

    dialog.setNeutralButton("Clear", dlgClickListener {
        dlg, idx ->
        //deliberately empty
    })

    val createdDialog = AlertDialogWithNeutralButton(dialog.create()!!, context.getString(R.string.clear)) {
        inputUrl.setText("")
    }

    return createdDialog
}

public fun createFlexibleInputDialog(context: Activity, title: String, inputs: Array<DialogInput>, action: (DialogInterface?, Array<DialogInput>) -> Unit): AlertDialog {
    val dlg: View = context.getLayoutInflater().inflate(R.layout.dialog_flex_input, null)!!

    val dialog = AlertDialog.Builder(context)
    dialog.setView(dlg)
    dialog.setTitle(title)

    val container = dlg.findView<LinearLayout>(R.id.dialog_flex_input_container)
    val editParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f)

    val edits = ArrayList<EditText>()

    for(i in inputs){
        val t = EditText(context)
        t.setInputType(i.inputType)
        t.setHint(i.hint)
        if (!i.defaultInput.isNullOrBlank())
            t.setText(i.defaultInput)

        if (i.validator != null)
            t.addTextChangedListener(i.validator)

        if (i.inputType == MULTI_LINE_INPUT){
            t.setMinLines(8)
            t.setMaxLines(10)
            t.setSingleLine(false)
            t.setGravity(Gravity.TOP or Gravity.LEFT)
        }

        edits.add(t)
        container.addView(t, editParam)
    }


    dialog.setPositiveButton(context.getString(R.string.ok), dlgClickListener {
        dlg, idx ->

        //retrieve values
        var i = 0
        for (inp in inputs){
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

public data class DialogInput(val inputType: Int, val hint: String, val defaultInput: String?, val validator: TextWatcher?){
    public var value: String? = null
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

