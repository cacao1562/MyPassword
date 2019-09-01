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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
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


import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

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
    private android.support.v7.widget.SearchView searchView;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.search, toolbar.getMenu());
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search ");
//        searchView.setIconified(false);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("aaa", "onClick = " );
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d("aaa", "onClose = " );
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("aaa", "onFocusChange = " + hasFocus + " isIconified" + searchView.isIconified() );
            }
        });
        searchView.setOnQueryTextListener(getOnQueryTextListener());

        return true;
    }

    private SearchView.OnQueryTextListener getOnQueryTextListener() {

        return new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("aaa", "onQueryTextChange = " + s);
                Disposable disposable = Observable.just(s)
//                                                .debounce(2, TimeUnit.SECONDS)
//                        .filter(str -> !TextUtils.isEmpty(str))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(t -> {
                            Log.d("aaa", "ttt = " + t);
                            if (TextUtils.isEmpty(t)) {

                            }else {

                                RealmResults<MyAccount> result = mRealm.where(MyAccount.class).like("domain", "*" + t + "*").findAll();
                                itemAdapter.setDataRefresh(result);
//                                                    if (result.size() == 0 || result == null) {
//                                                        RealmResults<MyAccount> result2 = mRealm.where(MyAccount.class).findAll();
//                                                        for ( MyAccount myAccount : result2) {
//                                                            myAccount.accountData.where().like("id", "*"+t+"*").like("pw", "*"+t+"*").like("note","*"+t+"*").findAll();
//                                                        }
//                                                    }
                            }

                        });

                compositeDisposable.add(disposable);

                return false;
            }
        };
    }


    @Override
    public void onBackPressed() {

        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        }else {
            super.onBackPressed();
        }

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

        RealmResults<MyAccount> myAccounts = mRealm.where(MyAccount.class).findAll().sort("date", Sort.DESCENDING);

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

            mRemoveArray = null;

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

        /** Adapter 에 null을 줘야 onDetachedFromRecyclerView 호출됨 */
        recyclerView.setAdapter(null);
        compositeDisposable.dispose();

        if (!mRealm.isClosed()) {
            mRealm.removeChangeListener(mRealmListener);
            mRealm.close();
        }

    }
}
