package com.iitb.mobileict.lokavidya.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;

import android.widget.GridView;

import android.widget.Toast;

import com.iitb.mobileict.lokavidya.Projectfile;
import com.iitb.mobileict.lokavidya.R;
import com.iitb.mobileict.lokavidya.ui.shotview.ViewShots;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import java.io.File;

import java.io.IOException;

import java.util.List;


public class EditProject extends Activity {
    String projectName;
    ImageAdapter imageadapter;
    public static int RESIZE_FACTOR = 400;
    private static final int REQUEST_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        projectName = intent.getStringExtra("projectname");
        setContentView(R.layout.activity_edit_project);
        loadImages();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case REQUEST_IMAGE:
                if(resultCode == RESULT_OK){
                    //Uri imageUri = imageReturnedIntent.getData();

                    List<String> path = imageReturnedIntent.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    Log.i("wth inside gallery case",path.get(0));


                    try{
                        int i;
                        for(i=0;i<path.size();i++) {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), getImageContentUri(getApplicationContext(), path.get(i))); //getbitmap() needs content uri as its parameter. for that see getimage content uri() method.
                            bitmap = getResizedBitmap(bitmap, RESIZE_FACTOR);
                            Projectfile f = new Projectfile(getApplicationContext());
                            f.addImage(bitmap, projectName);
                        }
                        loadImages();
                    }
                    catch(IOException fe){
                        toast("Image file not found in the library " + Uri.parse(path.get(0)));
                    }
                }
                break;
            case 2:

                if(resultCode == Activity.RESULT_OK) {
                    Uri takenPhotoUri = getPhotoFileUri("temp.png");
                    Log.i("inside onActvtyRslt cam",takenPhotoUri.toString());

                    Bitmap photo = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                    photo = getResizedBitmap(photo, RESIZE_FACTOR);

                    Projectfile f = new Projectfile(getApplicationContext());
                    f.addImage(photo, projectName);
                    loadImages();
                }
                else {
                    Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                }
                break;

            default:break;
        }
    }

    public void toast(String text){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();
    }

    public void gallery(View v){
        /*Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);*/
        Log.i("inside gallery method", "wth");

        Intent intent = new Intent(getApplicationContext(), MultiImageSelectorActivity.class);

// whether show camera
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);

// max select image amount
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);

// select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);

        startActivityForResult(intent, REQUEST_IMAGE);


    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void ImageClickCallBack(int position){
        Intent intent = new Intent(getApplicationContext(), Recording.class);
        intent.putExtra("projectname", projectName);
        Projectfile f = new Projectfile(getApplicationContext());
        List<String> ImageNames = f.getImageNames(projectName);
        String imagefilename = ImageNames.get(position);

        imagefilename = imagefilename.substring(0, imagefilename.length() - 4);

        intent.putExtra("filename", imagefilename);
        startActivity(intent);
    }

    public void loadImages(){
        final GridView gridview = (GridView) findViewById(R.id.gridview);
        imageadapter= new ImageAdapter(getApplicationContext(), projectName);
        gridview.setAdapter(imageadapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                ImageClickCallBack(position);
            }
        });

        gridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);


        gridview.setMultiChoiceModeListener(new MultiChoiceModeListener() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_delete, menu);
                imageadapter.initiateArray();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // TODO Auto-generated method stub
                int size = imageadapter.removeSelection();
                for (int i = 0; i < size; i++) {
                    gridview.getChildAt(i).toString();
                    gridview.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position, long id, boolean checked) {

                final int checkedCount;
                checkedCount = gridview.getCheckedItemCount();

                mode.setTitle(checkedCount + " Selected");

                boolean val = imageadapter.toggleSelection(position);
                if (val) {
                    gridview.getChildAt(position).setBackgroundColor(Color.BLUE);
                } else {
                    gridview.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);

                }
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:

                        SparseBooleanArray selected = imageadapter.getSelectedIds();

                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                int position = selected.keyAt(i);

                                imageadapter.remove(position);
                            }
                        }

                        SharedPreferences sharedPref;
                        SharedPreferences.Editor editor;
                        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        editor = sharedPref.edit();
                        editor.putInt("savedView", 0);
                        editor.commit();
                        // Close CAB
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }



    public  void  takePic(View v){



        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri("temp.png"));

        startActivityForResult(intent, 2);

    }
    public Uri getPhotoFileUri(String fileName) {


        if (isExternalStorageAvailable()) {


            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "lokavidya_images");

            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){

            }


            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));



        }
        return null;
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public void proceed(View view){
        ImageAdapter ia = new ImageAdapter(getApplicationContext(),projectName);
        int check = ia.getCount();
        System.out.println("CCCOOOUUNNNTTT" + check+"");
        if(check>0) {
            Intent intent1 = new Intent(getApplicationContext(), ViewShots.class);
            intent1.putExtra("projectname", projectName);
            startActivity(intent1);
        }else
            Toast.makeText(EditProject.this,"Empty project!!!",Toast.LENGTH_LONG).show();

    }



    /* The following method is used to convert an absolute path to Content Uri.
    The content Uri is needed for the MediaStore.Images.Media.getBitmap() method used above, to pass as the second parameter
     */
    public static Uri getImageContentUri(Context context, String filePath) {
       // String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (new File(filePath).exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

}

