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

import android.content.Context
import android.content.DialogInterface
import org.holoeverywhere.app.ProgressDialog

/*
This is a progress dialog that show indeterminate progress (there is no 0..100 % progress indicator)
 */
public class InfinityProgressDialog(ctx: Context, val message: String): ProgressDialog(ctx){
    init {
        super.setCancelable(true)
        super.setIndeterminate(true)
        super.setMessage(message)
    }

    public fun onCancel(action: (DialogInterface) -> Unit) {
        super.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", object : DialogInterface.OnClickListener{
            public override fun onClick(p0: DialogInterface, p1: Int) {
                action(p0)
            }
        })

        super.setOnCancelListener(object : DialogInterface.OnCancelListener{
            public override fun onCancel(p0: DialogInterface?) {
                action(p0!!)
            }
        })
    }
}