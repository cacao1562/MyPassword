package com.debbi.mypassword.Model;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Required;

public class MyAccount extends RealmObject {

    @Required
    public String domain;

    public RealmList<AccountData> accountData;
}
