package com.iitb.mobileict.lokavidya.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.iitb.mobileict.lokavidya.Communication.Settings;
import com.iitb.mobileict.lokavidya.Communication.postmanCommunication;
import com.iitb.mobileict.lokavidya.R;
import com.iitb.mobileict.lokavidya.data.browseVideoElement;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LinkVideos extends AppCompatActivity {

    ExpListViewAdapterWithCheckBox listAdapter;
    ExpandableListView expListView;
    public static ArrayList<String> listDataHeader;
    public static ArrayList<String> checkedVideosList;
    public static HashMap<String, List<String>> listDataChild;

    public static List<String> listLinkHeader;
    public static HashMap<String, List<String>> listLinkChild;
    public static HashMap<String,String> nameToId;
    public static HashMap<String,String> idToName;
    public  static HashMap<String,String> idToURL;
    public static final String VID_JSONARRAY_URL = "http://"+ Settings.serverURL+"/api/tutorials";
    public static final String VID_CAT_JSONARRAY_URL = "http://"+Settings.serverURL+"/api/categorys";
    Bundle extras;


    public List<browseVideoElement> videoObjList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_videos);
        extras= new Bundle();
        Intent intent;
        intent= getIntent();
        extras= intent.getExtras();
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.LinkVideosExpList );

        // preparing list data
        //prepareListData();
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listLinkHeader = new ArrayList<String>();
        listLinkChild = new HashMap<String, List<String>>();



        new viewVideosTask(LinkVideos.this).execute("OK"); //the real data preparation happens here

        System.out.println("asynctask running...");


        Log.i("Oncreate", "Preparelistdata complete");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actionbutton_next, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.link_video_next:
                Log.i("Link Video", "Done next pressed");
                checkedVideosList= new ArrayList<String>();
                Iterator iterator= ExpListViewAdapterWithCheckBox.mChildCheckStates.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry checkmap= (Map.Entry) iterator.next();
                    boolean checkedVideos[]= (boolean[])checkmap.getValue();
                    System.out.println("For group no."+ (Integer)checkmap.getKey()+" ,values:");
                    for(int i=0;i<checkedVideos.length;i++){
                        System.out.println("  "+checkedVideos[i]);
                        if(checkedVideos[i]){
                            System.out.println("\tChecked video:" + listAdapter.getChild((Integer) checkmap.getKey(), i).toString());
                            checkedVideosList.add(listAdapter.getChild((Integer) checkmap.getKey(), i).toString());

                        }
                    }
                }




                Intent in = new Intent(LinkVideos.this,VideoLinkDescriptionActivity.class);
                extras.putStringArrayList("LINKED_VIDEOS",checkedVideosList);
                in.putExtras(extras);
                startActivity(in);
               /* AlertDialog.Builder builderSingle = new AlertDialog.Builder(LinkVideos.this);
                builderSingle.setIcon(R.drawable.ic_launcher);
                builderSingle.setTitle("Enter Details of the links:");


                builderSingle.setPositiveButton(
                        getString(R.string.OkButton),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builderSingle.setAdapter(dialoglistAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("Dialog", "Dialog onclick");
                    }
                });
                builderSingle.show();

               // builderSingle.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

*/
        }
        return super.onOptionsItemSelected(item);
    }



    private class viewVideosTask extends AsyncTask<String,Void,String> {

        ProgressDialog pd;
        Context context;
        JSONArray vidArray;
        JSONArray catArray;

        public viewVideosTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* pd=new ProgressDialog(context);
            pd.setMessage(getString(R.string.stitchingProcessTitle));
            pd.show();*/
        }

        @Override
        protected String doInBackground(String... params) {
            Log.i("AsyncTask", "inside doinbackgrnd");
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if(!sharedPreferences.getBoolean("Skip",false)) {
                vidArray = postmanCommunication.okhttpgetVideoJsonArray(VID_JSONARRAY_URL, sharedPreferences.getString("token", ""));
                Log.i("videos jsonarray", vidArray.toString());
                catArray = postmanCommunication.okhttpgetVideoJsonArray(VID_CAT_JSONARRAY_URL, sharedPreferences.getString("token", ""));
                Log.i("categ jsonarray", catArray.toString());
            }else{
                Log.i("Browsing videos","Guest(Login has been skipped)");
                vidArray = postmanCommunication.okhttpgetGuestVideoJsonArray(VID_JSONARRAY_URL);
                Log.i("videos guest jsonarray", vidArray.toString());
                catArray = postmanCommunication.okhttpgetGuestVideoJsonArray(VID_CAT_JSONARRAY_URL);
                Log.i("categ guest jsonarray", catArray.toString());
            }


            browseVideoElement tempVidObj = new browseVideoElement();
            videoObjList = new ArrayList<browseVideoElement>();
            int i, catId;
            nameToId= new HashMap<String,String>();
            idToName= new HashMap<String,String>();
            idToURL= new HashMap<String,String>();
            try {
              //  vidArray = new JSONArray(loadJSONFromAsset("vids.json"));
              //  catArray = new JSONArray(loadJSONFromAsset("cats.json"));
                for (i = 0; i < vidArray.length(); i++) {
                    tempVidObj = new browseVideoElement();
                    tempVidObj.setVideoName(vidArray.getJSONObject(i).getString("name"));
                    tempVidObj.setVideoId(vidArray.getJSONObject(i).getString("id"));
                    Log.i("setvideo name", vidArray.getJSONObject(i).getString("name"));
                    catId = vidArray.getJSONObject(i).getJSONObject("categoryMembership").getInt("categoryId");
                    tempVidObj.setCategoryID(catId);
                    tempVidObj.setCategoryName(catArray.getJSONObject(catId - 1).getString("name"));
                    if (!vidArray.getJSONObject(i).isNull("externalVideo")) {
                        tempVidObj.setVideoUrl(vidArray.getJSONObject(i).getJSONObject("externalVideo").getString("httpurl"));
                        idToURL.put(tempVidObj.getVideoId(),tempVidObj.getVideoUrl());
                    } else {
                        tempVidObj.setVideoUrl("no URL");
                        idToURL.put(tempVidObj.getVideoId(), null);

                    }
                    nameToId.put(tempVidObj.getVideoName(),tempVidObj.getVideoId());
                    idToName.put(tempVidObj.getVideoId(),tempVidObj.getVideoName());
                    videoObjList.add(tempVidObj);

                }
                Recording.idToName=idToName;
                Log.i("asynctask", "data preparation successful");
            } catch (JSONException j) {
                j.printStackTrace();
            }

            /*Iterator<browseVideoElement> it= videoObjList.iterator();
            while(it.hasNext()){
                System.out.println("-----------------videoobj list ka attribute--------:" + it.next().getVideoName());
            }*/
            for (browseVideoElement b : videoObjList) {
                if (listDataChild.containsKey(b.getCategoryName())) {
                    Log.i("Asynctask", "category existing");

                    Log.i("video name", b.getVideoName());

                    listDataChild.get(b.getCategoryName()).add(b.getVideoName());
                    // if(b.getVideoUrl()!=null) {
                    listLinkChild.get(b.getCategoryName()).add(b.getVideoUrl());
                    //}
                    Log.i("Asynctask", "videoname mapped");

                } else {
                    Log.i("Asynctask", "new category");

                    List<String> vidtemp = new ArrayList<String>();
                    List<String> linktemp = new ArrayList<String>();
                    Log.i("video name", b.getVideoName());
                    vidtemp.add(b.getVideoName());

                    // if(b.getVideoUrl()!=null){
                    linktemp.add(b.getVideoUrl());//}

                    listDataChild.put(b.getCategoryName(), vidtemp);
                    listLinkChild.put(b.getCategoryName(), linktemp);
                    listDataHeader.add(b.getCategoryName());
                    listLinkHeader.add(b.getCategoryName());
                    Log.i("Asynctask new catg", "new category data mapped");
                }
            }

            Log.i("Asynctask", "Phew, over!");


            return "YAY";
        }

        @Override
        protected void onPostExecute(String s) {

            Log.i("AsyncTask", s);

            listAdapter = new ExpListViewAdapterWithCheckBox(context, listDataHeader, listDataChild);

            // setting list adapter
            expListView.setAdapter(listAdapter);
            // Listview on child click listener
            /*expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {

                    return false;
                }
            });
*/
            // pd.dismiss();
        }
    }




    public String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = getApplication().getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}



