package com.silverkeytech.android_rivers

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.github.kevinsawicki.http.HttpRequest
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import com.silverkeytech.android_rivers.outlines.Opml
import com.silverkeytech.android_rivers.outlines.Outline
import java.util.ArrayList
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister

public class DownloadSubscription(it: Context?, ignoreCache: Boolean): AsyncTask<String, Int, Result<Opml>>(){
    class object {
        public val TAG: String = javaClass<DownloadSubscription>().getSimpleName()
    }

    var dialog: ProgressDialog = ProgressDialog(it)
    var context: Activity = it!! as Activity
    val ignoreCache: Boolean = ignoreCache

    protected override fun onPreExecute() {
        dialog.setMessage(context.getString(R.string.please_wait_while_downloading_news_rivers_list))
        dialog.setIndeterminate(true)
        dialog.setCancelable(false)
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", object : DialogInterface.OnClickListener{

            public override fun onClick(p0: DialogInterface?, p1: Int) {
                p0!!.dismiss()
                this@DownloadSubscription.cancel(true)
            }
        })
        dialog.show()
    }

    protected override fun doInBackground(vararg url: String?): Result<Opml>? {
        try{
            var cache = context.getApplication().getMain().getSubscriptionListCache()

            if (cache != null && !ignoreCache){
                Log.d(TAG, "Cache is hit for subscription list")
                return Result.right(cache!!)
            }
            else {
                Log.d(TAG, "Cache is missed for subscription list")

                var req: String?
                try{
                    req = HttpRequest.get(url[0])?.body()
                }
                catch(e: HttpRequestException){
                    var ex = e.getCause()
                    return Result.wrong(ex)
                }

                var opml = transformFromXml(req)

                if(opml.isTrue()){
                    var ct = opml.value?.body?.outline?.count()

                    if (ct != null && ct!! > 0){
                        var cnt: Float = 1.toFloat();
                        var divisor = ct!!.toFloat()

                        while(cnt < ct!!){
                            var progress = Math.round((cnt / divisor) * 100.toFloat());
                            publishProgress(progress);
                            Thread.sleep(200)
                            cnt++
                        }
                    }

                    context.getApplication().getMain().setSubscriptionListCache(opml.value!!)
                }
                return opml
            }
        } catch(e: Exception){
            return Result.wrong(e)
        }
    }

    fun transformFromXml(xml: String?): Result<Opml> {
        var serial: Serializer = Persister()

        try{
            val opml: Opml? = serial.read(javaClass<Opml>(), xml)
            Log.d(TAG, "OPML ${opml?.head?.title} created on ${opml?.head?.getDateCreated()} and modified on ${opml?.head?.getDateModified()}")
            return Result.right(opml)
        }
        catch (e: Exception){
            Log.d(TAG, "Exception ${e.getMessage()}")
            return Result.wrong(e)
        }
    }

    protected override fun onProgressUpdate(vararg progress: Int?) {
        Log.d("DD", "Progressing...${progress[0]}")
    }

    protected override fun onPostExecute(result: Result<Opml>?) {
        dialog.dismiss()

        if (result == null){
            context.toastee("Sorry, we cannot handle this subscription download because operation is cancelled", Duration.AVERAGE)
        }
        else{
            if (result.isFalse()){
                var error = ConnectivityErrorMessage(
                        timeoutException = "Sorry, we cannot download this subscription list. The subscription site might be down",
                        socketException = "Sorry, we cannot download this subscription list. Please check your Internet connection, it might be down",
                        otherException = "Sorry, we cannot download this subscription list for the following technical reason : ${result.exception.toString()}"
                )

                context.handleConnectivityError(result.exception, error)

            } else {
                handleRiversListing(result.value!!)
            }
        }
    }

    public data class ViewHolder (var riverName: TextView)

    fun handleRiversListing(outlines: Opml) {
        var list = context.findView<ListView>(R.id.main_rivers_lv)

        var vals = ArrayList<Outline>()

        outlines.body?.outline?.forEach {
            vals.add(it!!)
        }

        var values = vals

        var adapter = object : ArrayAdapter<Outline>(context, android.R.layout.simple_list_item_1, android.R.id.text1, values){
            public override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                var vw = convertView
                var holder: ViewHolder?

                if (vw == null){
                    var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    vw = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)

                    holder = ViewHolder(vw!!.findViewById(android.R.id.text1) as TextView)
                    holder!!.riverName.setText(vals[position].toString())
                    vw!!.setTag(holder)
                }else{
                    holder = vw!!.getTag() as ViewHolder
                    holder!!.riverName.setText(vals[position].toString())
                    Log.d(TAG, "List View reused")
                }

                return vw
            }
        }

        list.setAdapter(adapter)

        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long) {
                var currentOutline = values.get(p2)

                var i = Intent(context, javaClass<RiverActivity>())
                i.putExtra(Params.RIVER_URL, currentOutline.url)
                i.putExtra(Params.RIVER_NAME, currentOutline.text)

                context.startActivity(i);
            }
        })
    }


}
