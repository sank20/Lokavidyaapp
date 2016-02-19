package com.iitb.mobileict.lokavidya.data;

import com.iitb.mobileict.lokavidya.data.enums.MElementType;

/**
 * Created by sanket on 15/2/16.
 */

public class MElement {

    public MElementType getElementType() {
        return elementtype;
    }

    public void setElementType(MElementType type) {
        this.elementtype = type;
    }

    MElementType elementtype;

    Audio audio;

    Video video;



    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    Image image;


}
