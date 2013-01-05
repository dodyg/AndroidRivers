package com.silverkeytech.android_rivers

import android.content.Context
import android.app.ProgressDialog
import android.content.DialogInterface

public class InfinityProgressDialog(ctx : Context, val message : String) : ProgressDialog(ctx){
    {
        super.setCancelable(true)
        super.setIndeterminate(true)
        super.setMessage(message)
    }

    public fun onCancel(action : (DialogInterface) -> Unit){
        super.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", object : DialogInterface.OnClickListener{
            public override fun onClick(p0: DialogInterface?, p1: Int) {
                action(p0!!)
            }
        })

        super.setOnCancelListener(object : DialogInterface.OnCancelListener{
            public override fun onCancel(p0: DialogInterface?) {
                action(p0!!)
            }
        })
    }
}