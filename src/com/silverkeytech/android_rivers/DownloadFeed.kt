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

import android.os.AsyncTask
import android.content.Context
import com.silverkeytech.android_rivers.syndication.SyndicationFeed
import android.app.ProgressDialog
import android.app.Activity
import android.content.DialogInterface
import com.github.kevinsawicki.http.HttpRequest
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import com.silverkeytech.android_rivers.outliner.transformXmlToRss
import android.util.Log
import com.silverkeytech.android_rivers.outliner.transformXmlToAtom

public class DownloadFeed(it: Context?, ignoreCache: Boolean): AsyncTask<String, Int, Result<SyndicationFeed>>(){
    class object {
        public val TAG: String = javaClass<DownloadFeed>().getSimpleName()
    }

    var dialog: ProgressDialog = ProgressDialog(it)
    var context: Activity = it!! as Activity
    val ignoreCache: Boolean = ignoreCache

    protected override fun onPreExecute() {
        dialog.setMessage("Downloading RSS feed")
        dialog.setIndeterminate(true)
        dialog.setCancelable(false)
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", object : DialogInterface.OnClickListener{

            public override fun onClick(p0: DialogInterface?, p1: Int) {
                p0!!.dismiss()
                this@DownloadFeed.cancel(true)
            }
        })
        dialog.show()
    }

    protected override fun doInBackground(p0: Array<String?>): Result<SyndicationFeed>? {
        try{
            var downloadedContent: String?
            var mimeType: String?
            try{
                val request = HttpRequest.get(p0[0])
                mimeType = request?.contentType()

                downloadedContent = request?.body()
            }
            catch(e: HttpRequestException){
                var ex = e.getCause()
                return Result.wrong(ex)
            }

            Log.d(TAG, "Syndication XML is downloaded at ${p0[0]}")

            if (mimeType!!.contains("atom")){
                var feed = transformXmlToAtom(downloadedContent)

                Log.d(TAG, "Transforming XML to ATOM")

                if (feed.isTrue()){
                    var f = SyndicationFeed(null, feed.value)
                    f.transformRss()
                    return Result.right(f)
                } else{
                    return Result.wrong(feed.exception)
                }
            } else {
                var feed = transformXmlToRss(downloadedContent)

                Log.d(TAG, "Transforming XML to RSS")

                if (feed.isTrue()){
                    var f = SyndicationFeed(feed.value, null)
                    f.transformRss()
                    return Result.right(f)
                } else{
                    return Result.wrong(feed.exception)
                }
            }
        }
        catch (e : Exception){
            return Result.wrong(e)
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