package com.iitb.mobileict.lokavidya.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
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

import com.iitb.mobileict.lokavidya.Communication.Settings;
import com.iitb.mobileict.lokavidya.Communication.postmanCommunication;
import com.iitb.mobileict.lokavidya.R;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadProject extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText description;
    EditText language;
    EditText keywords;
    String projectname;
    TextView projnamelabel;
    Spinner category;
    JSONArray categoriesJSONArray;
    Map<String,Integer> name2Id= new HashMap<String,Integer>();
    static String UPLOAD_URL = "http://"+ Settings.serverURL+"/api/tutorials/upload"; //TODO add the URL here
    Projects p; //for calling isNetworkAvailable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        p= new Projects();// for calling isNetworkAvailable
        setContentView(R.layout.activity_upload_video);
        Intent in = getIntent();
        projectname = in.getStringExtra("PROJECT_NAME");
        Log.i("UPLOAD-proj name", projectname);
        description = (EditText) findViewById(R.id.videodescription);
        language = (EditText) findViewById(R.id.uploadvideolanguage);
        keywords = (EditText) findViewById(R.id.keywordsedittext);
        category = (Spinner) findViewById(R.id.videocategoryspinner);
        projnamelabel = (TextView) findViewById(R.id.upload_video_name_label);
        //category.setOnItemSelectedListener(this);
        projnamelabel.setText(projectname);

        if (getActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.uploadVideo));
        }
        new getCategoryList(UploadProject.this).execute();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.upload_video_actionbar_button, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.uploadvideobutton:
                if(isNetworkAvailable(getApplicationContext())) {
                    if (description.getText().toString().equals("") || language.getText().toString().equals("") || keywords.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), getString(R.string.cannotupload), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("upload video", "upload action button pressed");
                        String desc = description.getText().toString();
                        String lang = language.getText().toString().replaceAll("\\s", "");
                        String tags = keywords.getText().toString().replaceAll("\\s", "");
                        String projektname = projectname.replaceAll("\\s", "");
                        String categoryId = String.valueOf(name2Id.get(category.getSelectedItem().toString()));

                   /* category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String catname= category.get
                        }
                    });
*/
                        String projectZipFile = compressProject();

                        HashMap<String, String> info = new HashMap<String, String>();
                        // info.put("name",projectname);
                        info.put("description", desc);
                        info.put("language", lang);
                        info.put("keywords", tags);
                        info.put("name", projektname);
                        info.put("file", projectZipFile);
                        info.put("categoryId", categoryId);
                        //info.put("categoryId",categoryId);
                        //info.put("file",projectZipFile);
                        Log.d("Upload Project", "Launching Async Task.");
                        new uploadTask(UploadProject.this).execute(info);
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Please connect to internet",Toast.LENGTH_SHORT).show();

                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public String compressProject() {
        String output=null;
        try {
            ZipFile zipFile = new ZipFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya" + "/" + projectname + ".zip");
            File inputFileH = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya" + "/" + projectname);
            ZipParameters parameters = new ZipParameters();

            // COMP_DEFLATE is for compression
            // COMp_STORE no compression
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            // DEFLATE_LEVEL_ULTRA = maximum compression
            // DEFLATE_LEVEL_MAXIMUM
            // DEFLATE_LEVEL_NORMAL = normal compression
            // DEFLATE_LEVEL_FAST
            // DEFLATE_LEVEL_FASTEST = fastest compression
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            File outputFileH = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/lokavidya" + "/" + projectname + ".zip");

            // file compressed
            if(!outputFileH.exists()) {
                zipFile.addFolder(inputFileH, parameters);
            }
            long uncompressedSize = inputFileH.length();
            long comrpessedSize = outputFileH.length();

            //System.out.println("Size "+uncompressedSize+" vs "+comrpessedSize);
            double ratio = (double) comrpessedSize / (double) uncompressedSize;
            System.out.println("File compressed with compression ratio : " + ratio);

            output= outputFileH.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    class getCategoryList extends AsyncTask<Void,Void,Void>{
        Context context;
        public getCategoryList(Context context ){
            this.context=context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            postmanCommunication.idTokenString= sharedPreferences.getString("idToken", "id token");
            categoriesJSONArray = postmanCommunication.okhttpgetVideoJsonArray(BrowseAndViewVideos.VID_CAT_JSONARRAY_URL,sharedPreferences.getString("token",""));
            Log.d("UploadProject", categoriesJSONArray.toString());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String name;
            int id;
            List<String> categories = new ArrayList<String>();
            for(int i=0;i< categoriesJSONArray.length();i++){
                try {
                    name = categoriesJSONArray.getJSONObject(i).getString("name");
                    id = Integer.parseInt(categoriesJSONArray.getJSONObject(i).getString("id"));


                categories.add(name);
                name2Id.put(name, id);
                }catch (JSONException j){
                    j.printStackTrace();
                }
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, categories);

            category.setAdapter(dataAdapter);

        }
    }

    class uploadTask extends AsyncTask<HashMap<String,String> ,Void,Void>{

        boolean isUpload;
        Context context;
        ProgressDialog pd;
        public uploadTask(Context context){
            this.context=context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd= new ProgressDialog(context);
            pd.setMessage(getString(R.string.upload_progressdialog_message));
            pd.show();
        }

        @Override
        protected Void doInBackground(HashMap<String, String>... params) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            Log.d("Upload Project","Inside Async Task.");
            Log.d("Upload Project","Completed Async Task Before. Value of X-AUTH-Token="+sharedPreferences.getString("token",""));
            postmanCommunication.idTokenString= sharedPreferences.getString("idToken", "id token");
            Log.d("Upload Project","Completed Async Task. Value of X-AUTH-Token="+postmanCommunication.xAUTH_TOKEN);
            isUpload= postmanCommunication.okhttpUpload(params[0],UPLOAD_URL,sharedPreferences.getString("token",""));
            Log.d("Upload Project","Completed Async Task. Value of isUpload="+isUpload);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(isUpload){
                Toast.makeText(context, R.string.upload_success_toast,Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
            else{
                Toast.makeText(context, R.string.upload_fail_toast,Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            finish();
        }
    }



    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
