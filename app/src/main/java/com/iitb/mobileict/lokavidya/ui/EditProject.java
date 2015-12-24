package com.iitb.mobileict.lokavidya.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.iitb.mobileict.lokavidya.Projectfile;
import com.iitb.mobileict.lokavidya.R;
import com.iitb.mobileict.lokavidya.ui.shotview.GalleryItem;
import com.iitb.mobileict.lokavidya.ui.shotview.ViewShots;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saifur on 16/10/15.
 */

/**
 * Implementation of the Editing activity- contains all the supporting methods
 *
 */
public class EditProject extends Activity {

    String projectName;
    ImageAdapter1 imageadapter;
    Button btnDelete;
    public static int RESIZE_FACTOR; // resize factor used for compression
    public static final int REQUEST_IMAGE = 1; //just a constant
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        projectName = intent.getStringExtra("projectname");
        setContentView(R.layout.activity_edit_project);
        btnDelete = (Button) findViewById(R.id.btnDeleteImg);

    }

    @Override
    protected void onStart() {
        super.onStart();

        loadImages(true);  //goes to the project folder and loads all the images in the gridview
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    public Bitmap getImage(String path) throws IOException {
        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(cr, getImageContentUri(getApplicationContext(), path));
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        RESIZE_FACTOR = getScreenwidth();
        System.out.println("RESIZE_FACTOR-----------------------------------------"+RESIZE_FACTOR);
        switch (requestCode) {
            case REQUEST_IMAGE: //when you tap on 'Choose from gallery'
                if (resultCode == RESULT_OK) {
                    //Uri imageUri = imageReturnedIntent.getData();
                    final List<String> path = imageReturnedIntent.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    Log.i("wth inside gallery case", path.get(0));

                    final ProgressDialog imageLoadingProgress = ProgressDialog.show(this, getString(R.string.stitchingProcessTitle), getString(R.string.galleryImageProcessMessage), true);

                    imageLoadingProgress.setCancelable(false);
                    imageLoadingProgress.setCanceledOnTouchOutside(false);
                    final ContentResolver cr = this.getContentResolver();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                int i;
                                for (i = 0; i < path.size(); i++) {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(cr, getImageContentUri(getApplicationContext(), path.get(i))); //getbitmap() needs content uri as its parameter. for that see getimage content uri() method.
                                    bitmap = getResizedBitmap(bitmap, RESIZE_FACTOR);

                                    Projectfile f = new Projectfile(getApplicationContext());
                                    f.addImage(bitmap, projectName);

                                    System.out.println("...........................??=="+ bitmap.getHeight());
                                    System.out.println("...........................??==" + bitmap.getWidth());
                                }

                            } catch (IOException fe) {
                                toast("Image file not found in the library " + Uri.parse(path.get(0)));
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadImages(true);
                                    imageLoadingProgress.dismiss();
                                }
                            });
                        }
                    }).start();




                }
                break;
            case 2: // when 'take a photo' is pressed

                if (resultCode == Activity.RESULT_OK) {
                    Uri takenPhotoUri = getPhotoFileUri("temp.png");
                    Log.i("inside onActvtyRslt cam", takenPhotoUri.toString());

                    Bitmap photo = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                    photo = getResizedBitmap(photo, RESIZE_FACTOR);

                    System.out.println("...........................??" + photo.getHeight());
                    System.out.println("...........................??"+photo.getWidth());

                    Projectfile f = new Projectfile(getApplicationContext());
                    f.addImage(photo, projectName);
                    loadImages(true);
                } else {
                    Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    /**
     * make a toast!
     * @param text string to put in the toast
     */
    public void toast(String text) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();
    }

    public int getScreenwidth(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }

    /**
     * called on click of 'Choose from gallery'
     * @param v view
     */
    public void gallery(View v) {
        /*Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);*/
        Log.i("inside gallery method", "ok");

        Intent intent = new Intent(getApplicationContext(), MultiImageSelectorActivity.class);

// whether show camera
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);

// max select image amount
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 10);

// select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);

        startActivityForResult(intent, REQUEST_IMAGE);


    }

    /**
     * resize the bitmap according to the given size
     * @param image the bitmap
     * @param maxSize Resize factor
     * @return returns resized bitmap
     */
    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        Log.i("getresizedbitmapInitial","width: "+Integer.toString(width) + " height: "+Integer.toString(height));
        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio >= 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        Log.i("getresizedbitmapChanged","width: "+Integer.toString(width) + " height: "+Integer.toString(height));
        return addpaddingBitmap(image, width, height, maxSize);
//        return Bitmap.createScaledBitmap(image, width, height, true);

    }

    /**
     * opens the recording activity of the given image in the project.
     * @param position position of the image in the gridview
     */
    public void ImageClickCallBack(int position) {
        Intent intent = new Intent(getApplicationContext(), Recording.class);
        intent.putExtra("projectname", projectName);
        Projectfile f = new Projectfile(getApplicationContext());
        List<String> ImageNames = f.getImageNames(projectName);
        String imagefilename = ImageNames.get(position);

        imagefilename = imagefilename.substring(0, imagefilename.length() - 4);

        intent.putExtra("filename", imagefilename);
        startActivity(intent);
    }


    /**
     * called when the button 'take picture is pressed
     * @param v the View
     */
    public void takePic(View v) {


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri("temp.png"));

        startActivityForResult(intent, 2);

    }

    /**
     * returns the uri of the given file path
     * @param fileName the image filename
     * @return
     */
    public Uri getPhotoFileUri(String fileName) {


        if (isExternalStorageAvailable()) {


            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "lokavidya_images");

            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {

            }


            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));


        }
        return null;
    }

    /**
     * check if SD card is available
     * @return true if available, false if not
     */
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    /**
     * When you press 'Let's make a project
     * @param view
     */
    public void proceed(View view) {
        //ImageAdapter1 ia = new ImageAdapter1();
        int check = imageadapter.getCount();
        System.out.println("CCCOOOUUNNNTTT" + check + "");
        if (check > 0) {
            Intent intent1 = new Intent(getApplicationContext(), ViewShots.class);
            intent1.putExtra("projectname", projectName);
            startActivity(intent1);
        } else
            Toast.makeText(EditProject.this, "Empty project!!!", Toast.LENGTH_LONG).show();

    }

    /**
     * Loads the images and displays them into the  gridview
     */
    public void loadImages(boolean isNew) {
        Projectfile f = new Projectfile(this);
        List<String> ImageNames = f.getImageNames(projectName);

        ArrayList<GalleryItem> galleryItemsList = new ArrayList<GalleryItem>();

        String imagefilename;
        //= ImageNames.get(position);

        File sdCard = Environment.getExternalStorageDirectory();

        Bitmap myBitmap;

        File imgDir = new File(sdCard.getAbsolutePath() + "/lokavidya" + "/" + projectName + "/images");
        File image_file;
        for (int i = 0; i < ImageNames.size(); i++) {
            imagefilename = ImageNames.get(i);
            // image_file=  new File(imgDir, imagefilename);
            //myBitmap = BitmapFactory.decodeFile(image_file.getAbsolutePath());
            //galleryItemsList.add(new GalleryItem(myBitmap,i,false));
            galleryItemsList.add(new GalleryItem(imagefilename, i, false));

        }

        for (int i = 0; i < galleryItemsList.size(); i++)
            System.out.println("loading image pos------->" + galleryItemsList.get(i).position + "---------" + galleryItemsList.get(i).imgFileName);

        if(isNew) {//this check was added to decide whether to add the images in the gridview, since if you need to call it from other method, this UI cannot be updated
            GridView gridview = (GridView) findViewById(R.id.gridview);
            imageadapter = new ImageAdapter1(this, R.layout.galleryitem, galleryItemsList);
            gridview.setAdapter(imageadapter);
        }



    }


    class ViewHolder {
        ImageView imageview;
        CheckBox checkbox;

    }

    public void deletePressed(View v) {
        if (count > 0) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(EditProject.this);
            builder1.setTitle("Sure?");
            builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    //  for(int i=imageadapter.getBox().size();i>=0;i--)
                    for (GalleryItem p : imageadapter.getBox()) {
                        // GalleryItem p = imageadapter.getBox().get(i);
                        if (p.box) {
                            System.out.println("image pos----------->" + p.position);

                            imageadapter.remove(p);
                            //  Projectfile f = new Projectfile(getApplicationContext());
                            //List<String> ImageNames = f.getImageNames(projectName);

                        }

                    }
                    // imageadapter.removeTask();
                    imageadapter.notifyDataSetChanged();
                    // selectedFileInt.clear();
                    loadImages(true);
                }
            })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                               /* for (int i = 0; i < numberOfImages; i++) {
                                    System.out.println("Arrraaaayyyyy  at  /////" + i + ".......is selected/not" + selectedFileInt.get(i));
                                }*/
                                    dialog.cancel();
                                }
                            }

                    );
            builder1.create().

                    show();


        }
    }

    public class ImageAdapter1 extends ArrayAdapter<GalleryItem> {
        Context ctx;
        LayoutInflater lInflater;
        ArrayList<GalleryItem> objects;
        ViewHolder viewHolder;
    /*ImageAdapter1(Context context, ArrayList<GalleryItem> galleryItemList) {
        ctx = context;
        objects = galleryItemList;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }*/

        public ImageAdapter1(Context context, int resourceId, ArrayList<GalleryItem> galleryItemList) {
            super(context, resourceId, galleryItemList);
            // mSelectedItemsIds = new SparseBooleanArray();
            ctx = context;
            objects = galleryItemList;
            lInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public GalleryItem getItem(int position) {
            return objects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void remove(GalleryItem object) {
            System.out.println("removing image pos----------->" + object.position);
            // int pos=object.position;
//            objects.remove(object);
            String imagefilename = object.imgFileName;
            String audioFilename = imagefilename.replace(".png", ".wav");

            File sdCard = Environment.getExternalStorageDirectory();


            File imgDir = new File(sdCard.getAbsolutePath() + "/lokavidya" + "/" + projectName + "/images");
            File audDir = new File(sdCard.getAbsolutePath() + "/lokavidya" + "/" + projectName + "/audio");

            File image_file = new File(imgDir, imagefilename);
            File audio_file = new File(audDir, audioFilename);


            if (image_file.delete()) {

            }
            if (audio_file.delete()) {

            }
            objects.remove(object);
            notifyDataSetChanged();
            count=0;
            for (int i = 0; i < objects.size(); i++)
                System.out.println("object postion retain------------------>" + objects.get(i).position + "---------" + objects.get(i).imgFileName);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = lInflater.inflate(R.layout.galleryitem, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.imageview = ((ImageView) view.findViewById(R.id.thumbImage));
                viewHolder.checkbox = (CheckBox) view.findViewById(R.id.itemCheckBox);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            GalleryItem p = getProduct(position);

            File sdCard = Environment.getExternalStorageDirectory();


            File imgDir = new File(sdCard.getAbsolutePath() + "/lokavidya" + "/" + projectName + "/images");
            File image_file = new File(imgDir, p.imgFileName);
            Bitmap myBitmap = BitmapFactory.decodeFile(image_file.getAbsolutePath());
            //      ((TextView) view.findViewById(R.id.subgrpname)).setText(p.name);
            viewHolder.imageview.setImageBitmap(myBitmap);

            viewHolder.imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageClickCallBack(position);

                }
            });
            //CheckBox cbBuy = (CheckBox) view.findViewById(R.id.itemCheckBox);
            viewHolder.checkbox.setOnCheckedChangeListener(myCheckChangList);
            viewHolder.checkbox.setTag(position);
            viewHolder.checkbox.setChecked(p.box);
            return view;
        }

        GalleryItem getProduct(int position) {
            return ((GalleryItem) getItem(position));
        }

        ArrayList<GalleryItem> getBox() {
            ArrayList<GalleryItem> box = new ArrayList<GalleryItem>();
            for (GalleryItem p : objects) {
                System.out.println("details------>"+p.position+"  "+p.box);

                box.add(p);
            }
            return box;
        }

        CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                getProduct((Integer) buttonView.getTag()).box = isChecked;
                if(isChecked==true){
                    count++;
                }else{
                    count--;
                }
                getProduct((Integer) buttonView.getTag()).position=(Integer) buttonView.getTag();

            }
        };


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

    public static Bitmap addpaddingBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight, int maxsize){
        int width, height;
        int padwidth, padheight;
        int imwidth, imheight;

        width = maxsize;
        height = (int) maxsize*3/4;

        if(dstWidth>=dstHeight) {
            if (height > dstHeight) {
                padheight = height - dstHeight;
                padwidth = 0;
            } else {
                padheight = 0;
                float ratio = (float) dstHeight / (float) height;
                padwidth = (int) (width - width / ratio);
            }
        }
        else{
            dstHeight = (int)dstHeight*3/4;
            dstWidth = (int)dstWidth*3/4;
            if(width>dstWidth){
                padheight = 0;
                padwidth = width - dstWidth;
            }
            else{
                padwidth = 0;
                float ratio = (float) dstWidth / (float) width;
                padheight = (int) (height - height/ratio);
            }
        }

        imwidth = width - padwidth;
        imheight = height - padheight;
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(unscaledBitmap, imwidth, imheight, true);

        Bitmap paddedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(paddedBitmap);
        canvas.drawARGB(0xFF, 0x00, 0x00, 0x00);
        canvas.drawBitmap(scaledBitmap, padwidth / 2, padheight / 2, null);

        System.out.println("...................................................dstHeight = "+dstHeight);
        System.out.println("...................................................dstWidth = " + dstWidth);
        System.out.println("...................................................padheight = "+padheight);
        System.out.println("...................................................padwidth = "+padwidth);
        System.out.println("...................................................imheight = "+imheight);
        System.out.println("...................................................imwidth = "+imwidth);
        System.out.println("...................................................canwidth = " + canvas.getWidth());
        System.out.println("...................................................canheight = " + canvas.getHeight());

        return paddedBitmap;
    }

}
