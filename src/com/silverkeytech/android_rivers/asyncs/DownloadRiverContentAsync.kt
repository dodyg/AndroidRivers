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
import android.util.Log
import com.silverkeytech.news_engine.riverjs.River
import org.holoeverywhere.app.Activity
import com.silverkeytech.android_rivers.activities.Duration
import com.silverkeytech.android_rivers.activities.toastee
import com.silverkeytech.android_rivers.activities.ConnectivityErrorMessage
import com.silverkeytech.android_rivers.activities.handleConnectivityError
import com.silverkeytech.android_rivers.Result
import com.silverkeytech.android_rivers.InfinityProgressDialog
import com.silverkeytech.android_rivers.R
import com.silverkeytech.android_rivers.downloadSingleRiver

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
//Responsible for handling a river js downloading and display in asynchronous way
public class DownloadRiverContentAsync(it: Context?, val language: String): AsyncTask<String, Int, Result<River>>(){
    companion object {
        public val TAG: String = javaClass<DownloadRiverContentAsync>().getSimpleName()
    }

    val context: Activity = it!! as Activity
    val dialog: InfinityProgressDialog = InfinityProgressDialog(context, context.getString(R.string.please_wait_while_loading))

    //Prepare stuff before execution
    protected override fun onPreExecute() {
        dialog.onCancel{
            dlg ->
            dlg.dismiss()
            this@DownloadRiverContentAsync.cancel(true)
        }

        dialog.show()
    }

    var url: String = ""

    var rawCallback: ((Result<River>, String) -> Unit)? = null

    public fun executeOnComplete(callback: (Result<River>, String) -> Unit): DownloadRiverContentAsync {
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
    protected override fun onPostExecute(result: Result<River>) {
        dialog.dismiss()
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
