package com.iitb.mobileict.lokavidya;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * all the file handling methods related to projects are found here
 */
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
        File back_img = new File(sdCard.getAbsolutePath() + "/"+mainFolder+"/"+projectname+ "/tmp_images");

        if(imgDir.exists() && imgDir.isDirectory())
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
                        //Toast.makeText(mContext,"Image already exists",Toast.LENGTH_LONG).show();
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

            File writebackup = new File(back_img, newImg);
            FileOutputStream outStream_backup = null;

            try {
                outStream = new FileOutputStream(writetofile);
                outStream_backup = new FileOutputStream(writebackup);

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream_backup);

                outStream.flush();
                outStream.close();

                outStream_backup.flush();
                outStream_backup.close();

            } catch (Exception e) {
                toast("Cannot create new image : addimage");
                e.printStackTrace();
            } finally {
                try {
                    if (outStream!= null) {
                        outStream.close();
                    }
                    if(outStream_backup!=null){
                        outStream_backup.close();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



        }
        else
        {
            imgDir.mkdirs();
            back_img.mkdirs();

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

    /**
     * create new project
     * @param nameofproject projectname
     * @return list of projects
     */
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
                    File tmpimgdir = new File(sdCard.getAbsolutePath() + "/"+mainFolder+"/"+nameofproject+"/tmp_images");
                    tmpimgdir.mkdirs();

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
                File tmpimgdir = new File(sdCard.getAbsolutePath() + "/"+mainFolder+"/"+nameofproject+"/tmp_images");
                tmpimgdir.mkdirs();

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
            File tmpimgdir = new File(sdCard.getAbsolutePath() + "/"+mainFolder+"/"+nameofproject+"/tmp_images");
            tmpimgdir.mkdirs();

            myStringArray.add(nameofproject);
        }




        return myStringArray;
    }

    /**
     * copy feature like google docs
     *
     * @param pname proj name
     * @param newName new name the project needs to be renamed to
     * @param activity
     * @param context
     * @throws IOException
     */
    public void duplicateContents(final String pname, final String newName, final Activity activity, final Context context) throws IOException {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(activity, "Duplicating..",
                "Duplicating your project", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.setCanceledOnTouchOutside(false);
        new Thread(new Runnable() {
            @Override
            public void run(){
                System.out.println("..............Duplicating..............");
                File sdCard = Environment.getExternalStorageDirectory();
                File mainDir = new File (sdCard.getAbsolutePath() + "/"+mainFolder);
                File fromImagesdir = new File (sdCard.getAbsolutePath() + "/"+mainFolder + "/" + pname + "/images");
                File fromAudiodir = new File (sdCard.getAbsolutePath() + "/"+mainFolder + "/" + pname + "/audio");
                File toImagesdir = new File (sdCard.getAbsolutePath() + "/"+mainFolder + "/" + newName + "/images");
                File toAudiodir = new File (sdCard.getAbsolutePath() + "/"+mainFolder + "/" + newName + "/audio");

                String[] fromImages = fromImagesdir.list();
                String[] fromAudio = fromAudiodir.list();
                List<String> toImagesList = new ArrayList<String>();
                List<String> toAudioList = new ArrayList<String>();
                for(int i=0;i<fromImages.length;i++){
                    String image = fromImages[i];
                    toImagesList.add(newName+image.substring(image.length()-6));
                }
                for(int i=0;i<fromAudio.length;i++){
                    String audio = fromAudio[i];
                    toAudioList.add(newName + audio.substring(audio.length() - 6));
                }
                String[] toImages = new String[toImagesList.size()];
                toImagesList.toArray(toImages);
                String[] toAudio = new String[toAudioList.size()];
                toAudioList.toArray(toAudio);
                try {
                    for (int i = 0; i < fromImagesdir.listFiles().length; i++) {
                        InputStream in = new FileInputStream(new File(fromImagesdir, fromImages[i]));
                        OutputStream out = new FileOutputStream(new File(toImagesdir, toImages[i]));

                        // Copy the bits from instream to outstream
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }

                        out.close();
                        System.out.println("Images done..now doing tmp_images");
                        System.out.println("tmp_images done!!");
                        in.close();
                    }
                    for (int i = 0; i < fromAudiodir.listFiles().length; i++) {
                        InputStream in = new FileInputStream(new File(fromAudiodir, fromAudio[i]));
                        OutputStream out = new FileOutputStream(new File(toAudiodir, toAudio[i]));
                        // Copy the bits from instream to outstream
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                        in.close();
                        out.close();
                    }
                }
                catch (Exception exp){
                    System.out.print(exp);
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        ringProgressDialog.dismiss();
                    }
                });
            }
        }).start();
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
            boolean i= file.delete();

            System.out.println("File deleted!!!!!!!!!!!!!!!!!,............"+i);

            return i;
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

    /**
     * the temp folder is completely deleted.
     * @param ProjectName The name of the project saved in a caller method's string.
     */
    public static void deleteTmpFolder(String ProjectName){
        String path= Environment.getExternalStorageDirectory().getAbsolutePath()+ "/lokavidya" + "/" + ProjectName + "/tmp/";
        File dir= new File(path);
        dir.delete();

    }

    /**
     * changing the image for the same audio
     * @param ImageName
     * @param projectname
     * @param bitmap
     */
    public void replaceImage(String ImageName, String projectname, Bitmap bitmap) {
        String newImg;
        File sdCard = Environment.getExternalStorageDirectory();
        File imgDir = new File(sdCard.getAbsolutePath() + "/" + mainFolder + "/" + projectname + "/images");

        File back_img = new File(sdCard.getAbsolutePath() + "/"+mainFolder+"/"+projectname+ "/tmp_images");

        File writetofile = new File(imgDir, ImageName);
        File write_backup = new File(back_img, ImageName);

        FileOutputStream outStream = null;
        FileOutputStream outStream_backup = null;
        try {


            outStream = new FileOutputStream(writetofile);
            outStream_backup = new FileOutputStream(write_backup);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream_backup);

            outStream.flush();
            //outStream.close();

            outStream_backup.flush();
           // outStream_backup.close();


        } catch (Exception e) {
            toast("Cannot create new image : addimage");
            e.printStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
                if (outStream_backup != null) {
                    outStream_backup.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Copies the zip from an inputstream to the given path
     * @param is the inputstream of the file
     * @param outputURI the path of the outputfile
     */
    public static void copyFileFromInputstream(InputStream is,String outputURI){

        OutputStream impout=null;
        File temp= new File(outputURI).getParentFile();
        try {

            //Convert your stream to data here
            File outputzip=new File(outputURI);
            if(!temp.exists()){
                temp.mkdir();

            }
            impout= new FileOutputStream(outputzip);
            int read=0;
            byte[] bytes= new byte[10240];
            Log.i("import", "begin is-> os");
            while((read=is.read(bytes))!= -1){
                impout.write(bytes,0,read);
                Log.i("import","writing to outstream");
            }
            Log.i("import", "done is->os, check the folder");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            if(is!=null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(impout!=null){
                try {
                    impout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }


}
