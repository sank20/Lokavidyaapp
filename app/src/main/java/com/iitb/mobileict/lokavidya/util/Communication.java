package com.iitb.mobileict.lokavidya.util;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.provider.SyncStateContract;

import com.iitb.mobileict.lokavidya.ui.Projects;

/**
 * Created by Sanket Pimple on 15/11/15.
 */
public class Communication extends BroadcastReceiver {

    private static String seedpath=Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya/";
    public static boolean DownloadComplete= false;


    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("got here");
        DownloadComplete=true;
        Log.i("Downloaded? onreceive",String.valueOf(DownloadComplete));



    }


   // @SuppressLint("NewApi")
    public static void downloadSampleProjects(Context context){
        String DownloadUrl = "http://ruralict.cse.iitb.ac.in/Downloads/lokavidyaProjects/odiyapump.zip";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DownloadUrl));
        request.setDescription("Downloading your project");   //appears the same in Notification bar while downloading
        //request.setTitle("Sample.pdf");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        Uri downloadDestination= Uri.parse(seedpath+ "odiya-pump.zip");
        //request.setDestinationUri(downloadDestination);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "odiyapump.zip");
        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        manager.enqueue(request);



        //Log.i("download seed", "download complete in " + seedpath);


    }

}
