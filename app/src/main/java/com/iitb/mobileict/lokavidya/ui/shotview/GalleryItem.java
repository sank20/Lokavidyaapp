package com.iitb.mobileict.lokavidya.ui.shotview;

import android.graphics.Bitmap;

/**
 * Created by root on 16/10/15.
 */
public class GalleryItem {

    public String  imgFileName;

   public int position;

   public boolean box;


    public GalleryItem(String _name,int  _position, boolean _box) {
        imgFileName = _name;

        position=_position;

        box = _box;
    }
}
