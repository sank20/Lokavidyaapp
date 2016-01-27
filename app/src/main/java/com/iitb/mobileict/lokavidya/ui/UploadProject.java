package com.iitb.mobileict.lokavidya.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.iitb.mobileict.lokavidya.R;

import java.util.ArrayList;
import java.util.List;

public class UploadProject extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    EditText description;
    EditText language;
    EditText keywords;
    String projectname;
    TextView projnamelabel;
    Spinner category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_info);
        Intent in= getIntent();
        projectname= in.getStringExtra("PROJECT_NAME");
        Log.i("UPLOAD-proj name",projectname);
        description=(EditText)findViewById(R.id.videodescription);
        language= (EditText) findViewById(R.id.uploadvideolanguage);
        keywords=(EditText) findViewById(R.id.keywordsedittext);
        category= (Spinner)findViewById(R.id.videocategoryspinner);
        projnamelabel= (TextView) findViewById(R.id.upload_video_name_label);
        category.setOnItemSelectedListener(this);
        projnamelabel.setText(projectname);

        if(getActionBar()!=null) {
            getSupportActionBar().setTitle(getString(R.string.uploadVideo));
        }
        List<String> categories= new ArrayList<String>();
        categories.add("agri");
        categories.add("art");
        categories.add("skill");
        categories.add("blah");
        categories.add("others");

        ArrayAdapter<String> dataAdapter= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,categories);

        category.setAdapter(dataAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.project_info_actionbar_buttons,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.uploadvideobutton:
                if( description.getText().toString().equals("") || language.getText().toString().equals("") || keywords.getText().toString().equals(""))  {
                    Toast.makeText(getApplicationContext(),getString(R.string.cannotupload),Toast.LENGTH_SHORT).show();
                }else{
                    Log.i("upload video","upload action button pressed");

                }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
