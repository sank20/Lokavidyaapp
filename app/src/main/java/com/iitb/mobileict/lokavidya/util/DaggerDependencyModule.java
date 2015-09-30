package com.iitb.mobileict.lokavidya.util;

import android.content.Context;

import com.iitb.mobileict.lokavidya.Stitch;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = Stitch.class,
        library = true
)
@SuppressWarnings("unused")
public class DaggerDependencyModule {

    private final Context context;

    public DaggerDependencyModule(Context context) {
        this.context = context;
    }

    @Provides @Singleton
    public FFmpeg provideFFmpeg() {
        return FFmpeg.getInstance(context.getApplicationContext());
    }

}
