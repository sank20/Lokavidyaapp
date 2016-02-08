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
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.iitb.mobileict.lokavidya.Communication.Settings;
import com.iitb.mobileict.lokavidya.Communication.postmanCommunication;
import com.iitb.mobileict.lokavidya.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VideoPlayerActivity extends Activity {

    private static final String TAG = "VideoPlayerActivity";

    private static final int PREFERENCE_MODE_PRIVATE = 0;

    public static final String VID_TUTORIAL = "http://"+ Settings.serverURL+"/api/tutorials";


    VideoView mVideoView;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    List<String> linkedVideosArray;
    HashMap<String,JSONObject> videoToLink;
    TextView videoTitlePlaying;
    ImageView downloadVideo;
    ProgressDialog progressDialog;
    String videoId;
    SharedPreferences sharedpref;

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
        videoId =getIntent().getStringExtra("VIDEO_ID");
        sharedpref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String playURL= sharedpref.getString("playVideoURL", "NA");
        String playName= sharedpref.getString("playVideoName","NA");
        String playDescrip= sharedpref.getString("playVideoDesc","NA");
        String playVideoId = sharedpref.getString("playVideoId","");

        Log.d(TAG,"Video URL:"+playURL);
        Log.d(TAG,"Video Name:"+playName);
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
                JSONObject tutorialJSON=videoToLink.get(item);
// THIS ASYNCTASK CONTAINS CODE TO POPULATE THE LIST AND TAKE CARE OF OTHER THINGS
                try {

                    SharedPreferences.Editor editor = sharedpref.edit();
                    editor.putString("playVideoName", tutorialJSON.getString("name"));
                    editor.putString("playVideoURL", tutorialJSON.getJSONObject("externalVideo").getString("httpurl"));
                    editor.putString("playVideoDesc", tutorialJSON.getString("description"));
                    videoId = tutorialJSON.getString("id");
                    editor.commit();
                    recreate();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

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
        HashMap<String,String> videoToLink;

        public getHyperlinkJson(Context context, HashMap<String,String> videoToLink){
            this.context=context;
            this.videoToLink =videoToLink;
        }

        @Override
        protected String doInBackground(String... params) {
            String videoId= params[0];

            //TODO do the network calls and get the json

            return "";
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
            videoToLink= new HashMap<String,JSONObject>();
            linkedVideosArray = new ArrayList<String>();
        }

        @Override
        protected String doInBackground(String... params) {
            String videoId= params[0];

            String json="";
            //Network call to get tutorial information
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            JSONObject tutorialJSON = postmanCommunication.okhttpgetTutorial(VideoPlayerActivity.VID_TUTORIAL, videoId, sharedPreferences.getString("token", ""));
            Log.d(TAG,tutorialJSON.toString());
            //Network calls to get information on the links
            //
            try {
                String referenceResourceLink=tutorialJSON.getString("referenceResourceLink");
                JSONArray referenceResourceLinkJSONObject = new JSONArray(referenceResourceLink);

                for(int i=0;i<referenceResourceLinkJSONObject.length();i++)
                {
                    JSONObject tutJsonObject = referenceResourceLinkJSONObject.getJSONObject(i);
                    JSONObject linktutorialJSON;
                    try {
                        linktutorialJSON = postmanCommunication.okhttpgetTutorial(VideoPlayerActivity.VID_TUTORIAL, tutJsonObject.getString("videoId"), sharedPreferences.getString("token", ""));
                        Log.d(TAG,tutorialJSON.toString());
                        if(linktutorialJSON.getJSONObject("externalVideo")!=null)
                        {
                            linkedVideosArray.add(linktutorialJSON.getString("name"));
                            videoToLink.put(linktutorialJSON.getString("name"),linktutorialJSON);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG,"Error in "+tutJsonObject.toString());
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }



            return json;
        }

        @Override
        protected void onPostExecute(String json) {

            if(linkedVideosArray.size()!=0)
            arrayAdapter = new ArrayAdapter<String>(context, R.layout.activity_video_player_content, R.id.textView_video_title, linkedVideosArray);
            listView.setAdapter(arrayAdapter);
        }

        }
}
