package com.iitb.mobileict.lokavidya.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.preference.PreferenceManager;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import android.os.Handler;


import com.iitb.mobileict.lokavidya.R;
import com.iitb.mobileict.lokavidya.util.Utilities;

import java.io.File;
import java.io.IOException;


public class Recording extends Activity implements SeekBar.OnSeekBarChangeListener{
    public String imagefileName,projectName;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    Button play, stop, record, retry, pause;
    private SeekBar audioProgressBar;
    private MediaRecorder myAudioRecorder;
    private MediaPlayer m;
    private String outputFile = null;
    boolean alreadyRecorded = false;
    boolean isRecording = false;
    int count=-1;
    File image_file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPref.edit();
        songCurrentDurationLabel = (TextView) findViewById(R.id.current);
        songTotalDurationLabel = (TextView) findViewById(R.id.total);
        audioProgressBar = (SeekBar) findViewById(R.id.seekBar);
        audioProgressBar.setOnSeekBarChangeListener(this);
        utils = new Utilities();
        Intent intent = getIntent();
        imagefileName = intent.getStringExtra("filename");
        projectName = intent.getStringExtra("projectname");

        count = sharedPref.getInt(projectName,0);
        if(count==0)
        {
            editor.putInt(projectName,0);
            editor.commit();
        }




        image_file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya"+"/"+projectName+"/images", imagefileName + ".png");


        Bitmap myBitmap = BitmapFactory.decodeFile(image_file.getAbsolutePath());
        ImageView imageView = (ImageView) findViewById(R.id.imagePlaying);
        imageView.setImageBitmap(myBitmap);

        play = (Button) findViewById(R.id.pausePlaying);
        stop = (Button) findViewById(R.id.stopPlaying);
        record = (Button) findViewById(R.id.startPlaying);
        retry = (Button) findViewById(R.id.retryRecording);

        stop.setEnabled(false);
        play.setEnabled(false);
        retry.setEnabled(false);

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya"+"/"+projectName+"/audio/"+ imagefileName + ".wav";



        if(new File(outputFile).exists()){
            alreadyRecorded = true;
            play.setEnabled(true);
            retry.setEnabled(true);
            record.setEnabled(false);
        }

        m = new MediaPlayer();
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                    isRecording = true;
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                play.setEnabled(false);
                record.setEnabled(false);
                stop.setEnabled(true);
                retry.setEnabled(false);

            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m.isPlaying()){
                    m.stop();
                    stop.setEnabled(false);
                    play.setEnabled(true);
                    retry.setEnabled(true);
                    record.setEnabled(false);
                }
                else if (isRecording == true){
                    Handler delayHandler = new Handler();
                    delayHandler.postDelayed(new Runnable() {
                        public void run() {
                            myAudioRecorder.stop();
                            myAudioRecorder.release();
                            myAudioRecorder = null;
                            isRecording = false;

                            boolean flag=false;


                            editor.putInt("savedView",0);
                            editor.commit();

                            for(int i=0;i<sharedPref.getInt(projectName,0);i++)
                            {
                                if((imagefileName+".png").equals(sharedPref.getString(projectName + "image_name" + (i+1),"")))
                                {
                                    flag=true;
                                    break;
                                }

                            }

                            if(!flag) {
                                count++;
                                editor.putInt(projectName, count);
                                editor.putString(projectName + "image_name" + count, imagefileName + ".png");
                                editor.putString(projectName + "image_path" + count, image_file.getAbsolutePath());

                                editor.commit();

                            }

                            stop.setEnabled(false);
                            play.setEnabled(true);
                            retry.setEnabled(true);
                            record.setEnabled(false);
                        }
                    }, 1000);

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try{
//                                Thread.sleep(5000);
//                            }
//                            catch (Exception e){
//                                e.printStackTrace();
//                            }
//                            stop.setEnabled(false);
//                            play.setEnabled(true);
//                            retry.setEnabled(true);
//                            record.setEnabled(false);
//                        }
//                    })
                }

            }
        });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAudioRecorder = new MediaRecorder();
                myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                myAudioRecorder.setOutputFile(outputFile);
                try {
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                    isRecording = true;
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                record.setEnabled(false);
                stop.setEnabled(true);
                play.setEnabled(false);
                retry.setEnabled(false);

            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {
                m = new MediaPlayer();
                try {
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

                record.setEnabled(false);
                play.setEnabled(false);
                stop.setEnabled(true);
                retry.setEnabled(false);

            }
        });

           }




     private Handler mHandler = new Handler();
    private TextView songTotalDurationLabel;
    private TextView songCurrentDurationLabel;
    private Utilities utils;
    private int seekForwardTime = 5000;
    private int seekBackwardTime = 5000;


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
            if(!(m.isPlaying())){
                mHandler.removeCallbacks(mUpdateTimeTask);
                return ;
            }

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
}