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

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.iitb.mobileict.lokavidya.Projectfile;
import com.iitb.mobileict.lokavidya.R;
import com.iitb.mobileict.lokavidya.Share;


import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Projects extends Activity {
    private String importProjectName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        importProjectName="";
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
                Intent intent = new Intent(getApplicationContext(), EditProject.class);
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
        if(newproject.equals("") || newproject.equals(" "))   return; //(Sanket P) changed newproject == "" to newproject.equals("").
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

        builder.setPositiveButton(getString(R.string.OkButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Pattern pattern1 = Pattern.compile("\\s");
                Pattern pattern2 = Pattern.compile("\\.");
               // Pattern pattern3 = Pattern.compile("");

                Matcher matcher1 = pattern1.matcher(input.getText().toString());
                Matcher matcher2 = pattern2.matcher(input.getText().toString());
               // Matcher matcher3 = pattern3.matcher(input.getText().toString());

                boolean found1 = matcher1.find();
                boolean found2 = matcher2.find();
               // boolean found3 = matcher3.find();


                if(found1)
                    Toast.makeText(Projects.this,getString(R.string.projectNameSpace),Toast.LENGTH_LONG).show();
                else if(found2)
                    Toast.makeText(Projects.this,getString(R.string.projectNameDot),Toast.LENGTH_LONG).show();
               /* else if(found3)
                    Toast.makeText(Projects.this,"Project name cannot be empty.",Toast.LENGTH_LONG).show();
*/
                else {
                    if(input.getText().toString().equals("")){
                        Toast.makeText(Projects.this,getString(R.string.projectNameEmpty),Toast.LENGTH_LONG).show();
                    }else {
                        addProject(input.getText().toString());
                    }
                }
            }
        });
        builder.setNegativeButton(getString(R.string.CancelButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void deleteProject(final CharSequence name){
        System.out.println("Outside dialog box");

//    final CharSequence name1 = name;
        AlertDialog.Builder builder1 = new AlertDialog.Builder(Projects.this);
        builder1.setTitle(getString(R.string.deleteConfirmation));
        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Projectfile f = new Projectfile(getApplicationContext());
                System.out.println("Inside fialog box");
                List<String> projects = f.DeleteProject(name);
                ProjectsListView(projects);
            }
        })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder1.create().show();


    }
    public void deleteProjectCallBack(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        List<String> projects = projectsList();
        final  CharSequence[] x = projects.toArray(new CharSequence[projects.size()]);
        builder.setTitle(getString(R.string.deleteProjectPickerDialog))
                .setItems(x, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //toast(Integer.toString(which));
                        deleteProject(x[which]);

                    }
                });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Share.DISCOVER_DURATION ) {
            if(requestCode == Share.REQUEST_BLU_VIDEO) Share.sendVideo(this, getApplicationContext());
        }
        if(requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK){
            Uri uri = data.getData();
            System.out.println("File Uri : "+ uri.toString());
//            try {
            String path = uri.getPath();
            System.out.println("FIle path ---------import> " + path);
            Share.importproject(uri,this,importProjectName);
//            }
//            catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
        }
    }
    boolean foundInProjectList(String project){
        List<String> projectList = projectsList();
        for(String str: projectList) {
            if(str.equalsIgnoreCase(project))
                return true;
        }
        return false;
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
        importProjectName="";
        try{
            //************************************************************************************************
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.enterName);
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton(getString(R.string.OkButton), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Pattern pattern1 = Pattern.compile("\\s");
                    Pattern pattern2 = Pattern.compile("\\.");
                    // Pattern pattern3 = Pattern.compile("");
                    Matcher matcher1 = pattern1.matcher(input.getText().toString());
                    Matcher matcher2 = pattern2.matcher(input.getText().toString());
                    // Matcher matcher3 = pattern3.matcher(input.getText().toString());
                    boolean found1 = matcher1.find();
                    boolean found2 = matcher2.find();
                    // boolean found3 = matcher3.find();
                    if (found1)
                        Toast.makeText(Projects.this, getString(R.string.projectNameSpace), Toast.LENGTH_LONG).show();
                    else if (found2)
                        Toast.makeText(Projects.this, getString(R.string.projectNameDot), Toast.LENGTH_LONG).show();
                    else {
                        if (input.getText().toString().equals("")) {
                            Toast.makeText(Projects.this, getString(R.string.projectNameEmpty), Toast.LENGTH_LONG).show();
                        } else {
                            if(foundInProjectList(input.getText().toString())){
                                Toast.makeText(Projects.this, getString(R.string.projectExists), Toast.LENGTH_LONG).show();
                            }
                            else{
                                importProjectName = input.getText().toString();
                                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                                i.setType("*/*");
                                i.addCategory(Intent.CATEGORY_OPENABLE);
                                startActivityForResult(
                                        Intent.createChooser(i, getString(R.string.selectProjectToImport)), FILE_SELECT_CODE
                                );
                            }
                        }
                    }
                }
            });
            builder.setNegativeButton(getString(R.string.CancelButton), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
            //************************************************************************************************
        }catch(android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, getString(R.string.NoFileManager), Toast.LENGTH_SHORT).show();
        }
    }

    public  void shareCallBack(View v){
        final String sharevid = getString(R.string.shareVideo);
        final String shareproj = getString(R.string.shareProject);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final List<String> projects = projectsList();
        final  CharSequence[] x = projects.toArray(new CharSequence[projects.size()]);
        builder.setTitle(R.string.shareDialog)
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
                        //features.add(sharevid);
                        features.add(sharevid);
                        features.add(shareproj);
                        //features.add("Share via");

                        final int whichUse = which;
                        final  CharSequence[] y = features.toArray(new CharSequence[features.size()]);
                        builderShare.setTitle(R.string.whatShareDialog)
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
