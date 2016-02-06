package com.iitb.mobileict.lokavidya.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.iitb.mobileict.lokavidya.R;

import java.util.ArrayList;
import java.util.List;

public class VideoPlayerActivity extends Activity {

    private static final String TAG = "VideoPlayerActivity";

    VideoView mVideoView;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    List<String> linkedVideosArray;
    TextView videoTitlePlaying;
    ImageView downloadVideo;
    ProgressDialog progressDialog;
    //TODO Think about a code for linkedVideosArray that would return list of videos to populate the ListView

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progressDialog.dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        final String videoURL=getIntent().getStringExtra("VIDEO_URL");
        //Code for the Video Player
        Uri uri = Uri.parse("http://"+videoURL); //Declare your url here.
        mVideoView  = (VideoView)findViewById(R.id.video_view);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.setVideoURI(uri);
        mVideoView.requestFocus();
        mVideoView.start();
        //progressDialog= new ProgressDialog(VideoPlayerActivity.this);
        //progressDialog.setCancelable(false);
        //TODO code to download the current playing video
        downloadVideo = (ImageView) findViewById(R.id.imageView_download_video);
        /*downloadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String filename= URLDecoder.decode(videoURL.substring(videoURL.lastIndexOf("/")));
                progressDialog.show();
                Communication.downloadBrowseVideo(getApplicationContext(), "http://" + videoURL, filename, Environment.DIRECTORY_DOWNLOADS+"/Lokavidya Videos/",TAG);
            }
        });*/
        //TODO find a code for getting the Video Title while calling recreate()
        videoTitlePlaying = (TextView) findViewById(R.id.textView_playing_video);
        videoTitlePlaying.setText(getIntent().getStringExtra("tutorialName"));
        //Code for the Linked Videos ListView
        listView = (ListView) findViewById(R.id.listView_hyperlink);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_video_player_content, R.id.textView_video_title, linkedVideos());
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                //TODO Add the logic for inflating the dynamic list of videos and handle the recreate() accordingly
                String item = (String) adapter.getItemAtPosition(position);
                recreate();
            }
        });
        //registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    public List<String> linkedVideos() {
        Context context = getApplicationContext();
        linkedVideosArray= new ArrayList<String>();
        //TODO Add the list of linked videos for hyperlinking
        linkedVideosArray.add("Agriculture");
        linkedVideosArray.add("Farming");
        linkedVideosArray.add("Fishing");
        linkedVideosArray.add("Pottery");
        linkedVideosArray.add("Animal Husbandry");
        return linkedVideosArray;
        //TODO also might need to find a way to parse the thumbnail placeholder as ArrayList
    }
}
