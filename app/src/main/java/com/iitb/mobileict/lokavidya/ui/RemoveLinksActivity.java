package com.iitb.mobileict.lokavidya.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iitb.mobileict.lokavidya.R;
import com.iitb.mobileict.lokavidya.data.Link;
import com.iitb.mobileict.lokavidya.util.ExtraImageHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RemoveLinksActivity extends Activity {

    ArrayAdapter dataAdapter;
    ArrayList<String> linked_videos;
    ArrayList<String> linked_video_id;
    String projectName;
    String imagefileName;
    Button remove;
     ArrayList<Link> removelink;
    Link remlink;
    HashMap<String,Boolean> checkboxValues= new HashMap<String,Boolean>();
    boolean jsonExists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_links);

        Intent in= getIntent();
        Bundle extras= in.getExtras();
        projectName=extras.getString("PROJECT_NAME");
        imagefileName= extras.getString("IMAGE_FILENAME");
        remove=(Button) findViewById(R.id.removeButton);
        removelink= new ArrayList<Link>();
        String json = null;
        try {
            InputStream is = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/lokavidya/"+projectName+ File.separator+"links/"+imagefileName.split("\\.")[1]+".json");
            if(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/lokavidya/"+projectName+ File.separator+"links/"+imagefileName.split("\\.")[1]+".json").exists()) {
                int size= is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            linked_videos= new ArrayList<String>();
            linked_video_id= new ArrayList<String>();
            JSONArray linkedVideos= new JSONArray(json);
            System.out.println("The json:"+ json);
            System.out.println("checking for null jsonarray:"+String.valueOf(linkedVideos.isNull(0)));
            for(int i=0;i< linkedVideos.length();i++) {
                linked_videos.add(linkedVideos.getJSONObject(i).getString("name"));
                linked_video_id.add(linkedVideos.getJSONObject(i).getString("videoId"));
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }


        final ExtraImageHandler rem= new ExtraImageHandler(Environment.getExternalStorageDirectory().getAbsolutePath()+"/lokavidya/",projectName,imagefileName);

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Remove Links","remove Pressed");
                Iterator iterator= checkboxValues.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry checkMap= (Map.Entry) iterator.next();
                    if((Boolean)checkMap.getValue()){
                        remlink= new Link();
                        remlink.setVideoId((String)checkMap.getKey());
                        rem.removeLink(remlink);

                    }
                }
                //Toast.makeText(RemoveLinksActivity.this,"Removed!",Toast.LENGTH_SHORT).show();
                /*Intent goBack= new Intent(RemoveLinksActivity.this,Recording.class);
                startActivity(goBack);*/
                finish();
            }



        });




        dataAdapter = new MyCustomAdapter(RemoveLinksActivity.this,
                R.layout.remove_links_list_item, linked_videos);
        ListView listView = (ListView) findViewById(R.id.removeLinksList);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);



    }

    private class MyCustomAdapter extends ArrayAdapter {

        private ArrayList<String> countryList;
        Context context;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<String> countryList) {
            super(context, textViewResourceId, countryList);
            this.countryList = countryList;
            this.context=context;

        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.remove_links_list_item, null);

                holder = new ViewHolder();
                //holder.code = (TextView) convertView.findViewById(R.id.removeLinkVideoname);
                holder.name = (CheckBox) convertView.findViewById(R.id.linkRemoveCheckbox);
                convertView.setTag(holder);

                /*holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;

                        Toast.makeText(getApplicationContext(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();
                        cb.setChecked(cb.isChecked());
                    }
                });*/
                holder.name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        checkboxValues.put(linked_video_id.get(position),isChecked);
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }


            // holder.code.setText(countryList.get(position));
            holder.name.setText(countryList.get(position));
            holder.name.setChecked(holder.name.isChecked());
            holder.name.setTag(holder);

            return convertView;

        }

    }




}
