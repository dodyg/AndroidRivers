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