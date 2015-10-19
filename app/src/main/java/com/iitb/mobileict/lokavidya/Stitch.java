package com.iitb.mobileict.lokavidya;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.iitb.mobileict.lokavidya.ffmpegwrapper.FfmpegWrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Stitch extends Activity {


    //private static final String TAG = Stitch.class.getSimpleName();

    String imageName;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    public String projectName;
    private String outputFile = null;
    int count;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context=this;
        final ArrayList<String> imageUrls = new ArrayList<>();
        final ArrayList<String> audioUrls = new ArrayList<>();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPref.edit();
        editor.putInt("savedView",1);
        editor.commit();

        projectName= sharedPref.getString("projectname","");

       count = sharedPref.getInt(projectName, 0);

        for(int i=0;i<count;i++)
        {
            imageUrls.add(sharedPref.getString(projectName+"image_path" + (i+1),""));
            imageName = sharedPref.getString(projectName+"image_name"+(i+1),"");
            imageName=imageName.replace(".png", "");
            outputFile= Environment.getExternalStorageDirectory().getAbsolutePath()+ "/lokavidya"+"/"+projectName+"/audio/"+ imageName + ".wav";
            audioUrls.add(outputFile);
        }


        final ProgressDialog ringProgressDialog = ProgressDialog.show(this, getString(R.string.stitchingProcessTitle), getString(R.string.stitchingProcessMessage), true);

        ringProgressDialog.setCancelable(false);
        ringProgressDialog.setCanceledOnTouchOutside(false);

        new Thread(new Runnable() {

            @Override

            public void run() {

                try {
                    //Thread.sleep(5000);
                    // Here you should write your time consuming task...
                    try{

                        FfmpegWrapper makeVideo = new FfmpegWrapper(context);


                        makeVideo.stitch(imageUrls,audioUrls, projectName);
                        File sdCard = Environment.getExternalStorageDirectory();
                        File final_file = new File (sdCard.getAbsolutePath() + "/lokavidya/"+projectName+"/tmp/final.mp4");

                        while(!final_file.exists()){
                            Thread.sleep(1000);
                        }
                    }
                    catch (FileNotFoundException e) {

                        e.printStackTrace();
                    }

                } catch (Exception e) {

                }finally {
                   // Projectfile.deleteTempFiles(projectName);
                }



                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        setResult(0);
                        ringProgressDialog.dismiss();
                        finish();
                    }
                });
            }

        }).start();





    }
}