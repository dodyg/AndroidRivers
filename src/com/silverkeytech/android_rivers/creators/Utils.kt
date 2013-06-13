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

package com.silverkeytech.android_rivers.creators

import android.content.Context
import com.silverkeytech.android_rivers.xml.AirportCodeParser
import java.util.ArrayList

public fun getCraigsListCities(context: Context): ArrayList<CraigsListCity> {
    val csv = context.getAssets()!!.open("craigslist_cities")
    val cities = CraigsListCityParser().parse(csv)
    csv.close()
    return cities
}

public fun getCraigsListCategories(context: Context): ArrayList<CraigsListCategory> {
    val csv = context.getAssets()!!.open("craigslist_categories")
    val categories = CraigsListCategoryParser().parse(csv)
    csv.close()
    return categories
}

public fun getAirportCodes(context: Context): ArrayList<AirportCode> {
    val xml = context.getAssets()!!.open("airports.xml")
    val builder = AirportCodeBuilder()
    AirportCodeParser().parse(xml, builder)
    xml.close()

    return builder.build()
}

fun getStringFromAsset(context: Context, path: String): String {
    val xmlFile = context.getAssets()!!.open(path)
    val buffer = ByteArray(xmlFile.available())
    xmlFile.read(buffer)
    xmlFile.close()
    return String(buffer)
}