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
package com.silverkeytech.android_rivers

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.util.Log

public class MediaScannerWrapper (val context: Context, val filePath: String, val mimeType: String)
: MediaScannerConnection.MediaScannerConnectionClient{
    companion object {
        public val TAG: String = javaClass<MediaScannerWrapper>().getSimpleName()

        fun scanPodcasts(context: Context, filePath: String) {
            var scanner = MediaScannerWrapper(context, filePath, "audio/mpeg")
            scanner.scan()
        }
    }

    var connection: MediaScannerConnection
    {
        connection = MediaScannerConnection(context, this)
    }

    public fun scan() {
        connection.connect()
    }

    public override fun onMediaScannerConnected() {
        connection.scanFile(filePath, mimeType)
        Log.d(TAG, "Start scanning at ${filePath} for ${mimeType}")
    }

    public override fun onScanCompleted(p0: String?, p1: Uri?) {
        Log.d(TAG, "Done scanning at ${filePath} for ${mimeType}")
    }
}
