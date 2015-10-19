package com.iitb.mobileict.lokavidya;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Projectfile {
    public Context mContext;
    public String mainFolder = "lokavidya";
    public Projectfile(Context c){
        mContext = c;
    }

    public List<String> DisplayProject(){
        List<String> myStringArray = new ArrayList<String>();


        File sdCard = Environment.getExternalStorageDirectory();

        File mainDir = new File (sdCard.getAbsolutePath() + "/"+mainFolder);


        if(mainDir.exists()&& mainDir.isDirectory())
        {

            File file[] = mainDir.listFiles();



            if(file.length!=0)
            {
                boolean fileExists= false;

                for (int i=0; i < file.length; i++)
                {

                    if (file[i].getName().length() >= 4){
                        if (file[i].getName().substring(file[i].getName().length() - 4).equals(".zip")) {
                            continue;
                        }
                    }
                    myStringArray.add(file[i].getName());


                }


            }

            else
            {
                Toast.makeText(mContext,"No project exists",Toast.LENGTH_LONG).show();


            }



        }

        return myStringArray;
    }

    public List<String> DisplayProject_with_zips(){
        List<String> myStringArray = new ArrayList<String>();


        File sdCard = Environment.getExternalStorageDirectory();

        File mainDir = new File (sdCard.getAbsolutePath() + "/"+mainFolder);


        if(mainDir.exists()&& mainDir.isDirectory())
        {

            File file[] = mainDir.listFiles();



            if(file.length!=0)
            {
                boolean fileExists= false;

                for (int i=0; i < file.length; i++)
                {


                    myStringArray.add(file[i].getName());


                }


            }

            else
            {
                Toast.makeText(mContext,"No project exists",Toast.LENGTH_LONG).show();


            }



        }

        return myStringArray;
    }

    /* addImage: used in EditProject.java. to take an image from the gallery and put it in the images folder.
    the picking image from gallery is done in EditProject.java.
     */
    public void addImage(Bitmap bitmap, String projectname){
        String newImg;
        File sdCard = Environment.getExternalStorageDirectory();
        File imgDir = new File (sdCard.getAbsolutePath() + "/"+mainFolder+"/"+projectname+"/images");

        if(imgDir.exists()&& imgDir.isDirectory())
        {
            File file[] = imgDir.listFiles();
            newImg = projectname + "." + String.valueOf(file.length+1) + ".png";

            if(file.length!=0)
            {
                boolean fileExists= false;

                int countImg[] = new int[file.length];

                for (int i=0; i < file.length; i++)
                {
                    String imgName = file[i].getName();
                    Log.i("addimage imgname", imgName);
                    imgName = imgName.replace(".png", "");
                    Log.i("addimage imgname 2", imgName);
                    String splitImgName[] = imgName.split("\\.");
                    countImg[i]=Integer.parseInt(splitImgName[1]);
                    Log.i("addimage splitimgname", splitImgName[1]);
                    if(newImg.equals(file[i].getName()))
                    {

                        Toast.makeText(mContext,"Image already exists",Toast.LENGTH_LONG).show();
                        fileExists=true;

                    }

                }


                if(fileExists==true)
                {
                 int large=largestAndSmallest(countImg);

                    newImg = projectname + "." + String.valueOf(large+1) + ".png";
                }
            }
            File writetofile = new File(imgDir, newImg);
            FileOutputStream outStream = null;
            try {


                outStream = new FileOutputStream(writetofile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();


            } catch (Exception e) {
                toast("Cannot create new image : addimage");
                e.printStackTrace();
            } finally {
                try {
                    if (outStream!= null) {
                        outStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



        }
        else
        {
            imgDir.mkdirs();

        }




    }



    public int largestAndSmallest(int[] numbers) {
        int largest = Integer.MIN_VALUE;
        int smallest = Integer.MAX_VALUE;
        for (int number : numbers) {
            if (number > largest) {
                largest = number;
            } else if (number < smallest) {
                smallest = number;
            }
        }

        return  largest;

    }


        public List<String> getImageNames(String projectname){
            List<String> ImageNames = new ArrayList<String>();
            File sdCard = Environment.getExternalStorageDirectory();
            File imgDir = new File (sdCard.getAbsolutePath() + "/"+mainFolder+"/"+projectname+"/images");

            if(imgDir.exists()&& imgDir.isDirectory())
            {
                File file[] = imgDir.listFiles();

                if(file.length!=0)
                {
                    boolean fileExists= false;

                    for (int i=0; i < file.length; i++)
                    {
                        ImageNames.add(file[i].getName());
                    }
                }
            }


        return ImageNames;
    }




    public void toast(String text){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(mContext, text, duration);
        toast.show();
    }

    public List<String> AddNewProject(String nameofproject) {


        List<String> myStringArray = new ArrayList<String>();
        File sdCard = Environment.getExternalStorageDirectory();



        File mainDir = new File (sdCard.getAbsolutePath() + "/"+mainFolder);

        if(mainDir.exists()&& mainDir.isDirectory())
        {


            File file[] = mainDir.listFiles();



            if(file.length!=0)
            {
                boolean fileExists= false;

                for (int i=0; i < file.length; i++)
                {


                    myStringArray.add(file[i].getName());

                    if(nameofproject.equals(file[i].getName()))
                    {

                        Toast.makeText(mContext,"Project already exists",Toast.LENGTH_LONG).show();
                     fileExists=true;
                        break;
                    }

                }

                if(fileExists==false)
                {
                    File projdir = new File (sdCard.getAbsolutePath() + "/"+mainFolder+"/"+nameofproject);
                    projdir.mkdirs();
                    File imgdir = new File (sdCard.getAbsolutePath() + "/"+mainFolder+"/"+nameofproject+"/images");
                    imgdir.mkdirs();
                    File audiodir = new File (sdCard.getAbsolutePath() + "/"+mainFolder+"/"+nameofproject+"/audio");
                    audiodir.mkdirs();

                    myStringArray.add(nameofproject);
                }
            }

            else
            {
                File projdir = new File (sdCard.getAbsolutePath() + "/"+mainFolder+"/"+nameofproject);
                projdir.mkdirs();
                File imgdir = new File (sdCard.getAbsolutePath() + "/"+mainFolder+"/"+nameofproject+"/images");
                imgdir.mkdirs();
                File audiodir = new File (sdCard.getAbsolutePath() + "/"+mainFolder+"/"+nameofproject+"/audio");
                audiodir.mkdirs();

                myStringArray.add(nameofproject);
            }


        }
        else
        {
            mainDir.mkdirs();
            File projdir = new File (sdCard.getAbsolutePath() + "/"+mainFolder+"/"+nameofproject);
            projdir.mkdirs();
            File imgdir = new File (sdCard.getAbsolutePath() + "/"+mainFolder+"/"+nameofproject+"/images");
            imgdir.mkdirs();
            File audiodir = new File (sdCard.getAbsolutePath() + "/"+mainFolder+"/"+nameofproject+"/audio");
            audiodir.mkdirs();

            myStringArray.add(nameofproject);
        }




        return myStringArray;
    }


    public List<String> DeleteProject(CharSequence projectName)
    {
        List<String> myStringArray = new ArrayList<String>();
        File sdCard = Environment.getExternalStorageDirectory();



        File prjDir = new File (sdCard.getAbsolutePath() + "/"+mainFolder+"/"+projectName);

        deleteFile(prjDir);

        myStringArray= DisplayProject();

       return  myStringArray;

    }


    public  boolean deleteFile(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteFile(new File(file, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
            return file.delete();
        }
        return false;
    }

/** By Sanket Pimple. :
*(The  method is called in the finally block in Stitch.java) It deletes the temporary files generated during the
*stitching process and retains only the out.txt and final.mp4 .
 * @param ProjectName : The name of the project saved in a caller method's string.
 */
    public static void deleteTempFiles(String ProjectName){
        int i;
        String path= Environment.getExternalStorageDirectory().getAbsolutePath()+ "/lokavidya" + "/" + ProjectName + "/tmp/";
        //Log.i("path", path);
        File dir= new File(path);
        File[] list=dir.listFiles();
        if(list!=null) {
            //Log.i("yay","found the files!");
            for (i = 0; i < list.length; i++) {
                String name = list[i].getName();
              //  Log.i("name", list[i].getName());
                String extension = name.substring(name.lastIndexOf(".") + 1, name.length());
                if (!name.equals("final.mp4") && extension.equals("mp4"))
                    list[i].delete();
            }
        }
    }

}
