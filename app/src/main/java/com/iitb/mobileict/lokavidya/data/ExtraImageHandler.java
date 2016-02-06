package com.iitb.mobileict.lokavidya.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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

    public ExtraImageHandler(String folderName, String projectName, String imageNo) {
        //Intialize the loading from assets
        File linksFolder = new File(pathJoin(folderName,projectName,"links"));
        if(!linksFolder.exists())
            try {
                linksFolder.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        File linksFile= new File(folderName,imageName.replace(projectName,"").replace(".png",""));
        absolutePath= linksFile.getAbsolutePath();
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
        linkStore.add(link);
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

    class Link {
        String videoId, description, url;
    }


    public void persist() {
        try {
            Writer writer = new FileWriter(absolutePath);
            Gson gson = new GsonBuilder().create();
            gson.toJson(linkStore, writer);
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
