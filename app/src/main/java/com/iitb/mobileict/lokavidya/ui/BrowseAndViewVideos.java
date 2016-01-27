package com.iitb.mobileict.lokavidya.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;


import com.iitb.mobileict.lokavidya.Communication.Communication;
import com.iitb.mobileict.lokavidya.Communication.postmanCommunication;
import com.iitb.mobileict.lokavidya.R;
import com.iitb.mobileict.lokavidya.data.browseVideoElement;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class BrowseAndViewVideos extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    public static List<String> listDataHeader;
    public static HashMap<String, List<String>> listDataChild;

    public static List<String> listLinkHeader;
    public static HashMap<String, List<String>> listLinkChild;

    private final String VID_JSONARRAY_URL = "http://192.168.1.134:8080/api/tutorials";
    private final String VID_CAT_JSONARRAY_URL = "http://192.168.1.134:8080/api/categorys";

    public List<browseVideoElement> videoObjList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veiw_videos);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.ViewVideosExpList                                                                                                                                                                                                                                                                                                                                                                                                                                                              );

        // preparing list data
        //prepareListData();
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listLinkHeader = new ArrayList<String>();
        listLinkChild = new HashMap<String, List<String>>();



        new viewVideosTask(BrowseAndViewVideos.this).execute("OK"); //the real data preparation happens here

        System.out.println("asynctask running...");


        Log.i("Oncreate", "Preparelistdata complete");

        Log.i("Oncreate over","all listeners set");
    }


    @Override
    protected void onStart() {
        super.onStart();



                // start time consuming background process here



    }

    private class viewVideosTask extends AsyncTask<String,Void,String>{

        ProgressDialog pd;
        Context context;
        JSONArray vidArray;
        JSONArray catArray;

        public viewVideosTask(Context context){
            this.context=context;
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
            vidArray = postmanCommunication.okhttpgetVideoJsonArray(VID_JSONARRAY_URL);
            Log.i("videos jsonarray",vidArray.toString());
            catArray = postmanCommunication.okhttpgetVideoJsonArray(VID_CAT_JSONARRAY_URL);
            Log.i("categ jsonarray", catArray.toString());

            browseVideoElement tempVidObj= new browseVideoElement();
            videoObjList= new ArrayList<browseVideoElement>();
            int i,catId;

            try {
                for (i = 0; i < vidArray.length(); i++) {
                    tempVidObj = new browseVideoElement();
                    tempVidObj.setVideoName(vidArray.getJSONObject(i).getString("name"));
                    Log.i("setvideo name",vidArray.getJSONObject(i).getString("name"));
                    catId = vidArray.getJSONObject(i).getJSONObject("categoryMembership").getInt("categoryId");
                    tempVidObj.setCategoryID(catId);
                    tempVidObj.setCategoryName(catArray.getJSONObject(catId - 1).getString("name"));
                    if (!vidArray.getJSONObject(i).isNull("externalVideo")){
                        tempVidObj.setVideoUrl(vidArray.getJSONObject(i).getJSONObject("externalVideo").getString("url"));
                }else{
                        tempVidObj.setVideoUrl("no URL");
                    }
                    videoObjList.add(tempVidObj);

                }
                Log.i("asynctask","data preparation successful");
            }catch (JSONException j){
                j.printStackTrace();
            }

            /*Iterator<browseVideoElement> it= videoObjList.iterator();
            while(it.hasNext()){
                System.out.println("-----------------videoobj list ka attribute--------:" + it.next().getVideoName());
            }*/
            for(browseVideoElement b : videoObjList){
                if(listDataChild.containsKey(b.getCategoryName())){
                    Log.i("Asynctask", "category existing");

                    Log.i("video name",b.getVideoName());

                    listDataChild.get(b.getCategoryName()).add(b.getVideoName());
                   // if(b.getVideoUrl()!=null) {
                        listLinkChild.get(b.getCategoryName()).add(b.getVideoUrl());
                    //}
                    Log.i("Asynctask", "videoname mapped");

                }else{
                    Log.i("Asynctask", "new category");

                    List<String> vidtemp= new ArrayList<String>();
                    List<String> linktemp= new ArrayList<String>();
                    Log.i("video name",b.getVideoName());
                    vidtemp.add(b.getVideoName());

                   // if(b.getVideoUrl()!=null){
                    linktemp.add(b.getVideoUrl());//}

                    listDataChild.put(b.getCategoryName(), vidtemp);
                    listLinkChild.put(b.getCategoryName(), linktemp);
                    listDataHeader.add(b.getCategoryName());
                    listLinkHeader.add(b.getCategoryName());
                    Log.i("Asynctask new catg","new category data mapped");
                }
            }

            Log.i("Asynctask","Phew, over!");



            return "YAY";
        }

        @Override
        protected void onPostExecute(String s) {

            Log.i("AsyncTask",s);

            listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);

            // setting list adapter
            expListView.setAdapter(listAdapter);

            // Listview on child click listener
            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    // TODO Auto-generated method stub
                    /*Toast.makeText(
                            getApplicationContext(),
                            listDataHeader.get(groupPosition)
                                    + " : "
                                    + listDataChild.get(
                                    listDataHeader.get(groupPosition)).get(
                                    childPosition), Toast.LENGTH_SHORT)
                            .show();*/

                    Log.i("video link:", listLinkChild.get(
                            listLinkHeader.get(groupPosition)).get(
                            childPosition));

                    String url= listLinkChild.get(
                            listLinkHeader.get(groupPosition)).get(
                            childPosition);

                    final String filename= URLDecoder.decode(url.substring(url.lastIndexOf("/")));
                    Communication.downloadBrowseVideo(context, "http://" + url, filename);


                    final ProgressDialog downloadvid = ProgressDialog.show(context, getString(R.string.stitchingProcessTitle), getString(R.string.download_video_progress_message));

                    downloadvid.setCanceledOnTouchOutside(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Boolean down=false;

                            while (!down) {/*wait till download hasn't completed */
                                down = Communication.isDownloadComplete;
                            //Log.i("Downloaded?",String.valueOf(down));
                            }

                            downloadvid.dismiss();

                            Intent playVideo = new Intent(context, TutorialVideo.class);
                            playVideo.putExtra("VIDEO_URL", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Lokavidya Videos/" + filename);
                            startActivity(playVideo);


                        }
                    }).start();



                    return false;
                }
            });

               // pd.dismiss();
        }
    }



}


///-----------------------------------------------------------------------------------------------------
 class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandablelist_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandablelist_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}