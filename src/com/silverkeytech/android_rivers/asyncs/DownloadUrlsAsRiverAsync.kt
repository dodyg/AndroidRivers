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

package com.silverkeytech.android_rivers.asyncs

import android.content.Context
import android.os.AsyncTask
import com.silverkeytech.news_engine.riverjs.RiverItemMeta
import com.silverkeytech.news_engine.riverjs.sortRiverItemMeta
import org.holoeverywhere.app.Activity
import com.silverkeytech.android_rivers.activities.getMain
import com.silverkeytech.android_rivers.Result
import com.silverkeytech.android_rivers.InfinityProgressDialog
import com.silverkeytech.android_rivers.R
import com.silverkeytech.android_rivers.daysBeforeNow
import com.silverkeytech.android_rivers.PreferenceDefaults
import com.silverkeytech.android_rivers.toHoursInMinutes

public class DownloadUrlsAsRiverAsync(it: Context?, private val localUrl: String): AsyncTask<String, Int, Result<List<RiverItemMeta>>>(){
    class object {
        public val TAG: String = javaClass<DownloadUrlsAsRiverAsync>().getSimpleName()
    }

    var context: Activity = it!! as Activity
    var dialog: InfinityProgressDialog = InfinityProgressDialog(context, context.getString(R.string.please_wait_while_loading)!!)

    protected override fun onPreExecute() {
        dialog.onCancel {
            dlg ->
            dlg.dismiss()
            this@DownloadUrlsAsRiverAsync.cancel(true)
        }

        dialog.show()
    }


    protected override fun doInBackground(vararg p0: String?): Result<List<RiverItemMeta>>? {
        val latestDate = daysBeforeNow(PreferenceDefaults.CONTENT_BOOKMARK_COLLECTION_LATEST_DATE_FILTER_IN_DAYS)

        val maximumItemFilter = when(p0.size){
            in 0..5 -> 20
            in 6..12 -> 15
            in 12..20 -> 10
            else -> 6
        }

        val result = concurrentFeedsDownload(4, latestDate, maximumItemFilter, *p0)
        return result
    }

    var callback: ((String, Result<List<RiverItemMeta>>) -> Unit)? = null

    //Set up function to call when download is done
    public fun executeOnCompletion(action: ((String, Result<List<RiverItemMeta>>) -> Unit)?): DownloadUrlsAsRiverAsync {
        callback = action
        return this
    }

    protected override fun onPostExecute(result: Result<List<RiverItemMeta>>?) {
        dialog.dismiss()
        if (result != null){
            if (result.isTrue()){
                try{
                    val sortedNewsItems = sortRiverItemMeta(result.value!!)
                    context.getMain().setRiverCache(localUrl, sortedNewsItems, 3.toHoursInMinutes())
                    if (callback != null)
                        callback!!(localUrl, Result.right(sortedNewsItems))
                }
                catch (e: Exception){
                    if (callback != null)
                        callback!!(localUrl, Result.wrong<List<RiverItemMeta>>(result.exception))
                }
            }
            else
            {
                if (callback != null)
                    callback!!(localUrl, Result.wrong<List<RiverItemMeta>>(result.exception))
            }
        }
    }
}