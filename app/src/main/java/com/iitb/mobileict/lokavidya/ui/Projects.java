package com.iitb.mobileict.lokavidya.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.iitb.mobileict.lokavidya.Communication.Communication;
import com.iitb.mobileict.lokavidya.Projectfile;
import com.iitb.mobileict.lokavidya.QRCode.QRScanner;
import com.iitb.mobileict.lokavidya.R;
import com.iitb.mobileict.lokavidya.Share;
import com.iitb.mobileict.lokavidya.util.animations;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of the Projects / dashboard activity
 */
public class Projects extends FragmentActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    private static final int PREFERENCE_MODE_PRIVATE = 0;
    private String importProjectName;
    private String seedpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya/";
    GoogleApiClient mGoogleApiClient;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private TextView mdrawerprofile;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;
    private LinearLayout drawerLinearLayout;

    public static Boolean isFabOpen = false;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;

    private Button fabAddButton,fabImportButton;
    private FloatingActionButton fabadd,fabmain,fabimport;
    String loktemp = Environment.getExternalStorageDirectory().getAbsolutePath() + "/loktemp/" ;
    private ListView sampleProjectList;
    private Dialog dialog;
    Window window;
    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    Point size;
    WindowManager windowManager;
    Display display;
    int width,height,dialogid,alertdialogid;
    ArrayList<String> zipNameArrayList;
    final HashMap<String,String> sampleprojectsHashmap =new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
//----------------------------------------------------------------------------------------------------------------------------
        /*1.Copy the zipped sample project from assets to a temp folder called loktemp.
          2.Extract the zip to the lokavidya folder.
          3.Delete loktemp.
         */
        if (!new File(seedpath + "biogas-st-marathi"+"/").exists()) {
            copyAssets();
            try {
                ZipFile seedzip = new ZipFile(loktemp+ "biogas-st-marathi.zip");
                if (!new File(seedpath).isDirectory()) {
                    File f1 = new File(seedpath);
                    f1.mkdir();
                }
                seedzip.extractAll(seedpath);
            } catch (ZipException e) {
                e.printStackTrace();
            }

            File delTemp = new File(loktemp + "biogas-st-marathi.zip");
            delTemp.delete();
            delTemp.getParentFile().delete();
        }
//-------------------------------------------------------------------------------------------------------------------------

        /*the following code loads all the folders inside the lokavidya folder and removes zips */
        importProjectName = "";
        Context context = getApplicationContext();
        //TODO check for permissions here
        Projectfile f = new Projectfile(context);
        List<String> projectsList = f.DisplayProject_with_zips();


        for (int i = 0; i < projectsList.size(); i++) {
            System.out.println("--------------projects : " + projectsList.get(i));

            if(new File(seedpath+projectsList.get(i)+File.separator).isDirectory()) {
                File tmp_images = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya/" + projectsList.get(i) + "/tmp_images");
                if (!tmp_images.exists()) {
                    Log.i("projectlist", "tmp_images not found, making dir");
                    tmp_images.mkdir();
                    Log.i("tmp_images", "tmp_images folder created");
                    Share.tmp_images_make(projectsList.get(i));
                }else if(tmp_images.list().length == 0){
                    Log.i("tmp_images","tmp_images is empty");
                    Share.tmp_images_make(projectsList.get(i));
                }
            }

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


        //------------------------------------------F---------------A---------------B--------------------------------------------------

        fabadd= (FloatingActionButton) findViewById(R.id.fab_add);
        fabimport= (FloatingActionButton) findViewById(R.id.fab_import);
        fabmain = (FloatingActionButton) findViewById(R.id.fab_main);


        fabmain.setOnClickListener(this);
        fabadd.setOnClickListener(this);
        fabimport.setOnClickListener(this);

        fabAddButton=(Button)findViewById(R.id.fabAddbutton);
        fabImportButton=(Button)findViewById(R.id.fabImportButton);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
//---------------------------------------------------------------------------------------------------------------------------

        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.drawer_list_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mdrawerprofile= (TextView) findViewById(R.id.drawer_profile_textview);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String emailid = sharedPref.getString("emailid","Guest");
        mdrawerprofile.setText(emailid);

        drawerLinearLayout=(LinearLayout)findViewById(R.id.drawer_linear_layout);
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //ImageView drawerImg = (ImageView) findViewById(R.id.drawer_image);
        //drawerImg.setImageResource(R.drawable.fuckthat);
        // enable ActionBar app icon to behave as action to toggle nav drawer
        /*TextView drawertext=(TextView)findViewById(R.id.drawer_textview);
        drawertext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(),"text clicked!!",Toast.LENGTH_SHORT).show();
            }
        });*/

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                if(isFabOpen)
                    animations.animateFAB(isFabOpen, fabmain, fabadd, fabimport, rotate_forward, rotate_backward, fab_open, fab_close, fabAddButton, fabImportButton);

            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }






    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id){
            case R.id.fab_main:

                animations.animateFAB(isFabOpen,fabmain,fabadd,fabimport,rotate_forward,rotate_backward,fab_open,fab_close,fabAddButton,fabImportButton);
                break;
            case R.id.fab_add:

                Log.d("FAB", "ADD");
                addProjectCallBack(v);
                animations.animateFAB(isFabOpen, fabmain, fabadd, fabimport, rotate_forward, rotate_backward, fab_open, fab_close, fabAddButton, fabImportButton);

                break;
            case R.id.fab_import:

                Log.d("FAB", "Import");
                importProjectCallback(v);
                animations.animateFAB(isFabOpen, fabmain, fabadd, fabimport, rotate_forward, rotate_backward, fab_open, fab_close, fabAddButton, fabImportButton);

                break;
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isFabOpen)
            animations.animateFAB(isFabOpen, fabmain, fabadd, fabimport, rotate_forward, rotate_backward, fab_open, fab_close, fabAddButton, fabImportButton);

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

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(drawerLinearLayout);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    /**
     * when the top right download icon is pressed, you get options to select the sample projects to download
     * @param item
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


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
     * parse projects.txt and get the list
     *
     */
    public void getSeedProjectList(){

        zipNameArrayList = new ArrayList<String>();

       /* if(!(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"projects.txt").exists())) {
            Communication.downloadseedList(getApplication());
        }
        final ProgressDialog downloadSeed = ProgressDialog.show(this, getString(R.string.stitchingProcessTitle),"");
        downloadSeed.setCancelable(false);
        downloadSeed.setCanceledOnTouchOutside(false);*/

        /*new Thread(new Runnable() {
            @Override
            public void run() {
        */      /*  while (!(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"projects.txt").exists())) {*//*wait till download hasn't completed *//*
                    try{
                        Thread.sleep(1000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Log.i("downloading txt","waiting");
                }
*/

        //TODO create an asynctask which does the below stuff and put the code in openDialog() in onPostExecute()

               // new DownloadSampleProjectTask().execute();
                File suicidebomber= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"projects.txt");
               // suicidebomber.delete();


                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // recreate();
                       // downloadSeed.dismiss();

                    }
                });
            }
        }).start();*/

        /*try{
            Thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }*/

        //return sampleprojectsHashmap;

                /*try{
                    Thread.sleep(3000);
                }catch (Exception e){
                    e.printStackTrace();
                }
*/



        // downloadSeed.dismiss();

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

                if (input.getText().toString().equals("")) {
                    Toast.makeText(Projects.this, getString(R.string.projectNameEmpty), Toast.LENGTH_LONG).show();
                }
                else if (input.getText().toString().charAt(0) == ' ' || input.getText().toString().endsWith(" "))
                    found1 = true;
                if (found1)
                    Toast.makeText(Projects.this, getString(R.string.projectNameSpace), Toast.LENGTH_LONG).show();
                else if (found2)
                    Toast.makeText(Projects.this, getString(R.string.projectNameDot), Toast.LENGTH_LONG).show();
                else if (found3)
                    Toast.makeText(Projects.this, "Project name cannot contain '/'", Toast.LENGTH_LONG).show();
                else {
                    addProject(input.getText().toString());


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
            menu.setHeaderTitle(projectsList().get(((AdapterView.AdapterContextMenuInfo) menuInfo).position));
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
        if(menuItemName.equals(getString(R.string.delete))){
            deleteProject(listItemName);
        }
        else if(menuItemName.equals(getString(R.string.long_press_duplicate_proj))){
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
                        recreate();
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
            String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bluetooth/";
//-----------------------------------------------------------------------------------------------------------




            InputStream is = null;
            try {
                is =  getContentResolver().openInputStream(uri);
                Projectfile.copyFileFromInputstream(is, loktemp +impProjectName+".zip");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }





            //-----------------------------------------------------------------------------------------------------------

            Log.i("Import","after pathtoprojectname: "+ impProjectName);
            if (foundInProjectList(impProjectName)) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Projects.this);
                builder1.setTitle(R.string.import_overwrite);
                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Projectfile f = new Projectfile(getApplicationContext());
                        List<String> projects = f.DeleteProject(impProjectName);
                        Share.importproject(uri, getThisActivity(), mContext,loktemp +impProjectName+".zip",impProjectName);
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
                Share.importproject(uri, getThisActivity(), this,loktemp +impProjectName+".zip",impProjectName);
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
    public void downloadSeed(Context context,String Projectname, final String zipname, String link) {

        if (!new File(seedpath + Projectname+"/").exists()) {
            Communication.isDownloadComplete = false;
            Communication.downloadSampleProjects(context,link,zipname);
            Log.i("Downloaded?", String.valueOf(Communication.isDownloadComplete));
            final ProgressDialog downloadSeed = ProgressDialog.show(context, getString(R.string.stitchingProcessTitle), getString(R.string.seedDownloadProgress));
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
                            dialog.dismiss();

                        }
                    });


                }
            }).start();

        }


    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            System.out.println("------------------------------Drawer list item click "+ position+"-----------------------------");
            selectItem(position);

        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        switch(position){

            case 1:
                System.out.println("------------------------------selectitem item click "+ position+"-------------------------------");

                //showSampleProjectPopup();
                if(isNetworkAvailable(getApplicationContext())) {
                    //openDialog();
                    new DownloadSampleProjectTask().execute();
                }
                else{
                    Toast.makeText(this,"Please connect to internet",Toast.LENGTH_SHORT).show();
                }
                break;

            case 0:
                Fragment fragment = new ProjectFragment();
                Bundle args = new Bundle();
                args.putInt("drawer_list_array", position);
                fragment.setArguments(args);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                // update selected item and title, then close the drawer
                mDrawerList.setItemChecked(position, true);
                setTitle(mPlanetTitles[position]);
                mDrawerLayout.closeDrawer(drawerLinearLayout);
                break;

            case 2:
                if(isNetworkAvailable(getApplicationContext())) {
                    Intent viewVid = new Intent(this, BrowseAndViewVideos.class);
                    startActivity(viewVid);
                }
                else{
                    Toast.makeText(this,"Please connect to internet",Toast.LENGTH_SHORT).show();
                }
                break;
/*
            case 3:
                //TODO call the activity for QR code scanning
                Intent scan = new Intent(Projects.this, QRScanner.class);
                startActivity(scan);
                break;*/

            case 3:
                //TODO start an activity which shows the list of videos saved in /downloads/lokavidya videos and play them on clicking
                Intent in = new Intent(Projects.this,SavedVideosActivity.class);
                startActivity(in);
                break;


            case 5:
                signOut();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sharedPreferences.edit().remove("lokavidyaToken").commit();
                sharedPreferences.edit().remove("idToken").commit();
                sharedPreferences.edit().remove("emailid").commit();
                Intent k= new Intent(this,LokavidyaAuthenticationActivity.class);
                startActivity(k);
                finish();
                break;
            case 4:
                Intent savedvid = new Intent(this,FeedbackActivity.class);
                startActivity(savedvid);

        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    public void showSampleProjectPopup(){
        System.out.println("------------------------------inside showsampleprojectpopup item click -------------------------------");

        PopupMenu popup = new PopupMenu(this , findViewById(R.id.drawelistitem));
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_seed_projects, popup.getMenu());

        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.TreadlePump:

                        downloadSeed(Projects.this,"Pump-Odiya", "odiyapump.zip", "http://ruralict.cse.iitb.ac.in/Downloads/lokavidyaProjects/odiyapump.zip");
                        Log.i("seed", "pump");
                        return true;
                    case R.id.biogas:
                        downloadSeed(Projects.this,"biogas-st-hindi", "biogasSThi.zip", "http://ruralict.cse.iitb.ac.in/Downloads/lokavidyaProjects/biogasSThi.zip");
                        Log.i("seed", "biogas");

                        return true;


                }
                return true;
            }
        });
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class ProjectFragment extends Fragment {
        public static final String ARG_PLANET_NUMBER = "planet_number";

        public ProjectFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            // Toast.makeText(getActivity(),"YAY",Toast.LENGTH_SHORT).show();
            int i = getArguments().getInt("drawer_list_array");
            String option = getResources().getStringArray(R.array.drawer_list_array)[i];
            Log.i("ProjectFragment", option + " at position " + Integer.toString(i) + " selected");


            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    class DownloadSampleProjectTask extends AsyncTask<Void,Void,Void>{


        @Override
        protected Void doInBackground(Void... params) {
            try {
                zipNameArrayList = new ArrayList<String>();

                //BufferedReader br = new BufferedReader(new FileReader(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+"projects.txt"));
                URL url = new URL("http://ruralict.cse.iitb.ac.in/Downloads/lokavidyaProjects/project.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    Log.i("Sample proj txt: ",line);
                    String name=line.split(":")[0];
                    Log.i("Sample proj txt name",name);
                    String link=line.split(":")[1];
                    Log.i("Samplr proj txt link:",link);
                    sampleprojectsHashmap.put(name, link);
                    Log.i("hashmap split 0 and 1", line.split(":")[0] + "," + line.split(":")[1]);

                    int pos = link.lastIndexOf("/");

                    String zipname =link.substring(pos + 1, link.length() );
                    zipname= URLDecoder.decode(zipname, "UTF-8");
                    Log.i("txt parse zipname",zipname);

                    zipNameArrayList.add(zipname);
                    sb.append(line);
                    // sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                //String projitem= sampleprojectsHashmap.get(0);

                String everything = sb.toString();
                System.out.println("------------------------------seed text file contents:\n " + everything + "\n" + "arraylist 0:" + sampleprojectsHashmap.get(0));
            }
            catch (IOException e){
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            openDialog();
        }
    }

    public void openDialog()
    {

        //HashMap<String,String> chaddi= getSeedProjectList();
        //getSeedProjectList();
        final List<String> lungi= new ArrayList<>(sampleprojectsHashmap.keySet());



        for(String s : lungi){
            System.out.println(" lungi number "+ lungi.indexOf(s) +"="+s);
        }



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,lungi );

        dialog = new Dialog(Projects.this);

        LayoutInflater inflater = getLayoutInflater();
        final View Layout = inflater.inflate(R.layout.sample_project_list, (ViewGroup) findViewById(R.id.sampleProjList), false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(Layout);
        sampleProjectList=(ListView) Layout.findViewById(R.id.sampleProjList);
        sampleProjectList.setAdapter(adapter);

        sampleProjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                String item = (String) adapter.getItemAtPosition(position);
                System.out.println("sample projects item-----------------------------------:" + item);
                int pos= lungi.indexOf(item);
                downloadSeed(Projects.this,item,zipNameArrayList.get(pos),"http://"+sampleprojectsHashmap.get(item));

            }
        });
        /*TextView totalBill = (TextView)Layout.findViewById(R.id.cancel_order_total_bill);
        totalBill.setText(""+order.totalBill);
*/

        window =dialog.getWindow();

        size = new Point();
        windowManager = getWindowManager();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
            windowManager.getDefaultDisplay().getSize(size);

            width = size.x;
            height = size.y;
        }else{
            display = windowManager.getDefaultDisplay();
            width = display.getWidth();
            height = display.getHeight();
        }



        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            lp.width=(int)(width/1.125);
            lp.height = (int)(height/1.5);


        } else {
            lp.width=width/2;
            lp.height = (int)(height/1.25);


        }



        lp.gravity = Gravity.CENTER;

        window.setAttributes(lp);

        dialog.setCancelable(true);
        dialog.show();
    }



    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

}

    