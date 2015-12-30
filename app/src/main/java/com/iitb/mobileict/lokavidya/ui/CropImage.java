package com.iitb.mobileict.lokavidya.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropimage);

        Intent intent = getIntent();
        imagepath = intent.getStringExtra("path");
        mDstWidth = intent.getIntExtra("mDstWidth", 0);
        mDstHeight = intent.getIntExtra("mDstHeight", 0);

        RESIZE_FACTOR = EditProject.RESIZE_FACTOR;
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        cropButton = (Button) findViewById(R.id.Button_crop);

        final Bitmap bm = ScalingUtilities.decodeResource(mDstWidth, mDstHeight, ScalingUtilities.ScalingLogic.FIT, imagepath);
        cropImageView.setImageBitmap(bm);

        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap croppedImage = cropImageView.getCroppedImage();

                String[] str = imagepath.split("/");
                String path = "";
                String temp_path="";
                int i;
                for (i = 0; i < str.length - 1; i++) {
                    if(i==str.length-2){
                        temp_path = path;
                    }
                    path = path.concat(str[i]);
                    path = path.concat("/");
                }

                OutputStream out = null;
                File file = new File(path,str[i]);

                OutputStream out2 = null;
                File temp_file = new File(temp_path,str[i]);

                try {
                    out = new FileOutputStream(file);
                    croppedImage.compress(Bitmap.CompressFormat.PNG, 100, out);

                    out2 = new FileOutputStream(temp_file);
                    bm.compress(Bitmap.CompressFormat.PNG, 100, out2);
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                finally{
                    try{
                        if(out != null){
                            out.close();
                            out2.close();
                            Intent data = new Intent();
                            data.putExtra("path", path);
                            data.putExtra("temp_path", temp_path);
                            setResult(RESULT_OK, data);
                            finish();
                        }
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}