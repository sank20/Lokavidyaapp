package com.iitb.mobileict.lokavidya.ffmpegwrapper;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.iitb.mobileict.lokavidya.Projectfile;
import com.iitb.mobileict.lokavidya.data.Video;
import com.iitb.mobileict.lokavidya.ui.EditProject;
import com.iitb.mobileict.lokavidya.util.DaggerDependencyModule;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.ObjectGraph;

/**
 * Created by sanket on 9/7/2015.
 */

public class FfmpegWrapper{
    private static final String TAG = FfmpegWrapper.class.getSimpleName();
    String projName;
    @Inject
    FFmpeg ffmpeg;
    String imgOut,videoOut,finalVideoOut;


    public FfmpegWrapper(Context context)
    {
        ButterKnife.inject((android.app.Activity) context);
        DaggerDependencyModule dm = new DaggerDependencyModule(context);
        ObjectGraph.create(dm).inject(context);
        ffmpeg=dm.provideFFmpeg();
        loadFFMpegBinary();
    }

    public Video stitch(ArrayList<String> imageUrls,ArrayList<String> audioUrls,String projectName) throws IOException {
        Log.v("imageURLS", imageUrls.toString());
        Log.v("audioURLS",audioUrls.toString());
        ArrayList<String> lastStitchCommand = new ArrayList<String>();
        projName=projectName;

        ArrayList<String> cmdArrayList= new ArrayList<String>();


        File sdCard = Environment.getExternalStorageDirectory();



        File tmpDir = new File (sdCard.getAbsolutePath() + "/lokavidya/"+projectName+"/tmp");

        imgOut=Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya"+"/"+projectName+"/tmp/out-";
        videoOut=Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya"+"/"+projectName+"/tmp/out";

        if(!tmpDir.exists()&&!tmpDir.isDirectory())
        {
            tmpDir.mkdirs();
        }
        else
        {
            deleteTmpProject(projectName);
            tmpDir.mkdirs();
        }

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageUrls.get(0), bitmapOptions);
        int width = bitmapOptions.outWidth;
        int height = bitmapOptions.outHeight;

        /// Command Generation
        if(imageUrls.size()==audioUrls.size())
        {
            for(int i=0;i<imageUrls.size()/2;i++)
            {
                File imageFile = new File(imageUrls.get(i));
//                String cmd= "-loop 1 -i "+imageUrls.get(i)+" -c:v libx264 -t 2 -pix_fmt yuv420p -vf scale=320:240 /storage/emulated/0/DstApp/tmp/out-"+i+".mp4";

//                String cmd= "-loop 1 -i '"+imageUrls.get(i)+"' -c:v libx264 -t 2 -pix_fmt yuv420p -vf scale=640:480 '"+imgOut+i+".mp4'";
                String cmd= "-loop 1 -i '"+imageUrls.get(i)+"' -c:v libx264 -t 2 -pix_fmt yuv420p -vf scale="+width+":"+height+" '"+imgOut+i+".mp4'";

                System.out.println("command to stich audio files:" + cmd);

                cmdArrayList.add(cmd);
            }
            for(int i=0;i<audioUrls.size()/2;i++)
            {
                String cmd="-i '"+audioUrls.get(i)+"' -i '"+imgOut+i+".mp4' -c:a copy -vcodec copy -strict -2 '"+videoOut+i+".mp4'";

                System.out.println("command to stich image files:"+cmd);

                cmdArrayList.add(cmd);
            }
            /////////////////
            try {
                FileOutputStream out = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya"+"/"+projectName+"/tmp/order.txt"));
                for(int i=0;i<imageUrls.size();i++) {
                    String data="file '"+videoOut+i+".mp4'\n";
                    out.write(data.getBytes());
                }
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /////////////////

            /*String cmd="";
            for(int i=0;i<audioUrls.size();i++)
            {
                cmd += "-i /storage/emulated/0/DstApp/tmp/out"+i+".mp4 ";
            }
            cmd+= "-filter_complex ";
            lastStitchCommand.addAll(Arrays.asList(cmd.split(" ")));
            lastStitchCommand.add(" \"[0:0] [0:1] [1:0] [1:1] concat=n=2:v=1:a=1 [v] [a] \"  -map \"[v]\"  -map \"[a]\" ");
            //lastStitchCommand.add("-map \"[v]\" ");
            //lastStitchCommand.add("\"[v]\"");
            //lastStitchCommand.add("-map \"[a]\"");
           // lastStitchCommand.add("\" [a] \"");
            lastStitchCommand.add("-strict");
            lastStitchCommand.add("-2");
            lastStitchCommand.add("/storage/emulated/0/DstApp/tmp/superfinal.mp4");
*/
            // cmd += " \" [0:0] [0:1] [1:0] [1:1] concat=n=2:v=1:a=1 [v] [a] \" -map \" [v] \" -map \" [a] \" -strict -2 superfinal.mp4";

            //cmdArrayList.add(cmd);
        }




        for (int i=0;i<cmdArrayList.size();i++) {
            //String cmd= "-loop 1 -i "+imageUrls.get(0)+" -c:v libx264 -t 2 -pix_fmt yuv420p -vf scale=320:240 /storage/emulated/0/DstApp/tmp/out-"+imageUrls.get(0)+".mp4";

            String cmd = cmdArrayList.get(i);

//            String str = "Location \"Welcome  to india\" Bangalore " +
//                    "Channai \"IT city\"  Mysore";

            List<String> command = new ArrayList<String>();
            Matcher m = Pattern.compile("([^']\\S*|'.+?')\\s*").matcher(cmd);
            while (m.find())
                command.add(m.group(1).replace("'","")); // Add .replace("\"", "") to remove surrounding quotes.


            System.out.println(command);
            String []commandArray = command.toArray(new String[command.size()]);
//            String[] command = cmd.split(" ");
            for(int z=0;z<commandArray.length;z++)
            {
                Log.e("wahji",commandArray[z]);
            }
            //  Log.v("Displaying list of commands", "Commands");
            for (String s : commandArray) {
                Log.v("Command", s);
            }
            if (commandArray.length != 0) {
                execFFmpegBinary(commandArray);
            } else {
                //Toast.makeText(, getString(R.string.empty_command_toast), Toast.LENGTH_LONG).show();
                Log.v("ho gya", "ho gya");
            }
        }
     /*   File[] listfiles = listFilesMatching(new File("/storage/emulated/0/DstApp/tmp"),"out/d/.mp4");
        ArrayList<File> fileList = new ArrayList<File>(Arrays.asList(listfiles));
        for(File f : fileList)
        {
            Log.e("Hello World", f.getName());
        }
*/
        String cmd="-f concat -i '" +Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya"+"/"+projectName+"/tmp/order.txt' -codec copy '"+Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya"+"/"+projectName+"/tmp/final.mp4'";

        List<String> command = new ArrayList<String>();
        Matcher m = Pattern.compile("([^']\\S*|'.+?')\\s*").matcher(cmd);
        while (m.find())
            command.add(m.group(1).replace("'","")); // Add .replace("\"", "") to remove surrounding quotes.

        System.out.println(command);
        String []commandArray = command.toArray(new String[command.size()]);
        //String[] command=cmd.split(" ");
        if (commandArray.length != 0) {
            execFFmpegBinary(commandArray);
        } else {
            //Toast.makeText(, getString(R.string.empty_command_toast), Toast.LENGTH_LONG).show();
            Log.v("ho gya final", "ho gya");
        }
        return null;
    }


    public void deleteTmpProject(CharSequence projectName)
    {
        List<String> myStringArray = new ArrayList<String>();
        File sdCard = Environment.getExternalStorageDirectory();

        //   System.out.println("external storage------------------->" + sdCard.getAbsolutePath());

        File prjDir = new File (sdCard.getAbsolutePath() + "/lokavidya/"+projectName+"/tmp");

        deleteFile(prjDir);





    }


    public  boolean deleteFile(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteFile(new File(file, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
            return file.delete();
        }
        return false;
    }


    public Video stitchOld(String folderPath)
    {
        /*
         * Read from the path
         */
        Log.d(TAG, "Reading the Folder");
        /*
         * Reading the file storing the information for storing data
         */



        return null;
    }



    public static File[] listFilesMatching(File root, String regex) {
        // System.out.println("************************************************************************************************");
        if(!root.isDirectory()) {
            throw new IllegalArgumentException(root+" is no directory.");
        }
        final Pattern p = Pattern.compile(regex); // careful: could also throw an exception!
        return root.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return p.matcher(file.getName()).matches();
            }
        });
    }

    private void loadFFMpegBinary() {
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    // showUnsupportedExceptionDialog();
                    Log.v("Unsupported", "Unsupported");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            //showUnsupportedExceptionDialog();
            Log.v("Unsupported","Unsupported");
        }
    }


    private void execFFmpegBinary(final String[] command) {


        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    Log.d(TAG, "FAILED with output : " + s);
                }

                @Override
                public void onSuccess(String s) {
                    Log.d(TAG, "SUCCESS with output : " + s);
                }

                @Override
                public void onProgress(String s) {
                    Log.d(TAG, "Started command : ffmpeg " + command);
                    Log.d(TAG, "progress : " + s);
                }

                @Override
                public void onStart() {
                    Log.d(TAG, "Started command : ffmpeg " + command);
                    ///progressDialog.setMessage("Processing...");
                    ///progressDialog.show();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Finished command : ffmpeg " + command);
                    //progressDialog.dismiss();
                   // Projectfile.deleteTempFiles(projName);
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }
}
