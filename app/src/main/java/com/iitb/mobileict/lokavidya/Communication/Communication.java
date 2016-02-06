package com.iitb.mobileict.lokavidya.Communication;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;


/**
 * Created by Sanket Pimple on 15/11/15.
 * contains everything related to communication with the server and upload/download
 */
public class Communication extends BroadcastReceiver {

    private static String seedpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya/";
    public static boolean isDownloadComplete = false;
    public static boolean isSeedDownloadComplete = false;


    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("got here");
        isDownloadComplete = true;
        Log.i("Downloaded? onreceive", String.valueOf(isDownloadComplete));


    }


    // @SuppressLint("NewApi")
    public static void downloadSampleProjects(Context context, String link, String zipname) {
        isDownloadComplete = false;
        String DownloadUrl = link; //"http://ruralict.cse.iitb.ac.in/Downloads/lokavidyaProjects/odiyapump.zip";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DownloadUrl));
        request.setDescription("Downloading your project");   //appears the same in Notification bar while downloading
        //request.setTitle("Sample.pdf");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        //Uri downloadDestination= Uri.parse(seedpath+ "odiya-pump.zip");
        //request.setDestinationUri(downloadDestination);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, zipname);
        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        manager.enqueue(request);


        //Log.i("download seed", "download complete in " + seedpath);


    }

    public static void downloadseedList(Context context) {
        isDownloadComplete = false;
        String DownloadUrl = "http://ruralict.cse.iitb.ac.in/Downloads/lokavidyaProjects/project.txt";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DownloadUrl));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            //  request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "projects.txt");
        DownloadManager manager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        manager.enqueue(request);


    }


    public static void downloadBrowseVideo(Context context, String link, String vidName,String destinationName,String broadcastName) {
        isDownloadComplete = false;
        String DownloadUrl = link;

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DownloadUrl));
        request.setDescription("Downloading your video");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        //Uri downloadDestination= Uri.parse(seedpath+ "odiya-pump.zip");
        //request.setDestinationUri(downloadDestination);
        String destination= destinationName;
        File destfile= new File(destination);
        if(!destfile.exists()){
            destfile.mkdir();
        }
        request.setDestinationInExternalPublicDir(destination, vidName);
        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        manager.enqueue(request);




    }


}







