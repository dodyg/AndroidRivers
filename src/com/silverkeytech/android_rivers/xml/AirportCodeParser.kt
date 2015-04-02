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

package com.silverkeytech.android_rivers.xml

import com.silverkeytech.android_rivers.creators.AirportCodeBuilder
import com.thebuzzmedia.sjxp.XMLParser
import java.io.InputStream

public class AirportCodeParser{
    public fun parse(input: InputStream, airportCodes: AirportCodeBuilder) {
        var parser = XMLParser<AirportCodeBuilder>(airportTag, airportName, airportCode)
        parser.parse(input, airportCodes)
    }
}


val airportTag = tagRule<AirportCodeBuilder>("/iata/iata_airport_codes", { isStartTag, arpt ->
    if (isStartTag)
        arpt.startItem()
    else
        arpt.endItem()
})

val airportName = textRule<AirportCodeBuilder>("/iata/iata_airport_codes/airport", { text, arpt ->
    arpt.setName(text)
})

val airportCode = textRule<AirportCodeBuilder>("/iata/iata_airport_codes/code", { text, arpt ->
    arpt.setCode(text)
})


