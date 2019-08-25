package com.debbi.mypassword;

import android.app.Application;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;

public class CommonApplication extends Application {

    private static boolean deleteMode;

    public static void setMode(boolean mode) {
        deleteMode = mode;
    }

    public static boolean getMode() {
        return deleteMode;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
    }

    public static String getDate(String format) {

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(c);

    }
}
