package com.iitb.mobileict.lokavidya;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.iitb.mobileict.lokavidya.data.Audio;
import com.iitb.mobileict.lokavidya.data.Image;
import com.iitb.mobileict.lokavidya.data.MElement;
import com.iitb.mobileict.lokavidya.data.enums.MElementType;
import com.iitb.mobileict.lokavidya.ffmpegwrapper.FfmpegWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * the stitching activity- totally java based, no xml used
 */
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

        getAudioImageURLs(imageUrls, audioUrls, PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
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

        ringProgressDialog.setCancelable(true);
        ringProgressDialog.setCanceledOnTouchOutside(false);

        new Thread(new Runnable() {

            @Override

            public void run() {

                try {
                    //Thread.sleep(5000);
                    // Here you should write your time consuming task..
                    persist(imageUrls,audioUrls,projectName);
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
                    e.printStackTrace();

                }finally {

                   //

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

    public static void getAudioImageURLs(ArrayList<String> imageUrls, ArrayList<String> audioUrls, SharedPreferences sharedPref) {

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("savedView", 1);
        editor.commit();

        String projectName= sharedPref.getString("projectname","");

        int count = sharedPref.getInt(projectName, 0);

        for(int i=0;i<count;i++)
        {
            imageUrls.add(sharedPref.getString(projectName+"image_path" + (i+1),""));
            String imageName = sharedPref.getString(projectName+"image_name"+(i+1),"");
            imageName=imageName.replace(".png", "");
            String outputFile= Environment.getExternalStorageDirectory().getAbsolutePath()+ "/lokavidya"+"/"+projectName+"/audio/"+ imageName + ".wav";
            audioUrls.add(outputFile);
        }
    }

    public static void persist(ArrayList<String> imageUrls, ArrayList<String> audioUrls, String projectName) {

        ArrayList<MElement> slideElements= new ArrayList<MElement>();
        File sdCard = Environment.getExternalStorageDirectory();
        File orderFile =  new File(sdCard.getAbsolutePath() + "/lokavidya/"+projectName+"/order.json");
        for(int i=0;i<imageUrls.size();i++)
        {
            MElement slideElement= new MElement();
            slideElement.setElementType(MElementType.SLIDE);
            slideElement.setAudio(new Audio(audioUrls.get(i)));
            slideElement.setImage(new Image(imageUrls.get(i)));
            slideElements.add(slideElement);
        }
        for(MElement slide:slideElements)
        {
            Log.d("Iterating",slide.getImage().getPath());
        }
        try {
            Writer writer = new PrintWriter(orderFile.getAbsoluteFile());
            Gson gson = new GsonBuilder().create();
            gson.toJson(slideElements, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<MElement> tempArrayList = null;
        Gson gson = new Gson();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(orderFile.getAbsolutePath()));
            tempArrayList = gson.fromJson(bufferedReader, new TypeToken<ArrayList<MElement>>() {
            }.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for(MElement slide:tempArrayList)
        {
            Log.d("After Iterating", slide.getImage().getPath());
        }
        Log.d("Stitch", "Created File Order.json");
    }



    @Override
    protected void onStart() {
        super.onStart();

        //Projectfile.deleteTempFiles(projectName);

        setVisible(true);

    }





}