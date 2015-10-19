package com.iitb.mobileict.lokavidya.ui;

import android.app.Activity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;

import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


import com.iitb.mobileict.lokavidya.R;
import com.iitb.mobileict.lokavidya.util.Utilities;

import java.io.File;
import java.io.IOException;



public class ViewVideo extends Activity implements SeekBar.OnSeekBarChangeListener{
    public String projectName;
    Button start, stop, pause;
    private SeekBar audioProgressBar;

    private MediaPlayer m;
    private String outputFile = null;

    int sequence=0;

    String imageFilePath,imageName;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private boolean paused = false;
    boolean check =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video);
        songCurrentDurationLabel = (TextView) findViewById(R.id.current);
        songTotalDurationLabel = (TextView) findViewById(R.id.total);
        audioProgressBar = (SeekBar) findViewById(R.id.seekBar);
        audioProgressBar.setOnSeekBarChangeListener(this);
        utils = new Utilities();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPref.edit();

        editor.putInt("savedView",1);
        editor.commit();

        projectName= sharedPref.getString("projectname","");



        start = (Button) findViewById(R.id.startPlaying);
        stop = (Button) findViewById(R.id.stopPlaying);
        pause = (Button) findViewById(R.id.pausePlaying);

        start.setEnabled(true);
        stop.setEnabled(false);
        pause.setEnabled(false);

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                m.stop();
                sequence = 0;
                paused = false;
                check=false;
                start.setEnabled(true);
                pause.setEnabled(false);
                stop.setEnabled(false);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    m.pause();
                    paused = true;
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                start.setEnabled(true);
                pause.setEnabled(false);
                stop.setEnabled(true);
            }
        });

        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {
                check=true;
                if(paused == true){
                    m.seekTo(m.getCurrentPosition());
                    m.start();
                    paused = false;
                    start.setEnabled(false);
                    pause.setEnabled(true);
                    stop.setEnabled(true);
                    return;
                }
                m = new MediaPlayer();
                m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        sequence++;

                        if(!(sequence==sharedPref.getInt(projectName,0))){
                            start.performClick();
                        }
                        else{
                            sequence = 0;
                            start.setEnabled(true);
                            pause.setEnabled(false);
                            stop.setEnabled(false);
                        }
                    }
                });
                try {
                    imageFilePath=sharedPref.getString(projectName+"image_path"+(sequence+1),"");
                    imageName = sharedPref.getString(projectName+"image_name"+(sequence+1),"");
                    imageName=imageName.replace(".png","");

                    Bitmap myBitmap = BitmapFactory.decodeFile(imageFilePath);
                    ImageView imageView = (ImageView) findViewById(R.id.imagePlaying);
                    imageView.setImageBitmap(myBitmap);

                   outputFile= Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya"+"/"+projectName+"/audio/"+ imageName + ".wav";

                    if(new File(outputFile).exists()){

                    }


                    m.setDataSource(outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    m.prepare();
                    m.start();
                    audioProgressBar.setProgress(0);
                    audioProgressBar.setMax(100);
                    updateProgressBar();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                start.setEnabled(false);
                pause.setEnabled(true);
                stop.setEnabled(true);

            }
        });



    }




    private Handler mHandler = new Handler();
    private TextView songTotalDurationLabel;
    private TextView songCurrentDurationLabel;
    private Utilities utils;


    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }


    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = m.getDuration();
            long currentDuration = m.getCurrentPosition();

            songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));

            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));


            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));

            audioProgressBar.setProgress(progress);

            mHandler.postDelayed(this, 100);
        }
    };


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = m.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);


        m.seekTo(currentPosition);


        updateProgressBar();
    }
    @Override
    public void onBackPressed()
    {
        if(check) {
            m.stop();
//            m.release();
        }else{//do nothing
         }


        finish();
    }
}