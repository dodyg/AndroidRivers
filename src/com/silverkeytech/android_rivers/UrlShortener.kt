package com.silverkeytech.android_rivers

import com.github.kevinsawicki.http.HttpRequest
import com.google.gson.Gson

public class shortenData (public val longUrl: String){
}

public class shortenDataReply(public val kind: String, public val id: String, public longUrl: String){
}

public fun googleShorten(url: String): Result<String>
{
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