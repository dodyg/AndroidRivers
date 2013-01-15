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
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.silverkeytech.android_rivers.syndications.SyndicationFeed
import com.silverkeytech.android_rivers.syndications.SyndicationFilter
import com.silverkeytech.android_rivers.syndications.downloadSingleFeed

public class DownloadFeed(it: Context?, ignoreCache: Boolean): AsyncTask<String, Int, Result<SyndicationFeed>>(){
    class object {
        public val TAG: String = javaClass<DownloadFeed>().getSimpleName()
    }

    var context: Activity = it!! as Activity
    var dialog: InfinityProgressDialog = InfinityProgressDialog(context, "Downloading RSS feed")
    val ignoreCache: Boolean = ignoreCache

    protected override fun onPreExecute() {
        dialog.onCancel {
            dlg ->
            dlg.dismiss()
            this@DownloadFeed.cancel(true)
        }

        dialog.show()
    }

    protected override fun doInBackground(p0: Array<String?>): Result<SyndicationFeed>? {
        try{
            val url = p0[0]!!

            val cache = context.getMain().getSyndicationCache(url)
            if (cache != null && !ignoreCache){
                Log.d(TAG, "Cache is hit for feed")
                return Result.right(cache)
            } else {
                val latestDate = daysBeforeNow(PreferenceDefaults.RSS_LATEST_DATE_FILTER_IN_DAYS)
                val res = downloadSingleFeed(url, SyndicationFilter(PreferenceDefaults.RSS_MAX_ITEMS_FILTER, latestDate))

                if (res.isTrue()){
                    context.getMain().setSyndicationCache(url, res.value!!)
                    return res
                }
                else
                    return res
            }
        }
        catch (e: Exception){
            return Result.wrong<SyndicationFeed>(e)
        }
    }

    var rawCallback: ((Result<SyndicationFeed>) -> Unit)? = null

    public fun executeOnComplete(callback: (Result<SyndicationFeed>) -> Unit): DownloadFeed {
        rawCallback = callback
        return this
    }

    protected override fun onPostExecute(result: Result<SyndicationFeed>?) {
        dialog.dismiss()

        if (result == null){
            context.toastee("Sorry, we cannot handle this rss download because operation is cancelled", Duration.AVERAGE)
        }
        else{
            if (result.isFalse()){
                val error = ConnectivityErrorMessage(
                        timeoutException = "Sorry, we cannot download this rss. The subscription site might be down",
                        socketException = "Sorry, we cannot download this rss. Please check your Internet connection, it might be down",
                        otherException = "Sorry, we cannot download this rss for the following technical reason : ${result.exception.toString()}"
                )

                context.handleConnectivityError(result.exception, error)

            } else {
                if (rawCallback != null)
                    rawCallback!!(result)
            }
        }
    }
}