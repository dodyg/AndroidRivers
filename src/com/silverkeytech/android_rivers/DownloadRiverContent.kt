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

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.util.Log
import com.github.kevinsawicki.http.HttpRequest
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import com.google.gson.Gson
import com.silverkeytech.android_rivers.riverjs.RiverItemMeta
import com.silverkeytech.android_rivers.riverjs.RiverSite
import com.silverkeytech.android_rivers.riverjs.River
import java.util.ArrayList

//Responsible for handling a river js downloading and display in asynchronous way
public class DownloadRiverContent(it: Context?, val language: String): AsyncTask<String, Int, Result<River>>(){
    class object {
        public val TAG: String = javaClass<DownloadRiverContent>().getSimpleName()
    }

    var dialog: ProgressDialog = ProgressDialog(it)
    var context: Activity = it!! as Activity

    //Prepare stuff before execution
    protected override fun onPreExecute() {
        dialog.setMessage(context.getString(R.string.please_wait_while_loading))
        dialog.setIndeterminate(true)
        dialog.setCancelable(false)
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(android.R.string.cancel), object : DialogInterface.OnClickListener{
            public override fun onClick(p0: DialogInterface?, p1: Int) {
                p0!!.dismiss()
                this@DownloadRiverContent.cancel(true)
            }
        })

        dialog.show()
    }

    var url: String = ""

    //Download river data in a thread
    protected override fun doInBackground(vararg p0: String?): Result<River>? {
        url = p0.get(0)!!
        Log.d(TAG, "Cache is missed for url $url")

        var req: String?
        try{
            req = HttpRequest.get(url)?.body()
        }
        catch(e: HttpRequestException){
            val ex = e.getCause()
            return Result.wrong(ex)
        }

        try{
            val gson = Gson()
            val scrubbed = scrubJsonP(req!!)
            val feeds = gson.fromJson(scrubbed, javaClass<River>())!!

            return Result.right(feeds)
        }
        catch(e: Exception)
        {
            return Result.wrong(e)
        }
    }

    //Once the thread is done.
    protected override fun onPostExecute(result: Result<River>?) {
        dialog.dismiss();
        if (result == null)
            context.toastee("Sorry, we cannot process this feed because the operation is cancelled", Duration.AVERAGE)
        else {
            if (result.isFalse()){
                val error = ConnectivityErrorMessage(
                        timeoutException = "Sorry, we cannot download this feed. The feed site might be down.",
                        socketException = "Sorry, we cannot download this feed. Please check your Internet connection, it might be down.",
                        otherException = "Sorry, we cannot download this feed for the following technical reason : ${result.exception.toString()}"
                )
                context.handleConnectivityError(result.exception, error)
            }else{
                var river = result.value!!
                var sortedNewsItems = river.getSortedNewsItems()

                context.getApplication().getMain().setRiverCache(url, sortedNewsItems)
                RiverContentRenderer(context, language).handleNewsListing(sortedNewsItems)
            }
        }
    }
}

//Take all the news items from several different feeds and combine them into one.
fun River.getSortedNewsItems(): List<RiverItemMeta> {
    var newsItems = ArrayList<RiverItemMeta>()

    var river = this
    for(val f : RiverSite? in river.updatedFeeds?.updatedFeed?.iterator()){
        if (f != null){
            f.item?.forEach{
                if (it != null) {
                    newsItems.add(RiverItemMeta(it, f.feedTitle, f.feedUrl))
                }
            }
        }
    }

    var sortedNewsItems = newsItems.filter { x -> x.item.isPublicationDate()!! }.sort(
            comparator {(p1 : RiverItemMeta, p2 : RiverItemMeta) ->
        val date1 = p1.item.getPublicationDate()
        val date2 = p2.item.getPublicationDate()

        if (date1 != null && date2 != null) {
            date1.compareTo(date2) * -1
        } else if (date1 == null && date2 == null) {
            0
        } else if (date1 == null) {
            -1
        } else {
            1
        }
    })

    return sortedNewsItems
}

