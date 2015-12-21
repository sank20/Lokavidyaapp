package com.iitb.mobileict.lokavidya.util;

import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.iitb.mobileict.lokavidya.ui.Projects;

/**
 * Created by sanket on 16/12/15.
 */
public class animations {


    public static void animateFAB(Boolean isFabOpen,FloatingActionButton fab,FloatingActionButton fab1, FloatingActionButton fab2,Animation rotate_forward,Animation rotate_backward, Animation fab_open, Animation fab_close,Button fabAddButton, Button fabImportButton){



        if(Projects.isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fabAddButton.startAnimation(fab_close);
            fabImportButton.startAnimation(fab_close);
            fabAddButton.setClickable(false);
            fabImportButton.setClickable(false);
            Projects.isFabOpen = false;
            Log.d("FAB", "close");

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fabAddButton.startAnimation(fab_open);
            fabImportButton.startAnimation(fab_open);
            fabAddButton.setClickable(true);
            fabImportButton.setClickable(true);
            Projects.isFabOpen = true;
            Log.d("FAB","open");

        }
    }
}
