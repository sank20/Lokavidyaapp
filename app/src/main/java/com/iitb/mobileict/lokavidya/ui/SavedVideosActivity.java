package com.iitb.mobileict.lokavidya.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.iitb.mobileict.lokavidya.Projectfile;
import com.iitb.mobileict.lokavidya.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SavedVideosActivity extends AppCompatActivity {

    private final String savedvideosDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Lokavidya Videos/";
    List<String> savedvidList;
    ListView savedListView;
    private ActionMode mActionMode;
    SavedVideoListAdapter savedVideoListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_videos);
        savedListView = (ListView) findViewById(R.id.savedVideosListView);

        savedvidList= new ArrayList<String>();
        final File savedVid = new File(savedvideosDir);
        if(savedVid.exists()) {
            File savedlist[] = savedVid.listFiles();
            if (savedlist.length > 0) {
                for (File f : savedlist) {
                    System.out.println(f.getName());
                    savedvidList.add(f.getName().substring(0,f.getName().length()-11));
                }
                savedVideoListAdapter = new SavedVideoListAdapter(this, R.layout.activity_video_player_content,savedvidList);
                savedListView.setAdapter(savedVideoListAdapter);

            }else{
                Toast.makeText(getApplicationContext(),"No saved videos exist",Toast.LENGTH_SHORT).show();
                finish();
            }
        }else{
            Toast.makeText(getApplicationContext(),"No saved videos exist",Toast.LENGTH_SHORT).show();
            finish();
        }

        savedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mActionMode== null) {
                    File savedlist[] = savedVid.listFiles();
                    for (File f : savedlist) {
                        if (parent.getItemAtPosition(position).toString().equals(f.getName().substring(0, f.getName().length() - 11))) {
                            Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW);
                            String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(f).toString());
                            String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                            myIntent.setDataAndType(Uri.fromFile(f), mimetype);
                            startActivity(myIntent);

                        }
                    }

                }else{
                    onListItemSelect(position);
                }
            }
        });

        savedListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemSelect(position);
                return true;
            }
        });

    }

    private void onListItemSelect(int position) {
        savedVideoListAdapter.toggleSelection(position);
        boolean hasCheckedItems = savedVideoListAdapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null)
            // there are some selected items, start the actionMode
            mActionMode = startActionMode(new ActionModeCallback());
        else if (!hasCheckedItems && mActionMode != null)
            // there no selected items, finish the actionMode
            mActionMode.finish();

        if (mActionMode != null)
            mActionMode.setTitle(String.valueOf(savedVideoListAdapter
                    .getSelectedCount()) + " selected");
    }
    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // inflate contextual menu
            mode.getMenuInflater().inflate(R.menu.menu_delete_saved_video, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }



        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.menu_delete:
                    // retrieve selected items and delete them out
                    final SparseBooleanArray selected = savedVideoListAdapter
                            .getSelectedIds();

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SavedVideosActivity.this);
                    builder1.setTitle(getString(R.string.deleteConfirmation));
                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            Log.i("Delete video","deleting selected video. sparesebool array: size- " + selected.size()+"first value:"+ selected.valueAt(0) );
                            for (int i = (selected.size() - 1); i >= 0; i--) {
                                if (selected.valueAt(i)) {
                                    Log.i("Delete video", "found the item to delete");

                                    String selectedItem = savedVideoListAdapter
                                            .getItem(selected.keyAt(i));
                                    savedVideoListAdapter.remove(selectedItem);
                                    Log.i("Delete video", "removed from list");

                                    File savedfile = new File(savedvideosDir);
                                    File savedlist[] = savedfile.listFiles();
                                    for (File f : savedlist) {
                                        if (selectedItem.equals(f.getName().substring(0, f.getName().length() - 11))) {
                                            f.delete();
                                            Log.i("Delete video", "file deleted from system");


                                        }
                                    }
                                }
                            }


                        }
                    })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    builder1.create().show();

                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }


        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // remove selection
            savedVideoListAdapter.removeSelection();
            mActionMode = null;
        }
    }
}



class SavedVideoListAdapter extends ArrayAdapter<String> {

    Activity context;
    List<String> laptops;
    private SparseBooleanArray mSelectedItemsIds;

    public SavedVideoListAdapter(Activity context, int resId, List<String> laptops) {
        super(context, resId, laptops);
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.laptops = laptops;
    }

    private class ViewHolder {
        TextView laptopTxt;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate( R.layout.activity_video_player_content, null);
            holder = new ViewHolder();
            holder.laptopTxt = (TextView) convertView
                    .findViewById(R.id.textView_video_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String laptop = getItem(position);
        holder.laptopTxt.setText(laptop);
        convertView
                .setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4
                        : Color.TRANSPARENT);

        return convertView;
    }

    @Override
    public void add(String laptop) {
        laptops.add(laptop);
        notifyDataSetChanged();
        //Toast.makeText(context, laptops.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void remove(String object) {
        // super.remove(object);
        laptops.remove(object);
        notifyDataSetChanged();

    }

    public List<String> getLaptops() {
        return laptops;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
