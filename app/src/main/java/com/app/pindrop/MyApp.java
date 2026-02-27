package com.app.pindrop;

import android.app.Application;

import org.osmdroid.config.Configuration;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Configuration.getInstance().load(this,
                getSharedPreferences("osmdroid", MODE_PRIVATE));

        Configuration.getInstance().setUserAgentValue(getPackageName());
    }
}
