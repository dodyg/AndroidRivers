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
import com.silverkeytech.android_rivers.riverjs.FeedsRiver

//Responsible for handling a river js downloading and display in asynchronous way
public class DownloadRiverContent(it: Context?, ignoreCache: Boolean): AsyncTask<String, Int, Result<FeedsRiver>>(){
    class object {
        public val TAG: String = javaClass<DownloadRiverContent>().getSimpleName()
    }

    var dialog: ProgressDialog = ProgressDialog(it)
    var context: Activity = it!! as Activity
    val ignoreCache: Boolean = ignoreCache

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

    //Download river data in a thread
    protected override fun doInBackground(vararg p0: String?): Result<FeedsRiver>? {
        try{
            var url = p0.get(0)!!
            var before = System.currentTimeMillis()
            var cache = context.getApplication().getMain().getRiverCache(url)

            var now = System.currentTimeMillis()

            Log.d(TAG, "Cache retrieval took ${now - before} ms")

            if (cache != null && !ignoreCache){
                Log.d(TAG, "Cache is hit for url $url")
                return Result.right(cache!!)
            }
            else {
                Log.d(TAG, "Cache is missed for url $url")

                var req: String?
                try{
                    req = HttpRequest.get(url)?.body()
                }
                catch(e: HttpRequestException){
                    var ex = e.getCause()
                    return Result.wrong(ex)
                }

                try{
                    var gson = Gson()
                    var scrubbed = scrubJsonP(req!!)
                    var feeds = gson.fromJson(scrubbed, javaClass<FeedsRiver>())!!
                    context.getApplication().getMain().setRiverCache(url, feeds)

                    return Result.right(feeds)
                }
                catch(e: Exception)
                {
                    return Result.wrong(e)
                }
            }
        } catch(e: Exception)
        {
            return Result.wrong(e)
        }
    }

    //Once the thread is done.
    protected override fun onPostExecute(result: Result<FeedsRiver>?) {
        dialog.dismiss();
        if (result == null)
            context.toastee("Sorry, we cannot process this feed because the operation is cancelled", Duration.AVERAGE)
        else {
            if (result.isFalse()){
                var error = ConnectivityErrorMessage(
                        timeoutException = "Sorry, we cannot download this feed. The feed site might be down.",
                        socketException = "Sorry, we cannot download this feed. Please check your Internet connection, it might be down.",
                        otherException = "Sorry, we cannot download this feed for the following technical reason : ${result.exception.toString()}"
                )
                context.handleConnectivityError(result.exception, error)
            }else{
                RiverContentRenderer(context).handleNewsListing(result.value!!)
            }
        }
    }
}
