package com.iitb.mobileict.lokavidya.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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

    private static final int PREFERENCE_MODE_PRIVATE = 0;

    VideoView mVideoView;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    List<String> linkedVideosArray;
    TextView videoTitlePlaying;
    ImageView downloadVideo;
    ProgressDialog progressDialog;
    String videoId;


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
        videoId= getIntent().getStringExtra("VIDEO_ID");
        SharedPreferences sharedpref= getPreferences(PREFERENCE_MODE_PRIVATE);

        String playURL= sharedpref.getString("playVideoURL", "NA");
        String playName= sharedpref.getString("playVideoName","NA");
        String playDescrip= sharedpref.getString("playVideoDesc","NA");

        //Code for the Video Player
        Uri uri = Uri.parse("http://" + playURL); //Declare your url here.
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

        videoTitlePlaying = (TextView) findViewById(R.id.textView_playing_video);
        //videoTitlePlaying.setText(getIntent().getStringExtra("tutorialName"));
        videoTitlePlaying.setText(playName);
        //Code for the Linked Videos ListView
        listView = (ListView) findViewById(R.id.listView_hyperlink);


        //--------- THE LISTVIEW IS POPULATED USING THIS ASYNCTASK
        new getHyperlinkListTask(VideoPlayerActivity.this).execute(videoId);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                String item = (String) adapter.getItemAtPosition(position);

// THIS ASYNCTASK CONTAINS CODE TO POPULATE THE LIST AND TAKE CARE OF OTHER THINGS
                new getHyperlinkJson(VideoPlayerActivity.this).execute(videoId);

            }
        });
        //registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

   /* public List<String> linkedVideos() { -------------------------------taken care of in the get hyperlinklistTask asynctask
        Context context = getApplicationContext();
        linkedVideosArray= new ArrayList<String>();

        SharedPreferences sharedpref= getPreferences(PREFERENCE_MODE_PRIVATE);

        String playURL= sharedpref.getStringSet("linkedVideoSet", "NA");

        linkedVideosArray.add("Agriculture");
        linkedVideosArray.add("Farming");
        linkedVideosArray.add("Fishing");
        linkedVideosArray.add("Pottery");
        linkedVideosArray.add("Animal Husbandry");
        return linkedVideosArray;
        //TODO also might need to find a way to parse the thumbnail placeholder as ArrayList.- ---(NOT NOW)
    }*/


    //TODO-------------------------------------------------------CHECK THE ASYNCTASKS AND YOU HAVE TO DO ALMOST SAME THING TWICE IN BOTH----------------------------------------------------
    class getHyperlinkJson extends AsyncTask<String,Void,String>{


        Context context;

        public getHyperlinkJson(Context context){
            this.context=context;
        }

        @Override
        protected String doInBackground(String... params) {
            String videoId= params[0];
            String json="";

            //TODO do the network calls and get the json

            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);



            //TODO parse the name url and description from json and add in the value fields of editor.putString()

            SharedPreferences sharepref= getPreferences(PREFERENCE_MODE_PRIVATE);
            SharedPreferences.Editor editor= sharepref.edit();
            editor.putString("playVideoName","blah2");
            editor.putString("playVideoURL", "https://www.youtube.com/watch?v=uFsV0ieoU-w");
            editor.putString("playVideoDesc", "blah blah");
            editor.commit();
            recreate();



        }
    }


    class getHyperlinkListTask extends AsyncTask<String,Void,String>{
        Context context;

        public getHyperlinkListTask(Context context){
            this.context=context;
        }

        @Override
        protected String doInBackground(String... params) {
            String videoId= params[0];
            String json="";

            //TODO do the network calls and get the json

            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);

            linkedVideosArray= /*get this list from the json*/ null; //TODO : see the comment

            arrayAdapter = new ArrayAdapter<String>(context, R.layout.activity_video_player_content, R.id.textView_video_title, linkedVideosArray);
            listView.setAdapter(arrayAdapter);


        }

        }
}
