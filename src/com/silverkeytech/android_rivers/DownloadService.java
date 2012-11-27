package com.silverkeytech.android_rivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends Service {

    private static final String TAG = DownloadService.class.getSimpleName();

    SharedPreferences preferences;

    private static final String DOCUMENT_VIEW_STATE_PREFERENCES = "DjvuDocumentViewState";

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private NotificationManager mNM;
    String downloadUrl;
    String fileName;
    int targetFileSize;

    public static boolean serviceState = false;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            downloadFile();
            showNotification("Downloading", "VVS");
            stopSelf(msg.arg1);
        }
    }


    @Override
    public void onCreate() {
        serviceState = true;
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        HandlerThread thread = new HandlerThread("ServiceStartArguments", 1);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

    }

    public void downloadFile() {
        downloadFile(this.downloadUrl, fileName);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "SERVICE-ONCOMMAND - onStartCommand");

        Bundle extra = intent.getExtras();
        if (extra != null) {
            String downloadUrl = extra.getString("downloadUrl");
            Log.d(TAG, "DOWNLOAD URL FROM INTENT URL " + downloadUrl);
            this.downloadUrl = downloadUrl;

            String filename = extra.getString("filename");
            Log.d(TAG, "Target name " + filename);
            this.fileName = filename;

            int targetFile = extra.getInt("contentLength");
            Log.d(TAG,  "Target content size " + targetFile);
            this.targetFileSize = targetFile;
        }

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "SERVICE-DESTROY - DESTORY");
        serviceState = false;
        //Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    void showNotification(String message, String title) {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = message;

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(android.R.drawable.gallery_thumb, "vvs",
                System.currentTimeMillis());

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this.getBaseContext(), 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, title,
                text, contentIntent);
        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        mNM.notify(R.string.app_name, notification);
    }

    public void downloadFile(String fileURL, String fileName) {

        StatFs stat_fs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double avail_sd_space = (double) stat_fs.getAvailableBlocks() * (double) stat_fs.getBlockSize();
        //double GB_Available = (avail_sd_space / 1073741824);
        double MB_Available = (avail_sd_space / 10485783);
        //System.out.println("Available MB : " + MB_Available);

        Log.d(TAG, "available MB " + MB_Available);

        try {
            Log.d(TAG, "Before checking externalStorageDirectory");
            File root = new File(Environment.getExternalStorageDirectory() + "/dody");
            if (root.exists() && root.isDirectory()) {

            } else {
                root.mkdir();
            }

            Log.d(TAG, "CURRENT PATH " + root.getPath());
            Log.d(TAG, "Opening " + fileURL);


            URL u = new URL(fileURL);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            Log.d(TAG, "Before connecting");
            c.connect();

            int fileSize = c.getContentLength() / 1048576;
            Log.d(TAG, "FILESIZE " + fileSize);

            if (MB_Available <= fileSize) {
                this.showNotification("NOTIFICATION NO MEMORY", "NOTIFICATION ERROR");
                c.disconnect();
                return;
            }

            Log.d(TAG, "Before opening output stream");
            FileOutputStream f = new FileOutputStream(new File(root.getPath(), fileName));

            Log.d(TAG, "Before getting the remote input stream");
            InputStream in = c.getInputStream();


            byte[] buffer = new byte[1024];
            int len1 = 0;
            int loop = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
                loop += 1024;

                Float percentage = ((float) loop / targetFileSize) * 100;

                Log.d(TAG, "Progressing " + percentage + "% download " + loop + " with target " + this.targetFileSize);

            }
            f.close();

            Log.d(TAG, "Download complete");

        } catch (Exception e) {
            Log.d(TAG, "Downloader There is an exception : " + e.getMessage());
        }
    }
}