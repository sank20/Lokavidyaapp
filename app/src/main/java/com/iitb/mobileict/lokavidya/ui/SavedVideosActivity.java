package com.iitb.mobileict.lokavidya.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iitb.mobileict.lokavidya.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SavedVideosActivity extends AppCompatActivity {

    private final String savedvideosDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Lokavidya Videos/";
    List<String> savedvidList;
    ListView savedListView;
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
                ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.activity_video_player_content, R.id.textView_video_title,savedvidList);
                savedListView.setAdapter(arrayAdapter);
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
                File savedlist[] = savedVid.listFiles();
                for(File f : savedlist){
                    if(parent.getItemAtPosition(position).toString().equals(f.getName().substring(0,f.getName().length()-11))){
                        Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW);
                        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(f).toString());
                        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        myIntent.setDataAndType(Uri.fromFile(f),mimetype);
                        startActivity(myIntent);

                    }
                }

            }
        });
        
    }
}
