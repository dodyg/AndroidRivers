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
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import com.github.kevinsawicki.http.HttpRequest
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import java.io.File
import org.holoeverywhere.app.Activity
import org.holoeverywhere.app.AlertDialog
import com.silverkeytech.android_rivers.activities.Duration
import com.silverkeytech.android_rivers.activities.toastee
import com.silverkeytech.android_rivers.activities.ConnectivityErrorMessage
import com.silverkeytech.android_rivers.activities.handleConnectivityError
import com.silverkeytech.android_rivers.Result
import com.silverkeytech.android_rivers.InfinityProgressDialog
import com.silverkeytech.android_rivers.R
import com.silverkeytech.android_rivers.generateThrowawayName
import com.silverkeytech.android_rivers.imageMimeTypeToFileExtension
import com.silverkeytech.android_rivers.findView

public data class DownloadedFile(val contentType: String, val filePath: String)

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
public class DownloadImageAsync(it: Context?): AsyncTask<String, Int, Result<DownloadedFile>>(){
    companion object {
        public val TAG: String = javaClass<DownloadImageAsync>().getSimpleName()
    }

    var context: Activity = it!! as Activity
    var dialog: InfinityProgressDialog = InfinityProgressDialog(context, context.getString(R.string.please_wait_while_loading))

    protected override fun onPreExecute() {
        dialog.onCancel {
            dlg ->
            dlg.dismiss()
            this@DownloadImageAsync.cancel(true)
        }
        dialog.show()
    }

    protected override fun doInBackground(p0: Array<String?>): Result<DownloadedFile>? {
        try{
            val req = HttpRequest.get(p0[0])
            val mimeType = req?.contentType()
            val dir = context.getCacheDir()!!.getPath()
            val fileName = dir + "/" + generateThrowawayName() + imageMimeTypeToFileExtension(mimeType!!)

            Log.d(TAG, "File to be saved at ${fileName}")

            var output = File(fileName)
            req!!.receive(output)

            return Result.right(DownloadedFile(mimeType, fileName))
        }catch(e: HttpRequestException){
            return Result.wrong(e)
        }
    }

    protected override fun onPostExecute(result: Result<DownloadedFile>) {
        super<AsyncTask>.onPostExecute(result)

        dialog.dismiss()

        if (result.isTrue()){
            var dialog = AlertDialog.Builder(context)
            var vw = context.getLayoutInflater().inflate(R.layout.image_view, null)!!

            var img: File = File(result.value!!.filePath)

            if (!img.exists()){
                context.toastee("Sorry, the image ${result.value} does not exist", Duration.AVERAGE)
                return
            }

            var image = vw.findView<ImageView>(R.id.image_view_main_iv)

            var bmp: Bitmap?
            try{
                bmp = BitmapFactory.decodeFile(img.getAbsolutePath())
                image.setImageBitmap(bmp)
            }
            catch(e: Exception){
                context.toastee("Sorry, I have problem in loading image ${result.value}", Duration.LONG)
                return
            }

            dialog.setView(vw)
            dialog.setNeutralButton(android.R.string.ok, object : DialogInterface.OnClickListener{
                public override fun onClick(p0: DialogInterface, p1: Int) {
                    if (bmp != null)  //toss the bitmap once it's not needed
                        bmp!!.recycle()

                    p0.dismiss()
                }
            })

            dialog.show()

        } else if (result.isFalse()){
            val error = ConnectivityErrorMessage(
                    timeoutException = "Sorry, we cannot download this image. The feed site might be down.",
                    socketException = "Sorry, we cannot download this image. Please check your Internet connection, it might be down.",
                    otherException = "Sorry, we cannot download this image for the following technical reason : ${result.exception.toString()}"
            )
            context.handleConnectivityError(result.exception, error)
        }
    }
}