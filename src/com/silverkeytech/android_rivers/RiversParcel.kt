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

import android.os.Parcelable
import android.os.Parcel

public class RiverParcel(p : Parcel?): Parcelable {

    class object{
        val CREATOR : Parcelable.Creator<RiverParcel> = object : Parcelable.Creator<RiverParcel>{

            public override fun createFromParcel(p0: Parcel?): RiverParcel? {
                return RiverParcel(p0)
            }

            public override fun newArray(p0: Int): Array<RiverParcel?>? {
                return Array<RiverParcel?>(p0,{ null })
            }
        }
    }

    public var title : String? = null
    public var url : String? = null

    {
        if (p != null){
            var data = Array<String>(2, { "" })
            p.readStringArray(data)

            title = data[0]
            url = data[1]
        }
    }

    public override fun describeContents(): Int {
        return 0
    }

    public override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0?.writeStringArray(array(title, url))
    }
}