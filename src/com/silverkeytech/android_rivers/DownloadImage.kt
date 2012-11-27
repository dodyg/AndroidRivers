package com.silverkeytech.android_rivers

import android.os.AsyncTask
import android.content.Context
import android.app.ProgressDialog
import android.app.Activity
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import android.os.Environment
import com.github.kevinsawicki.http.HttpRequest
import java.io.File
import android.util.Log
import android.app.AlertDialog
import android.content.DialogInterface
import android.widget.ImageView
import android.graphics.BitmapFactory
import android.graphics.Bitmap

public class DownloadImage(it : Context?) : AsyncTask<String, Int, Result<String>>(){

    class object {
        public val TAG: String = javaClass<DownloadImage>().getSimpleName()!!
    }

    var dialog : ProgressDialog = ProgressDialog(it)
    var context : Activity = it!! as Activity

    protected override fun onPreExecute() {
        super<AsyncTask>.onPreExecute()

        dialog.setMessage(context.getString(R.string.please_wait_while_loading))
        dialog.setIndeterminate(true)
        dialog.setCancelable(false)
        dialog.show()
    }

    protected override fun doInBackground(p0: Array<String?>): Result<String>? {
        try{

            var dir = context.getCacheDir()!!.getPath()
            var fileName = dir + "/" + "awesome.jpg"
            Log.d(TAG, "File to be saved at ${fileName}")
            var output = File(fileName)
            HttpRequest.get(p0[0])!!.receive(output)

            return Result.right(fileName)
        }catch(e : HttpRequestException){
            return Result.wrong(e)
        }
    }

    protected override fun onPostExecute(result: Result<String>?) {
        super<AsyncTask>.onPostExecute(result)

        dialog.dismiss()

        if (result == null){
            context.toastee("Download operation is cancelled", Duration.LONG)
        }
        else{
            if (result.isTrue()){
                context.toastee("The file is ready for download ${result.value}", Duration.LONG)

                var dialog = AlertDialog.Builder(context)
                var inflater = context.getLayoutInflater()!!
                var vw = inflater.inflate(R.layout.image_view, null)!!

                var img : File = File(result.value!!)

                if (!img.exists()){
                    context.toastee("Sorry, the image ${result.value} does not exist", Duration.AVERAGE)
                    return
                }

                var image = vw.findViewById(R.id.image_view_main_iv) as ImageView

                var bmp : Bitmap?
                try{
                    bmp = BitmapFactory.decodeFile(img.getAbsolutePath())
                    image.setImageBitmap(bmp)
                }
                catch(e : Exception){
                    context.toastee("Sorry, I have problem in loading image ${result.value}", Duration.LONG)
                    return
                }

                dialog.setView(vw)
                dialog.setNeutralButton(android.R.string.ok, object : DialogInterface.OnClickListener{
                    public override fun onClick(p0: DialogInterface?, p1: Int) {
                        if (bmp != null)  //toss the bitmap once it's not needed
                            bmp!!.recycle()

                        p0?.dismiss()
                    }
                })

                dialog.show()

            } else if (result.isFalse()){
                context.toastee("Download is wrong ${result.exception!!.getMessage()}", Duration.LONG)
            }
        }
    }
}