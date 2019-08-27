package com.debbi.mypassword.Activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.debbi.mypassword.CallbackItemclick;
import com.debbi.mypassword.CommonApplication;
import com.debbi.mypassword.Adapter.ItemAdapter;
import com.debbi.mypassword.Model.MyAccount;
import com.debbi.mypassword.R;
import com.debbi.mypassword.SpacesItemDecoration;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements CallbackItemclick {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView imageView;
    private ProgressBar progressBar;
    private CardView cardView;

    private FloatingActionButton add_floatingButton;

    private Realm mRealm;

    private LinearLayout bottomLinear;
    private Button itemRemoveButton, itemCloseButton;

    private String[] mRemoveArray;

    private RealmChangeListener mRealmListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main2);

        mRealm = Realm.getDefaultInstance();
        Log.d("aaa", "realm config = " + mRealm.getConfiguration());
        RealmResults<MyAccount> myAccounts = mRealm.where(MyAccount.class).findAll();
//        mRealm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                myAccounts.deleteAllFromRealm();
//            }
//        });

        intiBindViews();

        setSupportActionBar(toolbar);
//        toolbar.setTitle("");
        getSupportActionBar().setTitle(null);

        progressBar.setVisibility(View.VISIBLE);

        initServerImg();
        initRecyclerView();
        setFloatingButton();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        initOnclickItemButtons();

    }

    private void intiBindViews() {

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.main_recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        imageView = findViewById(R.id.main_imageView);
        progressBar = findViewById(R.id.main_progress);
        cardView = findViewById(R.id.item_layout_cardview);
        add_floatingButton = findViewById(R.id.main_floating_add_button);

        bottomLinear = findViewById(R.id.main_bottom_linear);
        itemRemoveButton = findViewById(R.id.main_item_remove_button);
        itemCloseButton = findViewById(R.id.main_item_close_button);
    }


    private void initServerImg() {

        Glide.with(this)
//                .load("http://35.243.82.86/captain_America.png")
                .load("http://35.243.82.86/bugatti.jpg")
//                .override(400,400)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);
    }


    private void initRecyclerView() {

        RealmResults<MyAccount> myAccounts = mRealm.where(MyAccount.class).findAll();

        itemAdapter = new ItemAdapter(this, myAccounts,this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(itemAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        mRealmListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {
                itemAdapter.notifyDataSetChanged();
            }
        };
        mRealm.addChangeListener(mRealmListener);
    }


    private void setFloatingButton() {

        add_floatingButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, InputActivity.class);
            startActivity(intent);
        });


    }


    @Override
    public void onclickItem(int index, View[] views, String domain) {

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                Pair.create(views[0], "itemCircle"),
                Pair.create(views[1], "itemText"));


        Intent in = new Intent(this,DetailsActivity.class);
        in.putExtra("domain", domain);
        startActivity(in, options.toBundle());
    }

    @Override
    public void onselectedremoveItem(int size, String[] selectedDomain) {

        if (add_floatingButton.getVisibility() == View.VISIBLE) {
            add_floatingButton.hide();
        }
        if (bottomLinear.getVisibility() == View.INVISIBLE) {
            bottomLinear.setVisibility(View.VISIBLE);
        }

        mRemoveArray = selectedDomain;

        itemRemoveButton.setText(size + " 개 항목 삭제하기");


//        if (size > 0) {
//
//            itemRemoveButton.setVisibility(View.VISIBLE);
//            itemRemoveButton.setText(size + " 개 항목 삭제하기");
//
//        }else {
//
//            itemRemoveButton.setVisibility(View.INVISIBLE);
//        }
    }

    private void initOnclickItemButtons() {

        itemRemoveButton.setOnClickListener(v -> {

            CommonApplication.setMode(false);

            Log.d("aaa", "mRemoveArray / length = " + mRemoveArray.length );
            for (String s : mRemoveArray) {
//                Log.d("aaa", "mRemoveArray item = " + s );
            }
            RealmResults<MyAccount> removeObj = mRealm.where(MyAccount.class).in("domain", mRemoveArray).findAll();
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    removeObj.deleteAllFromRealm();
                }
            });

            setDefaultMode();

        });

        itemCloseButton.setOnClickListener(v -> {

            setDefaultMode();
            itemAdapter.clearSelectedItem();
            itemAdapter.notifyDataSetChanged();
        });

    }


    private void setDefaultMode() {

        CommonApplication.setMode(false);

        if (bottomLinear.getVisibility() == View.VISIBLE) {
            bottomLinear.setVisibility(View.INVISIBLE);
        }
        if (add_floatingButton.getVisibility() == View.GONE) {
            add_floatingButton.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        recyclerView.setAdapter(null);
        mRealm.removeChangeListener(mRealmListener);
        mRealm.close();
    }
}
