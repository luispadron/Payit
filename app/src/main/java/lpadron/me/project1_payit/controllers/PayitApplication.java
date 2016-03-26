package lpadron.me.project1_payit.controllers;

import android.app.Application;
import android.content.Context;
import android.support.v4.content.ContextCompat;

/**
 * Payit
 * lpadron.me.project1_payit.controllers
 * Created by Luis Padron on 3/25/16, at 8:39 PM
 */
public class PayitApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        PayitApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return PayitApplication.context;
    }
}
