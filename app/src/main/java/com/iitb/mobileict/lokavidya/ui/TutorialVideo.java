package com.iitb.mobileict.lokavidya.ui;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.iitb.mobileict.lokavidya.R;

/**
 * This is a class merely for displaying the tutorial video after clicking the tutorialButton. It contains a VideoView with controls
 *  For now this feature has been removed (Date: 30/10/2015)
 */
public class TutorialVideo extends Activity {

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_video);
        Intent in= getIntent();
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        VideoView HowToLokavidya= (VideoView) findViewById(R.id.TutorialVideo);

        HowToLokavidya.setMediaController(new MediaController(this));
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample);
        Log.i("HowToVideo", video.toString());
        HowToLokavidya.setVideoURI(video);
        HowToLokavidya.start();





    }
*/
}
