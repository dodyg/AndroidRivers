package com.silverkeytech.android_rivers

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.util.Log
import com.silverkeytech.android_rivers.riverjs.RiverItem
import com.silverkeytech.android_rivers.riverjs.RiverItemMeta
import com.silverkeytech.android_rivers.syndications.SyndicationFeed
import com.silverkeytech.android_rivers.syndications.downloadSingleFeed
import java.util.ArrayList
import com.silverkeytech.android_rivers.riverjs.sortRiverItemMeta
import com.silverkeytech.android_rivers.syndications.SyndicationFilter

public class DownloadCollectionAsRiver(it: Context?, private val collectionId: Int): AsyncTask<String, Int, Result<List<RiverItemMeta>>>(){
    class object {
        public val TAG: String = javaClass<DownloadCollectionAsRiver>().getSimpleName()
    }

    var dialog: ProgressDialog = ProgressDialog(it)
    var context: Activity = it!! as Activity

    protected override fun onPreExecute() {
        dialog.setMessage(context.getString(R.string.please_wait_while_loading))
        dialog.setIndeterminate(true)
        dialog.setCancelable(false)
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(android.R.string.cancel), object : DialogInterface.OnClickListener{
            public override fun onClick(p0: DialogInterface?, p1: Int) {
                p0!!.dismiss()
                this@DownloadCollectionAsRiver.cancel(true)
            }
        })

        dialog.show()
    }

    protected override fun doInBackground(vararg p0: String?): Result<List<RiverItemMeta>>? {
        val latestDate = daysBeforeNow(1)

        var list = arrayListOf<RiverItemMeta>()
        for(val url in p0){
            val res = downloadSingleFeed(url!!, SyndicationFilter(20, latestDate))

            if (res.isFalse())
                Log.d(TAG, "Value at ${res.exception?.getMessage()}")
            else
            {
                Log.d(TAG, "Download for $url is successful")
                accumulateList(list, res.value!!)
            }
        }

        return Result.right(list)
    }

    fun accumulateList(list: ArrayList<RiverItemMeta>, feed: SyndicationFeed) {
        for(val f in feed.items.iterator()){
            val item = RiverItem()

            item.title = f.title
            item.body = f.description
            item.pubDate = DateHelper.formatRFC822(f.pubDate!!)

            if (f.hasLink())
                item.link = f.link

            val link = if (feed.hasLink())
                feed.link else ""

            val meta = RiverItemMeta(item, feed.title, link)

            list.add(meta)
        }
    }

    var callback: ((String, Result<List<RiverItemMeta>>) -> Unit)? = null

    //Set up function to call when download is done
    public fun executeOnCompletion(action: ((String, Result<List<RiverItemMeta>>) -> Unit)?): DownloadCollectionAsRiver {
        callback = action
        return this
    }

    protected override fun onPostExecute(result: Result<List<RiverItemMeta>>?) {
        dialog.dismiss()
        val url = makeLocalUrl(collectionId)
        if (result != null){
            if (result.isTrue()){
                try{
                    Log.d(TAG, "Total of items before sorting is ${result.value!!.size()}")

                    val sortedNewsItems = sortRiverItemMeta(result.value!!)
                    Log.d(TAG, "Total of sorted items is ${sortedNewsItems.size()}")
                    context.getApplication().getMain().setRiverCache(url, sortedNewsItems, 3.toHoursInMinutes())
                    if (callback != null)
                        callback!!(url, Result.right(sortedNewsItems))
                }
                catch (e : Exception){
                    if (callback != null)
                        callback!!(url, Result.wrong<List<RiverItemMeta>>(result.exception))
                }
            }
            else
            {
                if (callback != null)
                    callback!!(url, Result.wrong<List<RiverItemMeta>>(result.exception))
            }
        }
    }
}