package com.silverkeytech.android_rivers

import android.content.Context
import android.app.ProgressDialog
import android.app.Activity
import android.os.AsyncTask
import com.github.kevinsawicki.http.HttpRequest
import android.util.Log
import android.widget.TextView
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import com.silverkeytech.android_rivers.outlines.Opml
import com.silverkeytech.android_rivers.outlines.Body
import android.widget.Adapter
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView
import android.view.Gravity
import android.view.View
import android.widget.Toast
import android.widget.ArrayAdapter
import android.widget.ListView
import com.silverkeytech.android_rivers.outlines.Outline
import android.content.Intent
import java.util.ArrayList
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException
import org.apache.http.conn.ConnectTimeoutException
import java.net.UnknownHostException

public class DownloadSubscription(it : Context?) : AsyncTask<String, Int, Result<Opml>>(){
    class object {
        public val TAG: String = javaClass<DownloadSubscription>().getSimpleName()!!
    }

    var dialog : ProgressDialog = ProgressDialog(it)
    var context : Activity = it!! as Activity

    protected override fun onPreExecute(){
        dialog.setMessage(context.getString(R.string.please_wait_while_downloading_news_rivers_list))
        dialog.setIndeterminate(true)
        dialog.setCancelable(false)
        dialog.show()
    }

    protected override fun doInBackground(vararg url : String?): Result<Opml>?{
        var req : String? = null
        try{
           req = HttpRequest.get(url[0])?.body()
       }
       catch(e : HttpRequestException){
           var ex = e.getCause()
           return Result.wrong(ex)
       }

        var opml = transformFromXml(req)

        if(opml.isTrue()){
            var ct = opml.value?.body?.outline?.count()

            if (ct != null && ct!! > 0){
                var cnt : Float = 1.toFloat();
                var divisor = ct!!.toFloat()

                while(cnt < ct!!){
                    var progress  = Math.round((cnt / divisor) * 100.toFloat());
                    publishProgress(progress);
                    Thread.sleep(200)
                    cnt++
                }
            }
        }
        return opml
    }

    fun transformFromXml(xml : String?) : Result<Opml>{
        var serial : Serializer = Persister()

        try{
            val opml : Opml? = serial.read(javaClass<Opml>(),xml)
            Log.d(TAG, "OPML ${opml?.head?.title} created on ${opml?.head?.getDateCreated()} and modified on ${opml?.head?.getDateModified()}")
            return Result.right(opml)
        }
        catch (e: Exception){
            Log.d(TAG, "Exception ${e.getMessage()}")
            return Result.wrong(e)
        }
    }

    protected override fun onProgressUpdate(vararg progress : Int?){
        Log.d("DD", "Progressing...${progress[0]}")
    }

    protected override fun onPostExecute(result : Result<Opml>?){
        dialog.dismiss()

        if (result == null){
            context.toastee("Sorry, we cannot handle this subscription download because operation is cancelled", Duration.AVERAGE)
        }
        else{
            if (result.isFalse()){
                if (result.exception is ConnectTimeoutException)
                    context.toastee("Sorry, we cannot download this subscription list. The subscription site might be down", Duration.AVERAGE)
                else if (result.exception is UnknownHostException)
                    context.toastee("Sorry, we cannot download this subscription list. Please check your Internet connection, it might be down", Duration.AVERAGE)
                else
                    context.toastee("Sorry, we cannot download this subscription list for the following technical reason : ${result.exception.toString()}", Duration.AVERAGE)
            } else {
                var msg = context.findView<TextView>(R.id.main_message_tv)
                handleRiversListing(result.value!!)
            }
        }
    }

    fun handleRiversListing(outlines : Opml){
        var list = context.findView<ListView>(R.id.main_rivers_lv)

        var vals = ArrayList<Outline>()

        outlines.body?.outline?.forEach {
            vals.add(it!!)
        }

        var values = vals

        var adapter = ArrayAdapter<Outline>(context, android.R.layout.simple_list_item_1, android.R.id.text1, values)
        list.setAdapter(adapter)

        list.setOnItemClickListener(object : OnItemClickListener{
            public override fun onItemClick(p0: AdapterView<out Adapter?>?, p1: View?, p2: Int, p3: Long) {
                var currentOutline = values.get(p2)

                var i = Intent(context, javaClass<RiverActivity>())
                i.putExtra(Params.RIVER_URL, currentOutline.url)
                i.putExtra(Params.RIVER_NAME, currentOutline.text)

                context.startActivity(i);

                /*
                var t = Toast.makeText(context, textToBeDisplayed, 300)
                t!!.setGravity(Gravity.TOP or Gravity.CENTER, 0, 0 );
                t!!.show()
                */
            }
        })
    }

}
