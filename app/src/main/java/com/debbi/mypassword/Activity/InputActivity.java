package com.debbi.mypassword.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.debbi.mypassword.CommonApplication;
import com.debbi.mypassword.Model.AccountData;
import com.debbi.mypassword.Model.MyAccount;
import com.debbi.mypassword.R;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import io.reactivex.Observable;
import io.realm.Realm;

public class InputActivity extends RxAppCompatActivity {

    private Button saveButton;
    private EditText input_site;
    private EditText input_id;
    private EditText input_pw;
    private EditText input_note;
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        saveButton = findViewById(R.id.input_save_button);
        input_site = findViewById(R.id.input_site);
        input_id = findViewById(R.id.input_id);
        input_pw = findViewById(R.id.input_pw);
        input_note = findViewById(R.id.input_notes);

        mRealm = Realm.getDefaultInstance();

        Observable<TextViewTextChangeEvent> site_obs = RxTextView.textChangeEvents(input_site);
        Observable<TextViewTextChangeEvent> id_obs = RxTextView.textChangeEvents(input_id);
        Observable<TextViewTextChangeEvent> pw_obs = RxTextView.textChangeEvents(input_pw);


        Observable.combineLatest(
                site_obs,
                id_obs,
                pw_obs,
                (item1, item2, item3) -> item1.text().length() > 0 && item2.text().length() > 0 && item3.text().length() > 0)
                .compose(bindToLifecycle())
                .subscribe(flag -> saveButton.setEnabled(flag));


        RxView.clicks(saveButton)
                .compose(bindToLifecycle())
                .subscribe(e -> {
                    saveData();
                }, Throwable::printStackTrace);
    }

    private void saveData() {

        mRealm.beginTransaction();

        MyAccount myAccount = mRealm.createObject(MyAccount.class);
        myAccount.domain = input_site.getText().toString();
        AccountData accountData = new AccountData();
        accountData.id = input_id.getText().toString();
        accountData.pw = input_pw.getText().toString();
        accountData.note = input_note.getText().toString();
        accountData.date = CommonApplication.getDate("yyyyMMddHHmmss"); //"yyyy-MM-dd HH:mm:ss"
        myAccount.accountData.add(accountData);

        mRealm.commitTransaction();

//        finish();
    }

}
