package com.iitb.mobileict.lokavidya.ui;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.iitb.mobileict.lokavidya.Projectfile;
import com.iitb.mobileict.lokavidya.R;
import com.iitb.mobileict.lokavidya.ui.shotview.ViewShots;


import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Projects extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

    }


    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPref.edit();
        editor.putInt("savedView",0);
        editor.commit();

        displayProjects();
        ListView listView = (ListView) findViewById(R.id.ProjectList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                String item = (String) adapter.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), ViewShots.class);
                intent.putExtra("projectname", item);
                startActivity(intent);
            }
        });
    }



    public void toast(String text){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();
    }

    public void ProjectsListView(List<String> myStringArray){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_1, myStringArray);
        ListView listView = (ListView) findViewById(R.id.ProjectList);
        listView.setAdapter(adapter);
    }

    public void displayProjects() {
        Context context = getApplicationContext();
        Projectfile f = new Projectfile(context);
        List<String> myStringArray = f.DisplayProject();
        ProjectsListView(myStringArray);
    }

    public List<String> projectsList() {
        Context context = getApplicationContext();
        Projectfile f = new Projectfile(context);
        List<String> myStringArray = f.DisplayProject();
        return myStringArray;
    }

    public void addProject(String newproject){
        if(newproject == "" || newproject == " ")return ;
        Projectfile f = new Projectfile(getApplicationContext());
        List<String> projects = f.AddNewProject(newproject);
        ProjectsListView(projects);
    }

    public void addProjectCallBack(View v){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.enterName);

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Pattern pattern = Pattern.compile("\\s");
                Matcher matcher = pattern.matcher(input.getText().toString());
                boolean found = matcher.find();

                if(found)
                    Toast.makeText(Projects.this,"Project name should not contain space.",Toast.LENGTH_LONG).show();
                else
                addProject(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void deleteProject(CharSequence name){
        Projectfile f = new Projectfile(getApplicationContext());
        List<String> projects = f.DeleteProject(name);
        ProjectsListView(projects);
    }
    public  void deleteProjectCallBack(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        List<String> projects = projectsList();
      final  CharSequence[] x = projects.toArray(new CharSequence[projects.size()]);
        builder.setTitle("Pick a project to delete")
                .setItems(x, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        toast(Integer.toString(which));
                        deleteProject(x[which]);

                    }
                });
        builder.create().show();
    }

}
