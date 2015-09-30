package com.iitb.mobileict.lokavidya.ui;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Environment;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import android.widget.ImageView;
import android.widget.Toast;

import com.iitb.mobileict.lokavidya.Projectfile;

import java.io.File;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    public String projectName;
    private SparseBooleanArray mSelectedItemsIds;


    public ImageAdapter(Context c, String s) {
        mContext = c;
        projectName = s;
    }

    public int getCount() {
        Projectfile f = new Projectfile(mContext);


        return f.getImageNames(projectName).size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200,200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8,8,8,8);
        } else {
            imageView = (ImageView) convertView;
        }

        Projectfile f = new Projectfile(mContext);
        List<String> ImageNames = f.getImageNames(projectName);

        if (position >= ImageNames.size()) return imageView;
        String imagefilename = ImageNames.get(position);

        File sdCard = Environment.getExternalStorageDirectory();



        File imgDir = new File (sdCard.getAbsolutePath() + "/lokavidya"+"/"+projectName+"/images");



        File image_file = new File(imgDir, imagefilename);
        Bitmap myBitmap = BitmapFactory.decodeFile(image_file.getAbsolutePath());
        imageView.setImageBitmap(myBitmap);

        return imageView;
    }


    public void remove(int position) {
        Projectfile f = new Projectfile(mContext);
        List<String> ImageNames = f.getImageNames(projectName);

        String imagefilename = ImageNames.get(position);
        String audioFilename = imagefilename.replace(".png",".wav");

        File sdCard = Environment.getExternalStorageDirectory();



        File imgDir = new File (sdCard.getAbsolutePath() + "/lokavidya"+"/"+projectName+"/images");
        File audDir = new File(sdCard.getAbsolutePath()+"/lokavidya"+"/"+projectName+"/audio");

        File image_file = new File(imgDir, imagefilename);
        File audio_file = new File(audDir, audioFilename);


        if(image_file.delete()){

        }
        if(audio_file.delete()){

        }

    }

    public boolean toggleSelection(int position) {

        boolean val = selectView(position, !mSelectedItemsIds.get(position));
        return val;
    }

    public boolean selectView(int position, boolean value) {
        if (value){
            mSelectedItemsIds.put(position, value);
            notifyDataSetChanged();
            return true;}
        else
        {
            mSelectedItemsIds.delete(position);
            notifyDataSetChanged();
        return false;}

    }

    public void initiateArray(){

        Projectfile f = new Projectfile(mContext);
        mSelectedItemsIds= new SparseBooleanArray();
        for(int i=0;i<f.getImageNames(projectName).size();i++){
            mSelectedItemsIds.put(i, false);
        }

    }

    public int removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        Projectfile f = new Projectfile(mContext);
        notifyDataSetChanged();
        return f.getImageNames(projectName).size();
    }
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public void toast(String text){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(mContext, text, duration);
        toast.show();
    }

}
