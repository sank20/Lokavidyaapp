package com.iitb.mobileict.lokavidya.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.iitb.mobileict.lokavidya.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VideoLinkDescriptionActivity extends AppCompatActivity {

    ListView customDescriptionList;

    List<String> videolist= new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent in= getIntent();
        videolist= in.getStringArrayListExtra("LINKED_VIDEOS");
        setContentView(R.layout.activity_video_link_description);
        customDescriptionList= (ListView) findViewById(R.id.descListView);
        DialoglistAdapter listadapter= new DialoglistAdapter(VideoLinkDescriptionActivity.this,R.layout.dialog_list_item,R.id.vidname,videolist);
        customDescriptionList.setAdapter(listadapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actionbutton_tick, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.link_video_done:
                Log.i("Link Video", "Done tick pressed");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class DialoglistAdapter extends ArrayAdapter {

        Context c;
        String itemNames[];
        HashMap<Integer,String> selItems= new HashMap<Integer,String>();
        List<String> vidnames= new ArrayList<String>();
        public DialoglistAdapter(Context context, int resource, int textViewResourceId, List objects) {
            super(context, resource, textViewResourceId, objects);
            this.c=context;
            this.vidnames=objects;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Override
        public Object getItem(int position) {
            return vidnames.get(position);
        }

        @Override
        public int getPosition(Object item) {
            return super.getPosition(item);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ListViewHolder viewHolder;

            if (convertView == null) {

                viewHolder = new ListViewHolder();

                LayoutInflater inflater = getLayoutInflater();

                convertView = inflater.inflate(R.layout.dialog_list_item, null);

                viewHolder.videoName = (TextView) convertView.findViewById(R.id.vidname);

                viewHolder.videoDescription = (EditText) convertView.findViewById(R.id.edit_video_description);
                // viewHolder.videoName.setText(getItem(position).toString());

                convertView.setTag(viewHolder);

            } else {

                viewHolder = (ListViewHolder) convertView.getTag();

// loadSavedValues();

            }

            viewHolder.videoName.setText(vidnames.get(position));

            viewHolder.videoDescription.setId(position);

            viewHolder.id = position;

            if (selItems != null && selItems.get(position) != null) {

                viewHolder.videoDescription.setText(selItems.get(position));

            } else {

                viewHolder.videoDescription.setText(null);

            }

// Add listener for edit text

            viewHolder.videoDescription

                    .setOnFocusChangeListener(new View.OnFocusChangeListener() {

                        @Override

                        public void onFocusChange(View v, boolean hasFocus) {



/* * When focus is lost save the entered value for

 * later use*/



                            if (!hasFocus) {

                                int itemIndex = v.getId();

                                String enteredPrice = ((EditText) v).getText()

                                        .toString();

                                selItems.put(itemIndex, enteredPrice);

                            }

                        }

                    });

            return convertView;


        }

        public final class ListViewHolder{
            int id;
            TextView videoName;
            EditText videoDescription;

        }
    }



}
