package com.debbi.mypassword;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

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

    private static boolean restoreMode;
    public static void setRestoreMode(boolean mode) {
        restoreMode = mode;
    }
    public static boolean getRestorMode() {
        return restoreMode;
    }

    public static String PackageName;

    private static CommonApplication mCommon;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        PackageName = this.getPackageName();
        mCommon = this;
    }

    public static CommonApplication getContext() {
        return mCommon;
    }

    public static String getDate(String format) {

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(c);

    }

    public void showAlert(String title , String msg , final Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogStyle);
        builder.setTitle(title);
        builder.setMessage(msg);
//        builder.setNegativeButton("취소", null);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
//                ((Activity)context).finish();
            }
        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
