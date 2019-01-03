package com.app.grsonlineshopping.helper;

import android.app.Application;
import android.content.Context;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(mailTo = "shadowwsvinothkumar@gmail.com")
public class GRS extends Application {

    public static GRS mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        InternetAvailabilityChecker.init(this);
        mInstance = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        InternetAvailabilityChecker.getInstance().removeAllInternetConnectivityChangeListeners();
    }

    public static void freeMemory(){
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static boolean activityVisible;
}
