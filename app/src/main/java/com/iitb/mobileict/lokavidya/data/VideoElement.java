package com.iitb.mobileict.lokavidya.data;

/**
 * Created by sanket on 9/7/2015.
 */
public class VideoElement {

    private String Id;

    private AudioElement audioElement;

    private ImageElement imageElement;


    public AudioElement getAudioElement() {
        return audioElement;
    }

    public void setAudioElement(AudioElement audioElement) {
        this.audioElement = audioElement;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public ImageElement getImageElement() {
        return imageElement;
    }

    public void setImageElement(ImageElement imageElement) {
        this.imageElement = imageElement;
    }
}
