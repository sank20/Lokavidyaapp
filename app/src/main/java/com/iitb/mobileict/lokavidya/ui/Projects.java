package com.iitb.mobileict.lokavidya.ui;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.InputType;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.iitb.mobileict.lokavidya.Projectfile;
import com.iitb.mobileict.lokavidya.R;
import com.iitb.mobileict.lokavidya.Share;
import com.iitb.mobileict.lokavidya.ui.shotview.ViewShots;


import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Projects extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();
        Projectfile f = new Projectfile(context);
        List<String> projectsList = f.DisplayProject_with_zips();
        for (int i = 0; i < projectsList.size(); i++) {
            System.out.println("--------------projects : " + projectsList.get(i));
            if (projectsList.get(i).length() < 4) continue;
            if (projectsList.get(i).substring(projectsList.get(i).length() - 4).equals(".zip")) {
                System.out.println("-" + projectsList.get(i).substring(projectsList.get(i).length() - 4) + ":inside");
                File delete_file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya/" + projectsList.get(i));
                delete_file.delete();
            }
        }
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
    public void deleteProjectCallBack(View v){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Share.DISCOVER_DURATION ) {
            if(requestCode == Share.REQUEST_BLU_VIDEO)Share.sendVideo(this, getApplicationContext());
        }
        if(requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK){
            Uri uri = data.getData();
            //System.out.println("File Uri : "+ uri.toString());
            try {
                String path = getPath(this, uri);
                //System.out.println("FIle path ---------projects.java> " + path);
                Share.importproject(path);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
    public static final int FILE_SELECT_CODE = 102;
    public void importProjectCallback(View v){

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("*/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        try{
            startActivityForResult(
                    Intent.createChooser(i, "Select project file to import") , FILE_SELECT_CODE
            );
        }catch(android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager to import", Toast.LENGTH_SHORT).show();
        }
    }
    public  void shareCallBack(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final List<String> projects = projectsList();
        final  CharSequence[] x = projects.toArray(new CharSequence[projects.size()]);
        builder.setTitle("Pick a project to share")
                .setItems(x, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //toast(Integer.toString(which));
                        //******************************************
                        //deleteProject(x[which]);
                        //dialog.dismiss();
                        //final AlertDialog alert;
                        AlertDialog.Builder builderShare = new AlertDialog.Builder(Projects.this);
                        //alert=builderShare.create();
                        List<String> features = new ArrayList<String>();
                        features.add("Share video");
                        features.add("Share project");
                        features.add("Share via");
                        final int whichUse = which;
                        final  CharSequence[] y = features.toArray(new CharSequence[features.size()]);
                        builderShare.setTitle("Pick a project to delete")
                                .setItems(y, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogShare, int whichShare) {
                                        //toast(Integer.toString(whichShare));
                                        //deleteProject(y[whichShare]);
                                        //share(mContext,x[which],y[whichShare]);
                                        toast(y[whichShare]+" "+x[whichUse]);
                                        String projectname = x[whichUse].toString();
                                        System.out.println(projectname + whichShare);
                                        Share.SendOptions(whichShare, getThisActivity(), getApplicationContext(),projectname);
                                    }
                                });
                        builderShare.create().show();
                    }
                });
        builder.create().show();
    }
    public Activity getThisActivity(){
        return this;
    }

}
