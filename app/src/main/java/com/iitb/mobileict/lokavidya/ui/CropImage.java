package com.iitb.mobileict.lokavidya.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.edmodo.cropper.CropImageView;
import com.iitb.mobileict.lokavidya.R;
import com.iitb.mobileict.lokavidya.util.ScalingUtilities;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CropImage extends Activity {
    private static final int GUIDELINES_ON_TOUCH = 1;

    public String imagepath;
    public int mDstWidth, mDstHeight;

    public Button cropButton;
    public CropImageView cropImageView;
    public int RESIZE_FACTOR;

    public ImageView croppedImageView;
    public boolean changed;

    public Bitmap croppedImage;
    public Bitmap cropped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropimage);

        Intent intent = getIntent();
        imagepath = intent.getStringExtra("path");
        mDstWidth = intent.getIntExtra("mDstWidth", 0);
        mDstHeight = intent.getIntExtra("mDstHeight", 0);
        changed = false;

        RESIZE_FACTOR = EditProject.RESIZE_FACTOR;

        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        cropButton = (Button) findViewById(R.id.Button_crop);
        croppedImageView = (ImageView) findViewById(R.id.croppedImageView);

        final Bitmap bm = ScalingUtilities.decodeResource(mDstWidth, mDstHeight, ScalingUtilities.ScalingLogic.FIT, imagepath);
        cropImageView.setImageBitmap(bm);



        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                croppedImage = cropImageView.getCroppedImage();

                RESIZE_FACTOR = getScreenwidth();
                cropped = EditProject.getResizedBitmap(croppedImage, RESIZE_FACTOR);
                croppedImageView.setImageBitmap(cropped);

                changed = true;
            }
        });
    }

    public int getScreenwidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }


    @Override
    public void onBackPressed() {
        System.out.println("Back button pressed");
        if (changed) {
            System.out.println("changed = true");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.save);
            System.out.println("Dialog works");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String[] str = imagepath.split("/");
                    String path = "";
                    String temp_path = "";
                    int i;
                    for (i = 0; i < str.length - 1; i++) {
                        if (i == str.length - 2) {
                            temp_path = path;
                        }
                        path = path.concat(str[i]);
                        path = path.concat("/");
                    }

                    temp_path = temp_path.concat("tmp_images/");

                    OutputStream out = null;
                    File file = new File(path, str[i]);

                    OutputStream out2 = null;
                    File temp_file = new File(temp_path, str[i]);

                    try {
                        out = new FileOutputStream(file);
                        cropped.compress(Bitmap.CompressFormat.PNG, 100, out);

                        out2 = new FileOutputStream(temp_file);
                        croppedImage.compress(Bitmap.CompressFormat.PNG, 100, out2);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (out != null) {
                                out.close();
                                out2.close();
                                Intent data = new Intent();
                                setResult(RESULT_OK, data);
                                finish();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent data = new Intent();
                    setResult(RESULT_OK, data);
                    dialog.cancel();
                    finish();
                }
            });
            builder.show();
        }
        else{
            System.out.println("changed = false");
            Intent data = new Intent();
            setResult(RESULT_OK, data);
            finish();
        }
    }
}