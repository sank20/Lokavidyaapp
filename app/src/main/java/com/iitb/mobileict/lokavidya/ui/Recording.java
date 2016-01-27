package com.iitb.mobileict.lokavidya.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import android.os.Bundle;

import android.provider.MediaStore;
import android.support.v7.widget.PopupMenu;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import android.os.Handler;
import android.widget.Toast;


import com.iitb.mobileict.lokavidya.Projectfile;
import com.iitb.mobileict.lokavidya.R;
import com.iitb.mobileict.lokavidya.util.Utilities;
import com.iitb.mobileict.lokavidya.util.ScalingUtilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;


/**
 * implementation of the recording activity
 */
public class Recording extends Activity implements SeekBar.OnSeekBarChangeListener{
    public String imagefileName,projectName;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    Button play, stop, record, retry,badButton, cropButton,fitButton;
//    Button replaceImage= (Button) findViewById(R.id.imageChangeButton);

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
    File tmp_file;
    Bitmap scaledBitmap;

    public static final int REQUEST_CROP=2;

    public int flag;
    public static int RESIZE_FACTOR;
    /** Id of image resource to decode */
    private int mSourceId;

    /** Wanted width of decoded image */
    private int mDstWidth;

    /** Wanted height of decoded image */
    private int mDstHeight;

    private String temp_path;
    private String cropped_path;

    private boolean backup;

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
        final Chronometer myChronometer = (Chronometer)findViewById(R.id.chronometer);
        badButton=(Button)findViewById(R.id.button_scaling_bad);
        fitButton=(Button)findViewById(R.id.button_scaling_fit);
        cropButton=(Button)findViewById(R.id.button_scaling_crop);
        mDstWidth = getResources().getDimensionPixelSize(R.dimen.destination_width);
        mDstHeight = getResources().getDimensionPixelSize(R.dimen.destination_height);

        backup = false;

        count = sharedPref.getInt(projectName,0);
        if(count==0)
        {
            editor.putInt(projectName,0);
            editor.commit();
        }




        image_file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya"+"/"+projectName+"/images", imagefileName + ".png");
        tmp_file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya"+"/"+projectName+"/tmp_images", imagefileName + ".png");


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
                myChronometer.setVisibility(View.VISIBLE);
                try {
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                    isRecording = true;

                    myChronometer.setBase(SystemClock.elapsedRealtime());
                    myChronometer.start();
                    //  audioProgressBar.setProgress(0);

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
                            myChronometer.setVisibility(View.GONE);
                            audioProgressBar.setVisibility(View.VISIBLE);
                            songCurrentDurationLabel.setVisibility(View.VISIBLE);
                            songTotalDurationLabel.setVisibility(View.VISIBLE);
                            audioProgressBar.setProgress(0);
                            myAudioRecorder = null;
                            isRecording = false;
                            myChronometer.stop();
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
                myChronometer.setVisibility(View.VISIBLE);
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
                    myChronometer.setBase(SystemClock.elapsedRealtime());
                    myChronometer.start();
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
                myChronometer.setVisibility(View.GONE);
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
        scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, mDstWidth,mDstHeight, ScalingUtilities.ScalingLogic.FIT);
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

//        // Part 1: Decode image
//        Bitmap unscaledBitmap = ScalingUtilities.decodeResource(mDstWidth, mDstHeight, ScalingUtilities.ScalingLogic.FIT, image_file.getAbsolutePath());
//        // Part 2: Scale image
//        scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, mDstWidth, mDstHeight, ScalingUtilities.ScalingLogic.CROP);

//        imageView.setImageBitmap(scaledBitmap);

        Intent intent = new Intent(getApplicationContext(),CropImage.class);
        intent.putExtra("mDstWidth",mDstWidth);
        intent.putExtra("mDstHeight",mDstHeight);
        intent.putExtra("path", image_file.getAbsolutePath());
        startActivityForResult(intent, REQUEST_CROP);
//        unscaledBitmap.recycle();
    }


    public String[] imageBackup(String imagepath, Bitmap bm, Bitmap scaledBitmap){
        String[] str = imagepath.split("/");
        String temp_path="",path="";
        int i;
        for (i = 0; i < str.length - 1; i++) {
            if(i==str.length-2){
                temp_path = path;
            }
            path = path.concat(str[i]);
            path = path.concat("/");
        }

        String file_name = imagefileName + ".png";

        OutputStream out = null;
        File file = new File(path,file_name);

        OutputStream out2 = null;

        OutputStream out3 = null;
        File file3 = new File(temp_path+"/images/" ,file_name);

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap image = EditProject.getResizedBitmap(scaledBitmap, RESIZE_FACTOR);;

        try {
            out = new FileOutputStream(file);
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

            out3 = new FileOutputStream(file3);
            image.compress(Bitmap.CompressFormat.PNG, 100, out3);

            if(!backup) {
                File temp_file = new File(temp_path, file_name);

                out2 = new FileOutputStream(temp_file);
                bm.compress(Bitmap.CompressFormat.PNG, 100, out2);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if(out2 != null){
                    out2.close();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

        backup = true;

        String[] paths = new String[2];
        paths[0] = path;
        paths[1] = temp_path;
        return paths;
    }

    public Bitmap rotateImage(Bitmap bm){
        Matrix matrix = new Matrix();
        matrix.reset();
        int rotation = 90;

        int bw = bm.getWidth();
        int bh = bm.getHeight();

        Bitmap rot_image = Bitmap.createBitmap(bh, bw, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(rot_image);
        matrix.postTranslate(-bw / 2, -bh / 2);
        matrix.postRotate(rotation);
        matrix.postTranslate(bh / 2, bw / 2);
        canvas.drawBitmap(bm, matrix, null);

        return rot_image;
    }


    public void rotateButtonPressed(View view){
        String imagepath = tmp_file.getAbsolutePath();
        Bitmap bm = ScalingUtilities.decodeResource(mDstWidth, mDstHeight, ScalingUtilities.ScalingLogic.FIT, imagepath);
        scaledBitmap = rotateImage(bm);

        RESIZE_FACTOR = getScreenwidth();
        Bitmap imageview = EditProject.getResizedBitmap(scaledBitmap, RESIZE_FACTOR);
        imageView.setImageBitmap(imageview);

        String[] paths = new String[2];
        paths = imageBackup(imagepath, bm, scaledBitmap);
        cropped_path = paths[0];
        temp_path = paths[1];

        System.out.println("cropped_path = "+cropped_path);
        System.out.println("temp_path = "+temp_path);

        flag = 2;
        changed = true;
    }

    public void onChangeImagePressed(View view){

        //registerForContextMenu(replaceImage);

        /*PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_replace_image, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.takeApicture:
                        Log.i("Change image","camera selected");*/


        //MultipleImage selection code (implementation same as the one in Editproject////////////////////////////////////////////

        Intent intent = new Intent(getApplicationContext(), MultiImageSelectorActivity.class);

// whether show camera
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);

// max select image amount
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);

// select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);

        startActivityForResult(intent, EditProject.REQUEST_IMAGE);

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*
                        return true;
                    case R.id.PickFromGallery:
                        Log.i("Change image","gallery selected");

                        return true;
                    default:
                        return false;
                }
            }
        });*/



    }

    /**
     * read editproject before reading this
     * @see EditProject
     */

    public int getScreenwidth(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RESIZE_FACTOR = getScreenwidth();

        flag = 0;
        if(requestCode == EditProject.REQUEST_IMAGE){
            System.out.println("REQUEST_IMAGE passed");
            if(resultCode == RESULT_OK){
                // Get the result list of select image paths
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                final ContentResolver cr = this.getContentResolver();

                try {




                    // for (i = 0; i < path.size(); i++) {
                    Log.i("change image ", " file : " + new File(path.get(0)).getName());
                    Log.i("change image- name",image_file.getName());
                    Log.i("Change image- path", image_file.getAbsolutePath());

                    File changeImageFile= new File(path.get(0));
                    //File changeImageFileNew= new File(changeImageFile.getParent()+"/"+image_file.getName());

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(cr, EditProject.getImageContentUri(getApplicationContext(), path.get(0))); //getbitmap() needs content uri as its parameter. for that see getimage content uri() method.
                    bitmap = EditProject.getResizedBitmap(bitmap, RESIZE_FACTOR);
                    Projectfile f = new Projectfile(getApplicationContext());


                    //f.addImage(bitmap, projectName);
                    // File newImage= new File(image_file.getParent().toString()+"/"+projectName+"."+);
                    // Log.i("change image",newImage.getAbsolutePath());
                    image_file.delete();
                    //newImage.renameTo(image_file);
                    //Log.i("change image", newImage.getAbsolutePath());
                    f.replaceImage(image_file.getName(),projectName,bitmap);
                    recreate();

/*


                    //}
                    Projectfile.deleteTmpFolder(projectName);

                    *//*EditProject edObj= new EditProject();
                    edObj.loadImages(false);
*/

                } catch (IOException fe) {
                    Toast.makeText(getApplicationContext(),"Image file not found in the library " + Uri.parse(path.get(0)),Toast.LENGTH_LONG).show();
                    fe.printStackTrace();
                }
            }
        }
        else if(requestCode == REQUEST_CROP){
            if(resultCode == RESULT_OK){
                String imagepath = image_file.getAbsolutePath();
                scaledBitmap = ScalingUtilities.decodeResource(mDstWidth, mDstHeight, ScalingUtilities.ScalingLogic.FIT, imagepath);
                RESIZE_FACTOR = getScreenwidth();
                Bitmap imageview = EditProject.getResizedBitmap(scaledBitmap, RESIZE_FACTOR);
                imageView.setImageBitmap(imageview);
            }
        }
    }
//
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        MenuInflater inflater= new MenuInflater(getApplicationContext());
//
//    }
/* public void rotateButtonPressed(View view){
        // Part 1: Decode image
        Bitmap unscaledBitmap = ScalingUtilities.decodeResource(mDstWidth, mDstHeight, ScalingUtilities.ScalingLogic.FIT, image_file.getAbsolutePath());
        Bitmap scaledBitmap1 = ScalingUtilities.createScaledBitmap(unscaledBitmap, mDstWidth,
                mDstHeight, ScalingUtilities.ScalingLogic.FIT);
        //unscaledBitmap.recycle();
        Matrix matrix = new Matrix();
        imageView.setScaleType(ImageView.ScaleType.MATRIX);   //required
        matrix.postRotate((float) 90);

        imageView.setImageMatrix(matrix);
        scaledBitmap=Bitmap.createBitmap(scaledBitmap1,0,0,scaledBitmap1.getWidth(),scaledBitmap1.getHeight(),matrix,true);
        //imageView.setImageBitmap(scaledBitmap);
        changed=true;

    }*/


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
            RESIZE_FACTOR = getScreenwidth();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.save);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    File sdCard = Environment.getExternalStorageDirectory();
                    File imgDir = new File(sdCard.getAbsolutePath() + "/lokavidya" + "/" + projectName + "/images");
                    File tmpimgDir = new File(sdCard.getAbsolutePath() + "/lokavidya" + "/" + projectName + "/tmp_images");

                    File writetofile = new File(imgDir, imagefileName + ".png");
                    File write_backup = new File(tmpimgDir, imagefileName + ".png");

                    FileOutputStream outStream = null;
                    FileOutputStream outStream_backup = null;
                    try {
                        outStream = new FileOutputStream(writetofile);
                        outStream_backup = new FileOutputStream(write_backup);
                        Bitmap tmp_image = scaledBitmap;
                        if(flag>0){
                            scaledBitmap = EditProject.getResizedBitmap(scaledBitmap, RESIZE_FACTOR);
                        }
                        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                        tmp_image.compress(Bitmap.CompressFormat.PNG, 100, outStream_backup);

                        outStream.flush();
                        outStream.close();

                        outStream_backup.flush();
                        outStream_backup.close();

                    } catch (Exception e) {
                        Toast.makeText(Recording.this, "Cannot create new image : addimage", Toast.LENGTH_LONG);
                        e.printStackTrace();
                    } finally {
                        try {
                            if (outStream != null) {
                                outStream.close();
                                outStream_backup.close();
                                File temp_image = new File(temp_path+imagefileName + ".png");
                                temp_image.delete();
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
                    if(flag>0){

                        flag = 0;
                        String file_name = imagefileName + ".png";
                        Bitmap bm = ScalingUtilities.decodeResource(mDstWidth, mDstHeight, ScalingUtilities.ScalingLogic.FIT, temp_path+file_name);

                        OutputStream out = null;
                        File file = new File(cropped_path,file_name);

                        OutputStream out2 = null;
                        File file2 = new File(temp_path+"/images/" ,file_name);
                        try {
                            out = new FileOutputStream(file);
                            bm.compress(Bitmap.CompressFormat.PNG, 100, out);

                            out2 = new FileOutputStream(file2);
                            bm.compress(Bitmap.CompressFormat.PNG, 100, out2);
                        }
                        catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        finally{
                            try{
                                if(out != null){
                                    out.close();
                                    out2.close();
                                    File temp_image = new File(temp_path+imagefileName + ".png");
                                    temp_image.delete();
                                }
                            }
                            catch(IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                    dialog.cancel();
                    finish();
                }
            });

            builder.show();
        } else{
            finish();
        }

    }
}