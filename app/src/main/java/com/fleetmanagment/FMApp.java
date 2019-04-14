package com.fleetmanagment;

import android.app.Application;
import android.content.Context;

public class FMApp extends Application {
    public static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }
}
