package com.iitb.mobileict.lokavidya.ui;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.PopupMenu;
import android.text.InputType;



import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.iitb.mobileict.lokavidya.Projectfile;
import com.iitb.mobileict.lokavidya.R;
import com.iitb.mobileict.lokavidya.Share;
import com.iitb.mobileict.lokavidya.util.Communication;


import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Implementation of the Projects / dashboard activity
 */
public class Projects extends AppCompatActivity  {



    private String importProjectName;
    private String seedpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//----------------------------------------------------------------------------------------------------------------------------
        /*1.Copy the zipped sample project from assets to a temp folder called loktemp.
          2.Extract the zip to the lokavidya folder.
          3.Delete loktemp.
         */
        if (!new File(seedpath + "biogas-st-marathi"+"/").exists()) {
            copyAssets();
            String seed = Environment.getExternalStorageDirectory().getAbsolutePath() + "/loktemp/" + "biogas-st-marathi.zip";
            try {
                ZipFile seedzip = new ZipFile(seed);
                if (!new File(seedpath).isDirectory()) {
                    File f1 = new File(seedpath);
                    f1.mkdir();
                }
                seedzip.extractAll(seedpath);
            } catch (ZipException e) {
                e.printStackTrace();
            }

            File delTemp = new File(seed);
            delTemp.delete();
            delTemp.getParentFile().delete();
        }
//-------------------------------------------------------------------------------------------------------------------------

        /*the following code loads all the folders inside the lokavidya folder and removes zips */
        importProjectName = "";
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


//        View seed= (View)findViewById(R.id.action_sync_seed);
//        registerForContextMenu(seed);
        setContentView(R.layout.activity_projects);


    }


    @Override
    protected void onStart() {
        super.onStart();

        /*shared preferences used to store the project data*/
        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPref.edit();
        editor.putInt("savedView", 0);
        editor.commit();

        //show all the projects in the list
        displayProjects();
        ListView listView = (ListView) findViewById(R.id.ProjectList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listitem, projectsList());
        listView.setAdapter(adapter);
        registerForContextMenu(listView); //for floating context menu (on long click)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                String item = (String) adapter.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), EditProject.class);
                intent.putExtra("projectname", item);
                startActivity(intent);
            }
        });
    }

    /**
     * when the top right download icon is pressed, you get options to select the sample projects to download
     * @param item
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        PopupMenu popup = new PopupMenu(this, findViewById(R.id.action_sync_seed));
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_seed_projects, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.TreadlePump:

                        downloadSeed("Pump-Odiya","odiyapump.zip","http://ruralict.cse.iitb.ac.in/Downloads/lokavidyaProjects/odiyapump.zip");
                        Log.i("seed", "pump");
                        return true;
                    case R.id.biogas:
                        downloadSeed("biogas-st-hindi","biogasSThi.zip","http://ruralict.cse.iitb.ac.in/Downloads/lokavidyaProjects/biogasSThi.zip");
                        Log.i("seed", "biogas");

                        return true;

                }
                return true;
            }
        });

        // getMenuInflater().inflate(R.menu.menu_seed_download,menu);
        /*switch(item.getItemId()){
            case R.id.action_sync_seed:
                if(!new File(seedpath + "Pump-Odiya/").exists()) {
                    Communication.isDownloadComplete =false;
                    Communication.downloadSampleProjects(getThisActivity());
                    Log.i("Downloaded?", String.valueOf(Communication.isDownloadComplete));
                    final ProgressDialog downloadSeed = ProgressDialog.show(this,"please wait","Downloading sample project");
                    downloadSeed.setCancelable(false);
                    downloadSeed.setCanceledOnTouchOutside(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            while (!Communication.isDownloadComplete) {*//*wait till download hasn't completed *//*}


                            String serverseed = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/odiyapump.zip").toString();
                            try {
                                ZipFile seedzip = new ZipFile(serverseed);
                                seedzip.extractAll(seedpath);
                            } catch (ZipException e) {
                                e.printStackTrace();
                            }

                            File delTemp= new File(serverseed);
                            delTemp.delete();
                            //delTemp.getParentFile().delete();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    recreate();
                                    downloadSeed.dismiss();

                                }
                            });



                        }
                    }).start();


                    Log.i("Downloaded?", String.valueOf(Communication.isDownloadComplete));


                }else{
                    Toast.makeText(this,"Seed Project already exists",Toast.LENGTH_SHORT).show();
                }

                //return true;

        }
*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_seed_download, menu);

        //return true;
        return super.onCreateOptionsMenu(menu);
    }


    public void toast(String text) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();
    }

    /**
     *set adapter to listview in projects activity
     * @param myStringArray
     */
    public void ProjectsListView(List<String> myStringArray) {
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

    /**
     * when you need to add new project
     * @param newproject namme of the new project
     */
    public void addProject(String newproject) {
        if (newproject.equals("") || newproject.equals(" "))
            return; //(Sanket P) changed newproject == "" to newproject.equals("").
        Projectfile f = new Projectfile(getApplicationContext());
        List<String> projects = f.AddNewProject(newproject);
        ProjectsListView(projects);
    }

    /**
     * when you press the 'add project' button in the activity
     * @param v view
     */
    public void addProjectCallBack(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.enterName);

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(getString(R.string.OkButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //all the checks and validations for an appropriate project names are performed:

                Pattern pattern1 = Pattern.compile("\\s");
                Pattern pattern2 = Pattern.compile("\\.");
                // Pattern pattern3 = Pattern.compile("");

                //Matcher matcher1 = pattern1.matcher(input.getText().toString());
                Matcher matcher2 = pattern2.matcher(input.getText().toString());
                // Matcher matcher3 = pattern3.matcher(input.getText().toString());

                //boolean found1 = matcher1.find();
                boolean found1 = false;
                boolean found2 = matcher2.find();
                boolean found3 = input.getText().toString().contains("/");
                // boolean found3 = matcher3.find();

                if (input.getText().toString().charAt(0) == ' ' || input.getText().toString().charAt(input.getText().toString().length() - 1) == ' ')
                    found1 = true;

                if (found1)
                    Toast.makeText(Projects.this, getString(R.string.projectNameSpace), Toast.LENGTH_LONG).show();
                else if (found2)
                    Toast.makeText(Projects.this, getString(R.string.projectNameDot), Toast.LENGTH_LONG).show();
                else if (found3)
                    Toast.makeText(Projects.this, "Project name cannot contain '/'", Toast.LENGTH_LONG).show();
                else {
                    if (input.getText().toString().equals("")) {
                        Toast.makeText(Projects.this, getString(R.string.projectNameEmpty), Toast.LENGTH_LONG).show();
                    } else {
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        System.out.print("Entered long press-1");
        if (v.getId()==R.id.ProjectList) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(projectsList().get(info.position));
            String[] menuItems = getResources().getStringArray(R.array.menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        System.out.print("Entered long press-2");
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.menu);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = projectsList().get(info.position);
        if(menuItemName.equals("Delete")){
            deleteProject(listItemName);
        }
        else if(menuItemName.equals("Duplicate Project")){
            duplicateProject(listItemName);
        }
        else{
            Toast.makeText(Projects.this, "Something is wrong", Toast.LENGTH_SHORT).show();
        }
//        Toast.makeText(Projects.this, menuItemName+" + "+listItemName, Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * code for creating a copy of the project. Renaming of the project also implemented in the same
     * @param pname new project name
     */
    public void duplicateProject(final String pname){
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
                String enteredProjectName=input.getText().toString();
                //Matcher matcher1 = pattern1.matcher(input.getText().toString());
                Matcher matcher2 = pattern2.matcher(enteredProjectName);
                // Matcher matcher3 = pattern3.matcher(input.getText().toString());

                //boolean found1 = matcher1.find();
                boolean found1 = false;
                boolean found2 = matcher2.find();
                boolean found3 = enteredProjectName.contains("/");
                // boolean found3 = matcher3.find();

                if (enteredProjectName.charAt(0) == ' ' || enteredProjectName.charAt(enteredProjectName.length() - 1) == ' ')
                    found1 = true;

                if (found1)
                    Toast.makeText(Projects.this, getString(R.string.projectNameSpace), Toast.LENGTH_LONG).show();
                else if (found2)
                    Toast.makeText(Projects.this, getString(R.string.projectNameDot), Toast.LENGTH_LONG).show();
                else if (found3)
                    Toast.makeText(Projects.this, "Project name cannot contain '/'", Toast.LENGTH_LONG).show();
                else {
                    if (input.getText().toString().equals("")) {
                        Toast.makeText(Projects.this, getString(R.string.projectNameEmpty), Toast.LENGTH_LONG).show();
                    }
                    else if(foundInProjectList(enteredProjectName)){
                        Toast.makeText(Projects.this, getString(R.string.projectExists), Toast.LENGTH_LONG).show();
                    }
                    else {
                        copyDuplicateProject(pname,enteredProjectName);
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


    public void copyDuplicateProject(String pname, String newName){
        Projectfile f = new Projectfile(getApplicationContext());
        List<String> projects = f.AddNewProject(newName);
        try {
            f.duplicateContents(pname,newName,getThisActivity(),this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ProjectsListView(projects);
    }
    /**
     * This method is called on click of tutorialButton . It contains just an intent to open an activity containing the VideoView
     * to show the tutorial Video (TutorialVideo.java).
     *
     * @see TutorialVideo
     * //@param v view
     */ //for now it's been discarded//
    /*public void appTutorialCallBack(View v){

        Intent OpenVideo = new Intent(getThisActivity(),TutorialVideo.class);
        Projects.this.startActivity(OpenVideo);



    }*/

    /**
     * dialog for delete project
     * @param name project name
     */
    public void deleteProject(final CharSequence name) {
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
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder1.create().show();


    }

    /**
     * onclick of delete project button
     * @param v
     */
    public void deleteProjectCallBack(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        List<String> projects = projectsList();
        final CharSequence[] x = projects.toArray(new CharSequence[projects.size()]);
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
        if (resultCode == Share.DISCOVER_DURATION) {
            if (requestCode == Share.REQUEST_BLU_VIDEO)
                Share.sendVideo(this, getApplicationContext());
        }
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            final Uri uri = data.getData();
            final Context mContext = this;
            System.out.println("File Uri : " + uri.toString());
//            try {
            String path = uri.getPath();
            System.out.println("FIle path ---------import> " + path);
            final String impProjectName = Share.pathToProjectname(path);
            if (foundInProjectList(impProjectName)) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Projects.this);
                builder1.setTitle("Overwrite existing project with same name?");
                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Projectfile f = new Projectfile(getApplicationContext());
                        List<String> projects = f.DeleteProject(impProjectName);
                        Share.importproject(uri, getThisActivity(), mContext);
                    }
                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder1.create().show();
            } else {
                Share.importproject(uri, getThisActivity(), this);
            }
//            }
//            catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
        }
    }

    boolean foundInProjectList(String project) {
        List<String> projectList = projectsList();
        for (String str : projectList) {
            if (str.equalsIgnoreCase(project))
                return true;
        }
        return false;
    }



    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
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
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static final int FILE_SELECT_CODE = 102;

    /**
     * called for importing project after clicking on 'import'
     * @param v
     */
    public void importProjectCallback(View v) {
        importProjectName = "";
        try {
//            //************************************************************************************************
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle(R.string.enterName);
//            final EditText input = new EditText(this);
//            input.setInputType(InputType.TYPE_CLASS_TEXT);
//            builder.setView(input);
//            builder.setPositiveButton(getString(R.string.OkButton), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    Pattern pattern1 = Pattern.compile("\\s");
//                    Pattern pattern2 = Pattern.compile("\\.");
//                    // Pattern pattern3 = Pattern.compile("");
//
//                    Matcher matcher1 = pattern1.matcher(input.getText().toString());
//                    Matcher matcher2 = pattern2.matcher(input.getText().toString());
//                    // Matcher matcher3 = pattern3.matcher(input.getText().toString());
//
//                    //boolean found1 = matcher1.find();
//                    boolean found1 = false;
//                    boolean found2 = matcher2.find();
//                    // boolean found3 = matcher3.find();
//
//                    if(input.getText().toString().charAt(0) == ' ' || input.getText().toString().charAt(input.getText().toString().length() -1) == ' ' )
//                        found1 = true;
//
//                    if (found1)
//                        Toast.makeText(Projects.this, getString(R.string.projectNameSpace), Toast.LENGTH_LONG).show();
//                    else if (found2)
//                        Toast.makeText(Projects.this, getString(R.string.projectNameDot), Toast.LENGTH_LONG).show();
//                    else {
//                        if (input.getText().toString().equals("")) {
//                            Toast.makeText(Projects.this, getString(R.string.projectNameEmpty), Toast.LENGTH_LONG).show();
//                        } else {
//                            if(foundInProjectList(input.getText().toString())){
//                                Toast.makeText(Projects.this, getString(R.string.projectExists), Toast.LENGTH_LONG).show();
//                            }
//                            else{
//                                importProjectName = input.getText().toString();
//                                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//                                i.setType("*/*");
//                                i.addCategory(Intent.CATEGORY_OPENABLE);
//                                startActivityForResult(
//                                        Intent.createChooser(i, getString(R.string.selectProjectToImport)), FILE_SELECT_CODE
//                                );
//                            }
//                        }
//                    }
//                }
//            });
//            builder.setNegativeButton(getString(R.string.CancelButton), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            builder.show();
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("application/zip");
            i.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(
                    Intent.createChooser(i, getString(R.string.selectProjectToImport)), FILE_SELECT_CODE
            );
            //************************************************************************************************
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, getString(R.string.NoFileManager), Toast.LENGTH_SHORT).show();
        }
    }

    public Activity getThisActivity() {
        return this;
    }
//<<<<<<< HEAD


    /**
     * This method is used for transferring the sample project to be displayed after the installation, from assets to a temporary file called
     * loktemp.
     */
    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        /*try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }*/
        // for(String filename : files) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open("biogas-st-marathi.zip");


            String out1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/loktemp/";
            if (!new File(out1).isDirectory()) {
                File f1 = new File(out1);
                f1.mkdir();
            }
            File outFile = new File(out1 + "biogas-st-marathi.zip");
            Log.i("output file", outFile.toString());


            out = new FileOutputStream(outFile);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (IOException e) {
            Log.e("tag", "Failed to copy asset file: " + "testseedproject", e);
            e.printStackTrace();
        }
        //}
    }

    /**
     * this method is called by copyAssets() method just to perform the writing into the output buffer.
     *
     * @param in
     * @param out
     * @throws IOException
     */
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[5120];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    /**
     * method which calls the communication method for the given project zip and performs other checks
     * @param Projectname name of the project
     * @param zipname name of the project zip file
     * @param link link to the server
     */
    public void downloadSeed(String Projectname, final String zipname, String link) {

        if (!new File(seedpath + Projectname+"/").exists()) {
            Communication.isDownloadComplete = false;
            Communication.downloadSampleProjects(getThisActivity(),link,zipname);
            Log.i("Downloaded?", String.valueOf(Communication.isDownloadComplete));
            final ProgressDialog downloadSeed = ProgressDialog.show(this, getString(R.string.stitchingProcessTitle), getString(R.string.seedDownloadProgress));
            downloadSeed.setCancelable(false);
            downloadSeed.setCanceledOnTouchOutside(false);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!Communication.isDownloadComplete) {/*wait till download hasn't completed */}


                    String serverseed = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/"+ zipname).toString();
                    try {
                        ZipFile seedzip = new ZipFile(serverseed);
                        seedzip.extractAll(seedpath);
                    } catch (ZipException e) {
                        e.printStackTrace();
                    }

                    File delTemp = new File(serverseed);
                    delTemp.delete();
                    //delTemp.getParentFile().delete();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            recreate();
                            downloadSeed.dismiss();

                        }
                    });


                }
            }).start();

        }


    }
}

    