package com.iitb.mobileict.lokavidya.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import android.os.Bundle;

import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import android.os.Handler;
import android.widget.Toast;


import com.iitb.mobileict.lokavidya.R;
import com.iitb.mobileict.lokavidya.util.Utilities;
import com.iitb.mobileict.lokavidya.util.ScalingUtilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Recording extends Activity implements SeekBar.OnSeekBarChangeListener{
    public String imagefileName,projectName;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    Button play, stop, record, retry,badButton, cropButton,fitButton;
    private SeekBar audioProgressBar;
    private ProgressBar recordProgressBar;
    private MediaRecorder myAudioRecorder;
    private MediaPlayer m;
    private String outputFile = null;
    boolean alreadyRecorded = false;
    boolean isRecording = false;
    boolean changed = false;
    int count=-1;
    File image_file;
    Bitmap scaledBitmap;


    /** Id of image resource to decode */
    private int mSourceId;

    /** Wanted width of decoded image */
    private int mDstWidth;

    /** Wanted height of decoded image */
    private int mDstHeight;

    /** Image view for presenting decoding result */
    private ImageView imageView;

    /** Text view for presenting decoding statistics */
    private TextView mResultView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPref.edit();
        songCurrentDurationLabel = (TextView) findViewById(R.id.current);
        songTotalDurationLabel = (TextView) findViewById(R.id.total);
        audioProgressBar = (SeekBar) findViewById(R.id.seekBar);
        recordProgressBar = (ProgressBar)findViewById(R.id.progBar);
        audioProgressBar.setOnSeekBarChangeListener(this);
        utils = new Utilities();
        Intent intent = getIntent();
        imagefileName = intent.getStringExtra("filename");
        projectName = intent.getStringExtra("projectname");

        badButton=(Button)findViewById(R.id.button_scaling_bad);
        fitButton=(Button)findViewById(R.id.button_scaling_fit);
        cropButton=(Button)findViewById(R.id.button_scaling_crop);
        mDstWidth = getResources().getDimensionPixelSize(R.dimen.destination_width);
        mDstHeight = getResources().getDimensionPixelSize(R.dimen.destination_height);

        count = sharedPref.getInt(projectName,0);
        if(count==0)
        {
            editor.putInt(projectName,0);
            editor.commit();
        }




        image_file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya"+"/"+projectName+"/images", imagefileName + ".png");


        Bitmap myBitmap = BitmapFactory.decodeFile(image_file.getAbsolutePath());
        imageView = (ImageView) findViewById(R.id.imagePlaying);
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
                audioProgressBar.setVisibility(View.GONE);
                recordProgressBar.setVisibility(View.VISIBLE);
                songCurrentDurationLabel.setVisibility(View.GONE);
                songTotalDurationLabel.setVisibility(View.GONE);
                try {
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                    isRecording = true;
                  /*  audioProgressBar.setProgress(0);
                    audioProgressBar.setMax(180000);
                    updateProgressBar();*/
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
                System.out.println("..........isPlaying"+m.isPlaying()+"...");
                if(m.isPlaying()){
                    m.stop();

                    audioProgressBar.setProgress(0);
                    stop.setEnabled(false);
                    play.setEnabled(true);
                    retry.setEnabled(true);
                    record.setEnabled(false);
                }
                else if (isRecording == true){

                    stop.setEnabled(false);
                    Handler delayHandler = new Handler();
                    delayHandler.postDelayed(new Runnable() {
                        public void run() {
                            myAudioRecorder.stop();
                            myAudioRecorder.release();
                            recordProgressBar.setVisibility(View.GONE);
                            audioProgressBar.setVisibility(View.VISIBLE);
                            songCurrentDurationLabel.setVisibility(View.VISIBLE);
                            songTotalDurationLabel.setVisibility(View.VISIBLE);
                            audioProgressBar.setProgress(0);
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
                            songTotalDurationLabel.setText(R.string.totallength);
                            songCurrentDurationLabel.setText(R.string.current);
                        }
                    }, 1000);

                }
                else{
                    stop.setEnabled(false);
                    play.setEnabled(true);
                    retry.setEnabled(true);
                    record.setEnabled(false);
                }

            }
        });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordProgressBar.setVisibility(View.VISIBLE);
                audioProgressBar.setVisibility(View.GONE);
                songCurrentDurationLabel.setVisibility(View.GONE);
                songTotalDurationLabel.setVisibility(View.GONE);
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
                songTotalDurationLabel.setText(R.string.totallength);
                songCurrentDurationLabel.setText(R.string.current);

            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {
                recordProgressBar.setVisibility(View.GONE);
                audioProgressBar.setVisibility(View.VISIBLE);
                songCurrentDurationLabel.setVisibility(View.VISIBLE);
                songTotalDurationLabel.setVisibility(View.VISIBLE);
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
    /**
     * Invoked when pressing button for showing result of the "Bad" decoding
     * method
     */
    public void badButtonPressed(View view) {

        // Part 1: Decode image
        Bitmap unscaledBitmap = BitmapFactory.decodeFile(image_file.getAbsolutePath());

        // Part 2: Scale image
        scaledBitmap = Bitmap
                .createScaledBitmap(unscaledBitmap, mDstWidth, mDstHeight, true);
        unscaledBitmap.recycle();

        imageView.setImageBitmap(scaledBitmap);
        changed = true;
    }
    /**
     * Invoked when pressing button for showing result of the "Fit" decoding
     * method
     */
    public void fitButtonPressed(View view) {

        // Part 1: Decode image
        Bitmap unscaledBitmap = ScalingUtilities.decodeResource(mDstWidth, mDstHeight, ScalingUtilities.ScalingLogic.FIT, image_file.getAbsolutePath());

        // Part 2: Scale image
        scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, mDstWidth,
                mDstHeight, ScalingUtilities.ScalingLogic.FIT);
        unscaledBitmap.recycle();

        // Calculate memory usage and performance statistics
        final int memUsageKb = (unscaledBitmap.getRowBytes() * unscaledBitmap.getHeight()) / 1024;
        final long stopTime = SystemClock.uptimeMillis();

        imageView.setImageBitmap(scaledBitmap);
        changed = true;
    }

    /**
     * Invoked when pressing button for showing result of the "Crop" decoding
     * method
     */
    public void cropButtonPressed(View view) {
        final long startTime = SystemClock.uptimeMillis();

        // Part 1: Decode image
        Bitmap unscaledBitmap = ScalingUtilities.decodeResource(mDstWidth, mDstHeight, ScalingUtilities.ScalingLogic.FIT, image_file.getAbsolutePath());

        // Part 2: Scale image
        scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, mDstWidth,
                mDstHeight, ScalingUtilities.ScalingLogic.CROP);
        unscaledBitmap.recycle();

        imageView.setImageBitmap(scaledBitmap);
        changed = true;
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
        if(fromTouch)
            m.seekTo(progress);
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
    public void onBackPressed(){
        m.stop();
        if(changed){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.save);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    File sdCard = Environment.getExternalStorageDirectory();
                    File imgDir = new File (sdCard.getAbsolutePath() + "/lokavidya"+"/"+projectName+"/images");
                    File writetofile = new File(imgDir, imagefileName+".png");
                    FileOutputStream outStream = null;
                    try {


                        outStream = new FileOutputStream(writetofile);
                        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                        outStream.flush();
                        outStream.close();


                    } catch (Exception e) {
                        Toast.makeText(Recording.this,"Cannot create new image : addimage",Toast.LENGTH_LONG);
                        e.printStackTrace();
                    } finally {
                        try {
                            if (outStream!= null) {
                                outStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    finish();

                   }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                }
            });

            builder.show();
        }else{
            finish();
        }

    }
}