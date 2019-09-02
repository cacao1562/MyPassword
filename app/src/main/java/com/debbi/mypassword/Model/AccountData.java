package com.debbi.mypassword.Model;

import android.content.Context;
import android.util.Log;

import com.debbi.mypassword.Utils.RsaUtil;

import io.realm.RealmObject;

public class AccountData extends RealmObject {

    private String id;
    private String pw;
    private String note;
    private String date;

    public void setAccount(Context context, String id, String pw, String note, String date) {

        try {

            this.id = RsaUtil.encryptString(context, id);
            this.pw = RsaUtil.encryptString(context, pw);
            this.note = RsaUtil.encryptString(context, note);
            this.date = RsaUtil.encryptString(context, date);

        }catch (Exception e) {

            Log.d("aaa", "AccountData setAccount Exception = " + e.toString());
        }
    }


    public String getAccount_ID(Context context) {

        try {

            return RsaUtil.decryptString(context, this.id);

        }catch (Exception e) {

            Log.d("aaa", "AccountData getAccount_ID Exception = " + e.toString());
        }

        return null;
    }

    public String getAccount_PW(Context context) {

        try {

            return RsaUtil.decryptString(context, this.pw);

        }catch (Exception e) {

            Log.d("aaa", "AccountData getAccount_PW Exception = " + e.toString());
        }

        return null;
    }


    public String getAccount_Note(Context context) {

        try {

            return RsaUtil.decryptString(context, this.note);

        }catch (Exception e) {

            Log.d("aaa", "AccountData getAccount_Note Exception = " + e.toString());
        }

        return null;
    }


    public String getAccount_Date(Context context) {

        try {

            return RsaUtil.decryptString(context, this.date);

        }catch (Exception e) {

            Log.d("aaa", "AccountData getAccount_Date Exception = " + e.toString());
        }

        return null;
    }


    public String getstr(Context context) {

        String str = "";
        str = str + getAccount_ID(context);
        str = str + getAccount_PW(context);
        str = str + getAccount_Note(context);
        return str;
    }

}
