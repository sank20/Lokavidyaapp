package com.iitb.mobileict.lokavidya.util;

        import android.os.Environment;
        import android.util.Log;

        import com.google.gson.Gson;
        import com.google.gson.GsonBuilder;
        import com.google.gson.reflect.TypeToken;
        import com.iitb.mobileict.lokavidya.data.Link;

        import java.io.BufferedReader;
        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileReader;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.io.Writer;
        import java.lang.reflect.Type;
        import java.util.ArrayList;
        import java.util.List;

/**
 * Created by sanket on 5/2/16.
 */
public class ExtraImageHandler {


    String folderName;
    String projectName, imageName;
    ArrayList<Link> linkStore;

    String absolutePath;

    public ExtraImageHandler(String folderName, String projectName, String imageName) {
        //Intialize the loading from assets
        this.folderName=folderName;
        this.projectName=projectName;
        this.imageName=imageName;
       // File linksFolder = new File(pathJoin(folderName, projectName, "links"));
        File linksFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/lokavidya/"+projectName+"/links/");

        System.out.println("links folder:" + linksFolder.getPath());
        if(!linksFolder.exists()){
              linksFolder.mkdir();
        }
        Log.d("ExtraImageHandler", ""+projectName);
        Log.d("ExtraImageHandler", ""+imageName);
        Log.d("ExtraImageHandler", ""+imageName.split("\\.")[1]+".json");
       // Log.d("ExtraImageHandler", ""+imageName.replace(projectName,""));
        File linksFile= new File(linksFolder.getAbsolutePath(),imageName.split("\\.")[1]+".json");
        absolutePath= linksFile.getAbsolutePath();
        System.out.println("link folder absolute Path"+absolutePath);
        if(!linksFile.exists())
        {
            linkStore = new ArrayList<Link>();
            persist();
        }
        linkStore = load();
    }

    private ArrayList<Link> load() {
        ArrayList<Link> tempArrayList = null;
        Gson gson = new Gson();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(absolutePath));
            tempArrayList = gson.fromJson(bufferedReader, new TypeToken<List<Link>>() {
            }.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tempArrayList;
    }

    public void addLink(Link link) {
        for(Link l:linkStore)
        {
            if(l.getVideoId().equals(link.getVideoId()))
            {
                return;
            }
        }
        if(linkStore==null||linkStore.isEmpty())
        {
            linkStore = new ArrayList<Link>();
            linkStore.add(link);
        }
        else
        {
            linkStore.add(link);
        }
        System.out.println("inside ExtraImageHandler");
        System.out.println("id:" + link.getVideoId() + ",Desc:" + link.getDescription() + ",url:" + link.getUrl()+",name:" + link.getName());
        for(Link l:linkStore)
        {
            Log.i("ExtraImageHandler", "" + l.getUrl());
        }
        persist();
    }

    public void removeLink(Link link) {
        int toRemove = -1;
        for (int i = 0; i < linkStore.size(); i++) {
            if (link.videoId.equals(link.videoId)) {
                toRemove = i;
                break;
            }
        }
        if (toRemove >= 0)
            linkStore.remove(toRemove);
        persist();
    }


    public void persist() {

        Gson gson = new Gson();

        // convert java object to JSON format,
        // and returned as JSON formatted string
        String json = gson.toJson(linkStore);
        try {
            //write converted json data to a file named "file.json"
            FileWriter writer = new FileWriter(absolutePath);
            writer.write(json);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String pathJoin(final String... pathElements) {
        final String path;

        if (pathElements == null || pathElements.length == 0) {
            path = File.separator;
        } else {
            final StringBuilder builder;

            builder = new StringBuilder();

            for (final String pathElement : pathElements) {
                final String sanitizedPathElement;

                // the "\\" is for Windows... you will need to come up with the
                // appropriate regex for this to be portable
                sanitizedPathElement = pathElement.replaceAll("\\" + File.separator, "");


                if (sanitizedPathElement.length() > 0) {
                    builder.append(sanitizedPathElement);
                    builder.append(File.separator);
                }
            }

            path = builder.toString();
        }

        return (path);
    }

}

