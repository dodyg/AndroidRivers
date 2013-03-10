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
import android.os.AsyncTask
import android.util.Log
import com.silverkeytech.android_rivers.riverjs.River
import com.silverkeytech.android_rivers.riverjs.RiverItemMeta
import com.silverkeytech.android_rivers.riverjs.RiverItemSource
import com.silverkeytech.android_rivers.riverjs.RiverSite
import com.silverkeytech.android_rivers.riverjs.downloadSingleRiver
import com.silverkeytech.android_rivers.riverjs.sortRiverItemMeta
import java.util.ArrayList
import org.holoeverywhere.app.Activity

//Responsible for handling a river js downloading and display in asynchronous way
public class DownloadRiverContent(it: Context?, val language: String): AsyncTask<String, Int, Result<River>>(){
    class object {
        public val TAG: String = javaClass<DownloadRiverContent>().getSimpleName()
    }

    val context: Activity = it!! as Activity
    val dialog: InfinityProgressDialog = InfinityProgressDialog(context, context.getString(R.string.please_wait_while_loading)!!)

    //Prepare stuff before execution
    protected override fun onPreExecute() {
        dialog.onCancel{
            dlg ->
            dlg.dismiss()
            this@DownloadRiverContent.cancel(true)
        }

        dialog.show()
    }

    var url: String = ""

    var rawCallback: ((Result<River>, String) -> Unit)? = null

    public fun executeOnComplete(callback: (Result<River>, String) -> Unit): DownloadRiverContent {
        rawCallback = callback
        return this
    }

    //Download river data in a thread
    protected override fun doInBackground(vararg p0: String?): Result<River>? {
        url = p0.get(0)!!
        Log.d(TAG, "Cache is missed for url $url")

        val res = downloadSingleRiver(url)
        return res
    }

    //Once the thread is done.
    protected override fun onPostExecute(result: Result<River>?) {
        dialog.dismiss()
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
                if (rawCallback != null)
                    rawCallback!!(result, language)
            }
        }
    }
}

//Take all the news items from several different feeds and combine them into one.
fun River.getSortedNewsItems(): List<RiverItemMeta> {
    val newsItems = ArrayList<RiverItemMeta>()

    val river = this
    for(f : RiverSite? in river.updatedFeeds?.updatedFeed?.iterator()){
        if (f != null){
            f.item?.forEach{
                if (it != null) {
                    newsItems.add(RiverItemMeta(it, RiverItemSource(f.feedTitle, f.feedUrl)))
                }
            }
        }
    }

    val sortedNewsItems = sortRiverItemMeta(newsItems)
    return sortedNewsItems
}
