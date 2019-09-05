package com.debbi.mypassword.Activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageButton;
import android.widget.TextView;

import com.debbi.mypassword.Adapter.DetailsItemAdapter;
import com.debbi.mypassword.Model.MyAccount;
import com.debbi.mypassword.R;

import io.realm.Realm;
import io.realm.RealmResults;

public class DetailsActivity extends AppCompatActivity {

    private TextView title_textView, domain_textView;
    private Realm mRealm;
    private RecyclerView mRecyclerView;
    private ImageButton addButton;
    private ImageButton idCopyButton, pwCopyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setExitTransition(new Explode());
        setContentView(R.layout.activity_details);

        title_textView = findViewById(R.id.detail_title_textview);
        domain_textView = findViewById(R.id.detail_domain_textview);
        mRecyclerView = findViewById(R.id.detail_recyclerView);
        addButton = findViewById(R.id.detail_add_imgbutton);

        mRealm = Realm.getDefaultInstance();

        String domain = getIntent().getStringExtra("domain");
        title_textView.setText(String.valueOf(domain.charAt(0)).toUpperCase());
        domain_textView.setText(domain);

        RealmResults<MyAccount> myAccounts = mRealm.where(MyAccount.class).equalTo("domain", domain).findAll();


        DetailsItemAdapter detailsItemAdapter = new DetailsItemAdapter(this, myAccounts.get(0).accountData, domain);
        mRecyclerView.setAdapter(detailsItemAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRealm.addChangeListener(realm -> {
            detailsItemAdapter.notifyDataSetChanged();
        });

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, InputActivity.class);
            intent.putExtra("domain", domain);
            startActivity(intent);
        });

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finishAfterTransition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRecyclerView.setAdapter(null);

        if (!mRealm.isClosed()) {
            mRealm.close();
        }
    }
}
