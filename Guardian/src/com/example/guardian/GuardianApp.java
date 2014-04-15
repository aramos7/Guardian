package com.example.guardian;

/**
 * Created by Samarth on 4/14/14.
 */

import android.app.Application;

public class GuardianApp extends Application {

    private static GuardianApp instance;

    public GuardianApp() {
        instance = this;
    }

    public static GuardianApp getApplication() {
        return instance;
    }

}
