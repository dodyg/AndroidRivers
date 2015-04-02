package com.silverkeytech.android_rivers.activities

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.actionbarsherlock.view.Window
import org.holoeverywhere.app.Activity
import com.silverkeytech.android_rivers.Params

public class WebViewActivity: Activity() {
    companion object {
        public val TAG: String = javaClass<WebViewActivity>().getSimpleName()
    }

    var uri: String? = null
    var web: WebView? = null
    public override fun onCreate(savedInstanceState: Bundle?): Unit {
        //setTheme(this.getVisualPref().getTheme())

        web = WebView(getApplicationContext()!!)
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

        super<Activity>.onCreate(savedInstanceState)
        setContentView(web!!)

        val actionBar = getSupportActionBar()!!
        actionBar.setDisplayShowHomeEnabled(false) //hide the app icon.


        val i = getIntent()!!
        uri = i.getStringExtra(Params.BUILT_IN_BROWSER_URI)!!
        web!!.loadUrl(uri!!)
    }


    var isPaused: Boolean = false

    //ref http://www.anddev.org/other-coding-problems-f5/webviewcorethread-problem-t10234.html
    public override fun onPause() {
        if (!isPaused){
            val pause = javaClass<WebView>().getMethod("onPause")
            pause.invoke(web)
            web!!.pauseTimers()
        }
        super<Activity>.onPause()
    }

    public override fun onResume() {
        if (isPaused){
            val resume = javaClass<WebView>().getMethod("onResume")
            resume.invoke(web)
            web!!.resumeTimers()
            isPaused = false
        }
        super<Activity>.onResume()
    }
}