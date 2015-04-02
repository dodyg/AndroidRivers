package com.silverkeytech.android_rivers.meta_weblog

import android.util.Log
import org.xmlrpc.android.XMLRPCClient

//reference implementation
//http://codex.wordpress.org/XML-RPC_MetaWeblog_API
public class Blog(val blogId: Int?, val server: String, val username: String, val password: String){
    companion object {
        public val TAG: String = javaClass<Blog>().getSimpleName()
    }

    public fun newPost(payload: PostPayload) {
        val rpc = XMLRPCClient(server, "", "")

        val res = rpc.call("metaWeblog.newPost", "blogid", username, password, payload.toMap(), true)
        Log.d(TAG, "Return content $res")

    }

    public fun editPost(postId: Int) {

    }

    public fun getPost(postId: Int) {

    }

    public fun newMediaObject() {

    }

    public fun getCategories() {

    }

    public fun getRecentPosts() {

    }
}