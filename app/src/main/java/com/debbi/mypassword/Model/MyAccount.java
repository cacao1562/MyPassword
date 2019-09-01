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

    public String date;

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return this.domain;
    }

}
