package com.iitb.mobileict.lokavidya.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.iitb.mobileict.lokavidya.Communication.Communication;
import com.iitb.mobileict.lokavidya.Communication.Settings;
import com.iitb.mobileict.lokavidya.Communication.postmanCommunication;
import com.iitb.mobileict.lokavidya.R;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLDecoder;
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
    String zipurl;
    SharedPreferences sharedpref;
    Button generateQRButton;
    private String seedpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya/";


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
        videoId =getIntent().getExtras().getString("VIDEO_ID");
        zipurl=getIntent().getExtras().getString("ZIP_URL");
        sharedpref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        generateQRButton = (Button) findViewById(R.id.button_generate_qr);
        generateQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayerActivity.this);
                LayoutInflater inflater = VideoPlayerActivity.this.getLayoutInflater();
                View dialogView= inflater.inflate(R.layout.dialog_generatedqr_code, null);
                ImageView qrImage = (ImageView) dialogView.findViewById(R.id.QRCodeimageView);
                qrImage.setImageResource(R.drawable.ic_launcher_big);
                builder.setView(dialogView)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.create().show();
            }
        });
        System.out.println(System.getProperty("java.io.tmpdir"));
        Log.d(TAG,"VideoID:"+videoId);
        final String playURL= sharedpref.getString("playVideoURL", "NA");
        final String playName= sharedpref.getString("playVideoName","NA");
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
        downloadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayerActivity.this);
                builder.setTitle(getString(R.string.download_from_videoplayeractivity))
                        .setItems(getResources().getStringArray(R.array.download_video_or_project), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        final String filename = URLDecoder.decode(playURL.substring(playURL.lastIndexOf("/")));
                                        //  progressDialog.show();
                                        Communication.downloadBrowseVideo(getApplicationContext(), "http://" + playURL, filename, Environment.DIRECTORY_DOWNLOADS + "/Lokavidya Videos/", TAG);

                                        break;
                                    case 1:
                                        String zipname = zipurl.split("/")[zipurl.split("/").length-1];
                                        downloadProj(VideoPlayerActivity.this,playName.substring(0,playName.lastIndexOf("-")-1),zipname,"http://"+ zipurl);
                                        break;
                                }

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                //builder.show();

            }
        });

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
                    Log.d(TAG, videoId);
                    editor.putString("playVideoId",videoId);
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(),VideoPlayerActivity.class);
                    intent.putExtra("VIDEO_ID", videoId);
                    startActivity(intent);
                    //finish();
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



    /**
     * method which calls the communication method for the given project zip and performs other checks
     * @param Projectname name of the project
     * @param zipname name of the project zip file
     * @param link link to the server
     */
    public void downloadProj(final Context context,String Projectname, final String zipname, String link) {

        if (!new File(seedpath + Projectname+"/").exists()) {
            Communication.isDownloadComplete = false;
            Communication.downloadSampleProjects(context,link,zipname);
            Log.i("Downloaded?", String.valueOf(Communication.isDownloadComplete));
            final ProgressDialog downloadSeed = ProgressDialog.show(context, getString(R.string.stitchingProcessTitle), getString(R.string.progressdialog_download_project_from_videoplayeractivity));
            downloadSeed.setCancelable(false);
            downloadSeed.setCanceledOnTouchOutside(false);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!Communication.isDownloadComplete) {/*wait till download hasn't completed */}


                    String serverseed = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/"+ zipname).toString();
                    try {
                        ZipFile seedzip = new ZipFile(serverseed);
                        seedzip.extractAll(seedpath);
                    } catch (ZipException e) {
                        e.printStackTrace();
                    }

                    File delTemp = new File(serverseed);
                    delTemp.delete();
                    //delTemp.getParentFile().delete();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(context, "The project has been downloaded, please check your project list.", Toast.LENGTH_SHORT).show();;
                            downloadSeed.dismiss();


                        }
                    });


                }
            }).start();

        }


    }



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
            JSONObject tutorialJSON=null;
            if(!sharedPreferences.getBoolean("Skip",false)) {
                 tutorialJSON = postmanCommunication.okhttpgetTutorial(VideoPlayerActivity.VID_TUTORIAL, videoId, sharedPreferences.getString("token", ""));
            }else{
                Log.i(TAG,"login skipped, calling guest method");
                tutorialJSON = postmanCommunication.okhttpgetGuestTutorial(VideoPlayerActivity.VID_TUTORIAL, videoId);

            }
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
                        if(!sharedPreferences.getBoolean("Skip",false)) {
                            linktutorialJSON = postmanCommunication.okhttpgetTutorial(VideoPlayerActivity.VID_TUTORIAL, tutJsonObject.getString("videoId"), sharedPreferences.getString("token", ""));
                        }else{
                            Log.i(TAG,"login skipped, calling guest method");

                            linktutorialJSON = postmanCommunication.okhttpgetGuestTutorial(VideoPlayerActivity.VID_TUTORIAL, tutJsonObject.getString("videoId"));

                        }
                        Log.d(TAG,tutorialJSON.toString());
                        if(!linktutorialJSON.isNull("externalVideo"))
                        {
                            linkedVideosArray.add(linktutorialJSON.getString("name"));
                            Log.i("Videoplayeractivity","linked video:"+linktutorialJSON.getString("name"));
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
