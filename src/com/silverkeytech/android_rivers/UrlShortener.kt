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

import com.github.kevinsawicki.http.HttpRequest
import com.google.gson.Gson

public fun googleShorten(url: String): Result<String>
{
    class shortenData (public val longUrl: String)
    class shortenDataReply(public val kind: String, public val id: String, public val longUrl: String)

    try
    {
        val gson = Gson()
        val payload = gson.toJson(shortenData(url))
        var pst = HttpRequest.post("https://www.googleapis.com/urlshortener/v1/url")
        pst?.contentType("application/json")
        pst?.send(payload)

        val code = pst?.code()

        if (code == 200){
            val reply = pst!!.body()!!
            val s = gson.fromJson(reply, javaClass<shortenDataReply>())!!
            return Result.right(s.id)
        }
        else{
            return Result.wrong<String>(null)
        }
    } catch(ex: Exception){
        return Result.wrong<String>(ex)
    }
}