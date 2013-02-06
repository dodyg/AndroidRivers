package com.silverkeytech.android_rivers


import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.actionbarsherlock.app.SherlockActivity
import com.actionbarsherlock.view.ActionMode
import com.actionbarsherlock.view.Menu
import com.actionbarsherlock.view.MenuItem
import java.util.ArrayList
import com.actionbarsherlock.view.Window
import android.webkit.WebView
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.content.Intent


public class WebViewActivity:  SherlockActivity() {
    class object {
        public val TAG: String = javaClass<WebViewActivity>().getSimpleName()
    }

    var uri : String? = null
    var web : WebView? = null
    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        setTheme(this.getVisualPref().getTheme())

        web = WebView(getApplicationContext())
        requestWindowFeature(Window.FEATURE_PROGRESS)
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
        setSupportProgressBarVisibility(true)
        setSupportProgressBarIndeterminateVisibility(true)

        web!!.getSettings()!!.setJavaScriptEnabled(true)

        web!!.setWebChromeClient(object: WebChromeClient(){
            public override fun onProgressChanged(view: WebView?, newProgress: Int) {
                this@WebViewActivity.setSupportProgress(newProgress * 100)
                if (newProgress == 100){
                    setSupportProgressBarIndeterminateVisibility(false)
                    setProgressBarVisibility(false)
                }
            }
        })

        web!!.setWebViewClient(object : WebViewClient(){
            public override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                this@WebViewActivity.toastee("Sorry, I have problem loading this web page")
            }
        })

        super<SherlockActivity>.onCreate(savedInstanceState)
        setContentView(web!!)

        val actionBar = getSupportActionBar()!!
        actionBar.setDisplayShowHomeEnabled(false) //hide the app icon.


        val i = getIntent()!!
        uri = i.getStringExtra(Params.BUILT_IN_BROWSER_URI)!!
        web!!.loadUrl(uri!!)
    }


    var isPaused : Boolean = false

    //ref http://www.anddev.org/other-coding-problems-f5/webviewcorethread-problem-t10234.html
    public override fun onPause(){
        if (!isPaused){
            val pause = javaClass<WebView>().getMethod("onPause")
            pause.invoke(web)
            web!!.pauseTimers()
        }
        super<SherlockActivity>.onPause()
    }

    public override fun onResume(){
        if (isPaused){
            val resume = javaClass<WebView>().getMethod("onResume")
            resume.invoke(web)
            web!!.resumeTimers()
            isPaused = false
        }
        super<SherlockActivity>.onResume()
    }
}