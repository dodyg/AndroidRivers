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

import android.os.AsyncTask
import org.holoeverywhere.app.Activity
import android.content.Context
import android.util.Log
import java.io.File
import com.silverkeytech.android_rivers.db.removePodcast
import com.silverkeytech.android_rivers.db.SortingOrder
import com.silverkeytech.android_rivers.db.getPodcastsFromDb
import com.silverkeytech.android_rivers.Result
import com.silverkeytech.android_rivers.InfinityProgressDialog
import com.silverkeytech.android_rivers.R

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
public class DeleteAllPodcastsAsync(it: Context?): AsyncTask<String, Int, Result<Int>>(){
    companion object {
        public val TAG: String = javaClass<DeleteAllPodcastsAsync>().getSimpleName()
    }

    val context: Activity = it!! as Activity
    val dialog: InfinityProgressDialog = InfinityProgressDialog(context, context.getString(R.string.deleting_all_podcasts))

    //Prepare stuff before execution
    protected override fun onPreExecute() {
        dialog.onCancel{
            dlg ->
            dlg.dismiss()
            this@DeleteAllPodcastsAsync.cancel(true)
        }

        dialog.show()
    }

    var rawCallback: ((Result<Int>) -> Unit)? = null

    public fun executeOnComplete(callback: (Result<Int>) -> Unit): DeleteAllPodcastsAsync {
        rawCallback = callback
        return this
    }

    //Download river data in a thread
    protected override fun doInBackground(vararg p0: String?): Result<Int>? {
        var deletedPodcasts = 0
        //get all active podcasts
        val podcasts = getPodcastsFromDb(SortingOrder.DESC)
        if (podcasts.size() == 0)
            return Result.right(deletedPodcasts)

        try{
            for(current in podcasts){
                var f = File(current.localPath)

                if (f.exists())
                    f.delete()

                val res = removePodcast(current.id)
                if (res.isFalse()){
                    Log.d(TAG, "Fail in deleting file ${f.name} ${res.exception?.getMessage()}")
                    break
                }
                else
                    deletedPodcasts++
            }
        }
        catch(e: Exception){
            Log.d(TAG, "Fail in trying to delete a file ${e.getMessage()}")
        }
        finally{
            return Result.right(deletedPodcasts)
        }
    }

    protected override fun onPostExecute(result: Result<Int>) {
        dialog.dismiss()

        if (rawCallback != null)
            rawCallback!!(result)
    }
}