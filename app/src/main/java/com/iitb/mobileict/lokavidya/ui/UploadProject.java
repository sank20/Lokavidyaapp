package com.iitb.mobileict.lokavidya.ui;

import android.app.Activity;
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
import android.widget.Toast;

import com.iitb.mobileict.lokavidya.R;

import java.util.ArrayList;
import java.util.List;

public class UploadProject extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    EditText author;
    EditText videoName;
    EditText description;
    EditText gmail;
    EditText language;
    EditText keywords;

    Spinner category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_info);
        author=(EditText)findViewById(R.id.authornameedittext);
        videoName=(EditText)findViewById(R.id.videonameedittext);
        description=(EditText)findViewById(R.id.videodescription);
        gmail= (EditText) findViewById(R.id.authormailedittext);
        language= (EditText) findViewById(R.id.uploadvideolanguage);
        keywords=(EditText) findViewById(R.id.keywordsedittext);
        category= (Spinner)findViewById(R.id.videocategoryspinner);
        category.setOnItemSelectedListener(this);
        getSupportActionBar().setTitle(getString(R.string.uploadVideo));
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
                if(author.getText().toString().equals("") || videoName.getText().toString().equals("") || description.getText().toString().equals("") || gmail.getText().toString().equals("")|| language.getText().toString().equals("") || keywords.getText().toString().equals(""))  {
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
