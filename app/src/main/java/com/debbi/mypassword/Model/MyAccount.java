package com.debbi.mypassword.Model;

import android.content.Context;
import android.util.Log;

import com.debbi.mypassword.Utils.RSA_Cipher;
import com.debbi.mypassword.Utils.RsaUtil;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Required;

public class MyAccount extends RealmObject {

    @Required
    private String domain;

    public RealmList<AccountData> accountData;

//    public void setDomain(Context context, String domain) {
//
//        try {
//
//            String enDomain = RsaUtil.encryptString(context, domain);
//            this.domain = enDomain;
////            Log.d("aaa", "setDomain domain = " + domain );
////            Log.d("aaa", "setDomain  enDomain = " + enDomain );
//
//        }catch (Exception e) {
//
//            Log.d("aaa", "Exception setDomain = " + e.toString() );
//        }
//
//    }
//
//    public String getDomain(Context context) {
//
//        try {
//            String deDomain = RsaUtil.decryptString(context, this.domain);
////            Log.d("aaa", "getDomain domain = " + this.domain );
////            Log.d("aaa", "getDomain  deDomain = " + deDomain );
//            return deDomain;
//        }catch (Exception e) {
//
//            Log.d("aaa", "Exception getDomain = " + e.toString() );
//        }
//        return null;
//    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return this.domain;
    }

}
