package com.debbi.mypassword.Activity;

import android.content.Intent;
import android.os.SystemClock;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

import org.w3c.dom.Text;

import io.reactivex.Observable;
import io.realm.Realm;
import io.realm.RealmResults;

public class InputActivity extends RxAppCompatActivity {

    private Button saveButton;
    private EditText input_site;
    private EditText input_id;
    private EditText input_pw;
    private EditText input_note;
    private Realm mRealm;
    private boolean isAddMode = false;
    private long mLastClickTime;
    private TextInputLayout id_layout;

    private String extra_id;
    private String extra_pw;
    private String extra_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        saveButton = findViewById(R.id.input_save_button);
        input_site = findViewById(R.id.input_site);
        input_id = findViewById(R.id.input_id);
        input_pw = findViewById(R.id.input_pw);
        input_note = findViewById(R.id.input_notes);
        id_layout = findViewById(R.id.main_id_layout1);

        mRealm = Realm.getDefaultInstance();

        if (!TextUtils.isEmpty(getIntent().getStringExtra("domain")) ) {
            input_site.setText(getIntent().getStringExtra("domain"));
            input_site.setEnabled(false);
            id_layout.setBoxBackgroundColor(getResources().getColor(R.color.gray_disable));
            isAddMode = true;
        }

        if (!TextUtils.isEmpty(getIntent().getStringExtra("id"))) {
            extra_id = getIntent().getStringExtra("id");
            input_id.setText(extra_id);
        }

        if (!TextUtils.isEmpty(getIntent().getStringExtra("pw"))) {
            extra_pw = getIntent().getStringExtra("pw");
            input_pw.setText(extra_pw);
        }

        if (!TextUtils.isEmpty(getIntent().getStringExtra("note"))) {
            extra_note = getIntent().getStringExtra("note");
            input_note.setText(extra_note);
        }

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

        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {

            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        mRealm.beginTransaction();

        AccountData accountData = new AccountData();
        accountData.setAccount(this,
                                input_id.getText().toString(),
                                input_pw.getText().toString(),
                                input_note.getText().toString(),
                                CommonApplication.getDate("yyyyMMddHHmmss")
                                );

        MyAccount results = mRealm.where(MyAccount.class).equalTo("domain", input_site.getText().toString()).findFirst();

        //detail 뷰에서 추가했을때 || 메인에서 이미 있는 이름으로 추가했을때 새로 만들지 않고 기존거에 추가
        if (isAddMode || (results != null) ) {

            if (!TextUtils.isEmpty(extra_id)) {

                for (AccountData data : results.accountData) {

                    if (data.getAccount_ID(this).equals(extra_id) ) {

                        data.setAccount(this, input_id.getText().toString(), input_pw.getText().toString(), input_note.getText().toString(), CommonApplication.getDate("yyyyMMddHHmmss"));
                    }
                }

            }else {

                results.accountData.add(accountData);
            }

        }else {

            MyAccount myAccount = mRealm.createObject(MyAccount.class);
            myAccount.setDomain(input_site.getText().toString());
            myAccount.accountData.add(accountData);
            myAccount.date = CommonApplication.getDate("yyyyMMddHHmmss");
        }

        mRealm.commitTransaction();

        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!mRealm.isClosed()) {
            mRealm.close();
        }
    }
}
