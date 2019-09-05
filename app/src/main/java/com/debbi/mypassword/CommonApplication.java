package com.debbi.mypassword;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.util.Log;

import com.debbi.mypassword.Utils.RSA_Cipher;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class CommonApplication extends Application {

    private static boolean deleteMode;

    public static void setMode(boolean mode) {
        deleteMode = mode;
    }

    public static boolean getMode() {
        return deleteMode;
    }

    public static String PackageName;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        PackageName = this.getPackageName();
    }

    public static String getDate(String format) {

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(c);

    }

}
