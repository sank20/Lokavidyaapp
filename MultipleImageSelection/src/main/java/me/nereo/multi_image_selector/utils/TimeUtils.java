package me.nereo.multi_image_selector.utils;

import android.media.ExifInterface;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Time processing tools
 *
 */
public class TimeUtils {

    public static String timeFormat(long timeMillis, String pattern){
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date(timeMillis));
    }

    public static String formatPhotoDate(long time){
        return timeFormat(time, "MM-dd-yyyy");
    }

    public static String formatPhotoDate(String path){
        File file = new File(path);
        if(file.exists()){
            long time = file.lastModified();
            return formatPhotoDate(time);
        }
        return "01-01-1970";
    }
}
