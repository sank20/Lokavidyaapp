package com.iitb.mobileict.lokavidya;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.iitb.mobileict.lokavidya.ui.Projects;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static android.support.v4.app.ActivityCompat.startActivity;
import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by saikiran on 1/10/15.
 */
public class Share {
    public Context mContext;
    public static String projectname;

    public static void SendOptions(int option, Activity activity, Context mContext, String projectname) {
        Share.projectname = projectname;
        if(option ==1){
            Share.shareProject(activity, mContext);
            return;
        }
        if(option == 0){
            Share.shareVia(activity, mContext);
            return;
        }
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(btAdapter == null) {
            Toast.makeText(mContext,"Bluetooth is not supported on this device", Toast.LENGTH_LONG).show();
        } else {
            Share.enableBluetooth(activity, option);
        }
    }
    public static final int DISCOVER_DURATION = 300;
    public static final int REQUEST_BLU_VIDEO = 100;
    public static final int REQUEST_BLU_PROJECT= 200;
    public static final int REQUEST_BLU_VIA = 300;

    public static void enableBluetooth(Activity activity, int option) {
        System.out.println("----------------------> enableBluetooth() function called in : Share.java");
//        Activity activity = (Activity) mContext;
        System.out.println("---------------> Activity obtained : enableBluetooth : Share.java");
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
        if(option == 0)activity.startActivityForResult(discoveryIntent, REQUEST_BLU_VIDEO);
        if(option == 1)activity.startActivityForResult(discoveryIntent, REQUEST_BLU_PROJECT);
        if(option == 2)activity.startActivityForResult(discoveryIntent, REQUEST_BLU_VIA);
    }
    public static void  sendVideo(Activity activity,Context mContext){
//        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
//        Activity activity = (Activity) mContext;
        //   sai kiran and ravi
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("video/*");
            String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya" + "/" + Share.projectname + "/tmp/" +  "final.mp4";
            File f = new File(outputFile);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));

            PackageManager pm = activity.getPackageManager();
            List<ResolveInfo> appsList = pm.queryIntentActivities(intent, 0);

            if(appsList.size() > 0) {
                String packageName = null;
                String className = null;
                boolean found = false;

                for (ResolveInfo info : appsList) {
                    packageName = info.activityInfo.packageName;
                    if (packageName.equals("com.android.bluetooth") || packageName.equals("com.mediatek.bluetooth")) {
                        className = info.activityInfo.name;
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Toast.makeText(mContext, "Bluetooth hasn't been found",
                            Toast.LENGTH_LONG).show();
                } else {
                    intent.setClassName(packageName, className);
                    //System.out.println("-------------------Share:sendVideo:- starting the activity");
                    activity.startActivity(intent);
                }
            }
    }

    public static  void shareProject(final Activity activity, Context mContext) {
//        final ProgressDialog ringProgressDialog = ProgressDialog.show(mContext, mContext.getString(R.string.stitchingProcessTitle), mContext.getString(R.string.stitchingProcessMessage), true);
//        final ProgressDialog ringProgressDialog = new ProgressDialog(mContext);
//        ringProgressDialog.setMessage("loading");
//        ringProgressDialog.setCancelable(false);
//        ringProgressDialog.setCanceledOnTouchOutside(false);
//        ringProgressDialog.show();
        final ProgressDialog ringProgressDialog = ProgressDialog.show(activity, "EXPORT",
                "Generating .zip folder", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.setCanceledOnTouchOutside(false);

        new Thread(new Runnable() {
            @Override
            public void run()
            {
                String inputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya" + "/" + Share.projectname;
                String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya" + "/" + Share.projectname + ".zip";
                ZipFile zipFile = null;
                try {
                    zipFile = new ZipFile(outputFile);
                } catch (ZipException e) {
                    e.printStackTrace();
                }
                ZipParameters parameters = new ZipParameters();

                // COMP_DEFLATE is for compression
                // COMp_STORE no compression
                parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
                // DEFLATE_LEVEL_ULTRA = maximum compression
                // DEFLATE_LEVEL_MAXIMUM
                // DEFLATE_LEVEL_NORMAL = normal compression
                // DEFLATE_LEVEL_FAST
                // DEFLATE_LEVEL_FASTEST = fastest compression
                parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_FASTEST);
                File dir = new File(inputFile+"/tmp");
                if (dir.isDirectory())
                {
                    String[] children = dir.list();
                    for (int i = 0; i < children.length; i++)
                    {
                        new File(dir, children[i]).delete();
                    }
                }
                dir.delete();
                // file compressed
                try {
                    zipFile.addFolder(inputFile, parameters);
                } catch (ZipException e) {
                    e.printStackTrace();
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        ringProgressDialog.dismiss();
                    }
                });
            }
        }).start();


//        zipFolder(outputFile + "/images", outputFile + "/images.zip");
//        zipFolder(outputFile + "/audio", outputFile + "/audio.zip");
//        zipFolder(outputFile, outputFile + ".zip");
//        File delete_file = new File(outputFile + "/images.zip");
//        delete_file.delete();
//        delete_file = new File(outputFile + "/audio.zip");
//        delete_file.delete();
        Intent i = new Intent(Intent.ACTION_SEND);
        String inputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya" + "/" + Share.projectname;
        String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya" + "/" + Share.projectname + ".zip";
        i.setType("application/zip");
        File f = new File(outputFile);
        i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
        activity.startActivity(Intent.createChooser(i, "Share Project Via"));
}
    public static void shareVia(Activity activity, Context mContext){
        String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya" + "/" + Share.projectname + "/tmp/" +  "final.mp4";
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("video/*");
        File f = new File(outputFile);
        i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
        activity.startActivity(Intent.createChooser(i, "Share Video Via"));
    }
    private static void zipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            Log.d("", "Zip directory: " + srcFile.getName());
            for (int i = 0; i < files.length; i++) {
                if(files[i].isDirectory())continue;
                Log.d("", "Adding file: " + files[i].getName());
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(files[i]);
                zos.putNextEntry(new ZipEntry(files[i].getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
        } catch (IOException ioe) {
            Log.e("", ioe.getMessage());
        }
    }

    public static String pathToProjectname(String path){
        int j = path.length() - 5;
        for(int i=path.length()-5;i>=0;i--){
            if(path.charAt(i) == '/'){
                j = i;break;
            }
        }
        return path.substring(j+1,path.length() - 4);
    }

    public static void importproject(final Uri uri,final Activity activity,final Context context){
        final ProgressDialog ringProgressDialog = ProgressDialog.show(activity, "EXTRACTING...",
                "extracting .zip to your app", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.setCanceledOnTouchOutside(false);
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya";
//        System.out.println("----------------Share.java: import project: >" + outputFile);
//        System.out.println(Share.unpackZip(path, outputFile));
                //path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/empty.zip";
                //System.out.println(path);
                //String proj = importProjectName;
                //_dirChecker(outputFile + "/", proj);
                //String path1= outputFile +"/" + proj + "/";
                String path1= outputFile +"/";
                Share.unzipuri(uri, path1,context); // check for / at the end of path1
                Intent intent = activity.getIntent();
                activity.finish();
                activity.startActivity(intent);
//                _dirChecker(path1, "images");
//                _dirChecker(path1, "audio");
//                Share.unzip(path1 + "images.zip", path1 + "images/");
//                Share.unzip(path1 + "audio.zip", path1 + "audio/");
//                System.out.println("---------------->FInished extraction calls");
//                File delete_file = new File(path1 + "images.zip");delete_file.delete();
//                delete_file = new File(path1 + "audio.zip");delete_file.delete();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        ringProgressDialog.dismiss();
                    }
                });
            }
        }).start();
    }

//    public static boolean unpackZip(String input, String output)
//    {
//        InputStream is;
//        ZipInputStream zis;
//        try
//        {
//            is = new FileInputStream(input);
//            zis = new ZipInputStream(new BufferedInputStream(is));
//            ZipEntry ze;
//
//            while((ze = zis.getNextEntry()) != null)
//            {
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                byte[] buffer = new byte[1024];
//                int count;
//
//                String filename = ze.getName();
//                FileOutputStream fout = new FileOutputStream(output);
//
//                // reading and writing
//                while((count = zis.read(buffer)) != -1)
//                {
//                    baos.write(buffer, 0, count);
//                    byte[] bytes = baos.toByteArray();
//                    fout.write(bytes);
//                    baos.reset();
//                }
//
//                fout.close();
//                zis.closeEntry();
//            }
//
//            zis.close();
//        }
//        catch(IOException e)
//        {
//            e.printStackTrace();
//            return false;
//        }
//
//        return true;
//    }
    public static void unzipuri(Uri uri, String _location,Context context) {
        try  {
            InputStream fin=context.getContentResolver().openInputStream(uri);
            //FileInputStream fin = new
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                Log.v("Decompress", "Unzipping " + ze.getName());

                if(ze.isDirectory()) {
                    Share._dirChecker(_location, ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(_location + ze.getName());
                    System.out.println("-*---------->EXTRACT TO THIS DIR :" + _location + " " + ze.getName());
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                    }

                    zin.closeEntry();
                    fout.close();
                }

            }
            zin.close();
        } catch(Exception e) {
            Log.e("Decompress", "unzip", e);
        }
    }

    public static void unzip(String _zipfile, String _location) {
        try  {
            FileInputStream fin = new FileInputStream(_zipfile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                Log.v("Decompress", "Unzipping " + ze.getName());

                if(ze.isDirectory()) {
                    Share._dirChecker(_location, ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(_location + ze.getName());
                    System.out.println("-*---------->EXTRACT TO THIS DIR :" + _location + " " + ze.getName());
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                    }

                    zin.closeEntry();
                    fout.close();
                }

            }
            zin.close();
        } catch(Exception e) {
            Log.e("Decompress", "unzip", e);
        }

    }

    public static void _dirChecker(String _location, String dir) {
        File f = new File(_location + dir);

        if(!f.isDirectory()) {
            f.mkdirs();
        }
    }
}
