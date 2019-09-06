package com.debbi.mypassword.Activity;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.debbi.mypassword.Adapter.ItemAdapter;
import com.debbi.mypassword.CallbackItemclick;
import com.debbi.mypassword.CommonApplication;
import com.debbi.mypassword.Model.AccountData;
import com.debbi.mypassword.Model.MyAccount;
import com.debbi.mypassword.R;
import com.debbi.mypassword.SpacesItemDecoration;
import com.debbi.mypassword.Utils.RealmBackupRestore;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jakewharton.rxbinding2.widget.RxSearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
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
    private TextView noResultTextview;
    private AppBarLayout appBarLayout;

    private FloatingActionButton add_floatingButton, backup_floatingButton;

    private Realm mRealm;

    private LinearLayout bottomLinear;
    private Button itemRemoveButton, itemCloseButton;

    private String[] mRemoveArray;

    private RealmChangeListener mRealmListener;
    private android.widget.SearchView searchView;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private RealmResults<MyAccount> mMyAccounts;

    private boolean isAppbarFocus;

    private RealmBackupRestore mBackup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main2);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }
                    , 1000);
        } else {

        }

        mRealm = Realm.getDefaultInstance();
        Log.d("aaa", "realm config = " + mRealm.getConfiguration());
        mBackup = new RealmBackupRestore(this);


        mMyAccounts = mRealm.where(MyAccount.class).findAll().sort("date", Sort.DESCENDING);;
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

        progressBar.setVisibility(View.VISIBLE);

        initServerImg();
        initRecyclerView();
        setFloatingButton();

//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });

        initOnclickItemButtons();

        backup_floatingButton.setOnClickListener(v -> {
//            mBackup.backup();
            mBackup.restore();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }


                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.search, toolbar.getMenu());
        MenuItem searchItem = menu.findItem(R.id.action_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        searchView = (android.widget.SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search ");
//        searchView.setIconified(false);
        searchView.setOnCloseListener(new android.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d("aaa", "onClose = ");
                setShowNoResult(false);
                itemAdapter.setDataRefresh(getcurrentList());
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("aaa", "onClick = ");
            }
        });
//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                Log.d("aaa", "onClose = ");
//                setShowNoResult(false);
//                itemAdapter.setDataRefresh(getcurrentList());
//                return false;
//            }
//        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("aaa", "onFocusChange = " + hasFocus + " isIconified  =  " + searchView.isIconified());
                if (hasFocus) {
                    isAppbarFocus = true;
                    appBarLayout.setExpanded(false);
                }else {
                    isAppbarFocus = false;
                    appBarLayout.setExpanded(true);
                }
            }
        });
//        searchView.setOnQueryTextListener(getOnQueryTextListener());
        RxSearchView.queryTextChanges(searchView)
                .debounce(1, TimeUnit.SECONDS) // stream will go down after 1 second inactivity of user
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(@NonNull CharSequence charSequence) throws Exception {
                        // perform necessary operation with `charSequence`

                        Log.d("aaa", "accept = " + charSequence.toString() );
                        findInputText(charSequence.toString());
                    }
                });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home :
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findInputText(String str) {

        if (TextUtils.isEmpty(str)) {

            if (isAppbarFocus) {
                setShowNoResult(true);
                itemAdapter.setDataRefresh(new ArrayList<>());
            }

            return;
        }
//                            RealmResults<MyAccount> result = mRealm.where(MyAccount.class).like("domain", "*" + t + "*").findAll();
        RealmResults<MyAccount> result = mMyAccounts.where().like("domain", "*" + str + "*").findAll();
        List<MyAccount> list = mRealm.copyFromRealm(result);
        itemAdapter.setDataRefresh(list);

        if (result.size() > 0) {

            setShowNoResult(false);

        } else {

            List<MyAccount> findAccount = new ArrayList<>();

            for (MyAccount myAccount : mMyAccounts) {

                for (AccountData accountData : myAccount.accountData) {

                    if (accountData.getstr(MainActivity.this).contains(str) ) {

                        findAccount.add(myAccount);
                    }
                }
            }

            itemAdapter.setDataRefresh(findAccount);

            if (findAccount.size() > 0) {

                setShowNoResult(false);

            }else {

                setShowNoResult(true);
            }

        }
    }

    private void setShowNoResult(boolean show) {

        if (show) {

            noResultTextview.setVisibility(View.VISIBLE);

        } else {

            noResultTextview.setVisibility(View.GONE);
        }
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
                                setShowNoResult(true);
                                return;
                            }
//                            RealmResults<MyAccount> result = mRealm.where(MyAccount.class).like("domain", "*" + t + "*").findAll();
                            RealmResults<MyAccount> result = mMyAccounts.where().like("domain", "*" + t + "*").findAll();
                            List<MyAccount> list = mRealm.copyFromRealm(result);
                            itemAdapter.setDataRefresh(list);

                            if (result.size() > 0) {

                                setShowNoResult(false);

                            } else {

                                List<MyAccount> findAccount = new ArrayList<>();

                                for (MyAccount myAccount : mMyAccounts) {

                                    for (AccountData accountData : myAccount.accountData) {

                                        if (accountData.getstr(MainActivity.this).contains(t) ) {

                                            findAccount.add(myAccount);
                                        }
                                    }
                                }

                                itemAdapter.setDataRefresh(findAccount);

                                if (findAccount.size() > 0) {

                                    setShowNoResult(false);

                                }else {

                                    setShowNoResult(true);
                                }

                            }

//                                                    if (result.size() == 0 || result == null) {
//                                                        RealmResults<MyAccount> result2 = mRealm.where(MyAccount.class).findAll();
//                                                        for ( MyAccount myAccount : result2) {
//                                                            myAccount.accountData.where().like("id", "*"+t+"*").like("pw", "*"+t+"*").like("note","*"+t+"*").findAll();
//                                                        }
//                                                    }


                        });

                compositeDisposable.add(disposable);

                return false;
            }
        };
    }


    @Override
    public void onBackPressed() {

        if (!searchView.isIconified()) {

            searchView.setIconified(true); // 서치뷰 아이콘으로 축소
            setShowNoResult(false);
            itemAdapter.setDataRefresh(getcurrentList());
            appBarLayout.setExpanded(true);

        } else if (CommonApplication.getMode()) {

            setDefaultMode();
            itemAdapter.clearSelectedItem();
            itemAdapter.notifyDataSetChanged();

        }else {

            super.onBackPressed();
        }

    }

    private void intiBindViews() {

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.main_recyclerView);
//        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        imageView = findViewById(R.id.main_imageView);
        progressBar = findViewById(R.id.main_progress);
        cardView = findViewById(R.id.item_layout_cardview);

        add_floatingButton = findViewById(R.id.main_floating_add_button);
        backup_floatingButton = findViewById(R.id.main_floating_backup_button);

        bottomLinear = findViewById(R.id.main_bottom_linear);
        itemRemoveButton = findViewById(R.id.main_item_remove_button);
        itemCloseButton = findViewById(R.id.main_item_close_button);

        noResultTextview = findViewById(R.id.main_no_result_textview);
        appBarLayout = findViewById(R.id.AppBar);
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

//        RealmResults<MyAccount> myAccounts = mRealm.where(MyAccount.class).findAll().sort("date", Sort.DESCENDING);


        itemAdapter = new ItemAdapter(this, getcurrentList(), this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(itemAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        mRealmListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {
                itemAdapter.setDataRefresh(getcurrentList());
//                itemAdapter.notifyDataSetChanged();
            }
        };
        mRealm.addChangeListener(mRealmListener);
    }

    private List<MyAccount> getcurrentList() {
        return mRealm.copyFromRealm(mMyAccounts);
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


        Intent in = new Intent(this, DetailsActivity.class);
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


            Log.d("aaa", "mRemoveArray / length = " + mRemoveArray.length);
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
    protected void onResume() {
        super.onResume();

        if (CommonApplication.getRestorMode()) {

            Log.d("MainActivity", "onResume");
            CommonApplication.setRestoreMode(false);
//            mRealm = Realm.getDefaultInstance();

//            Realm defaultRealm = Realm.getDefaultInstance();
//            mRealm.close();
//            // Delete default realm file
////            Realm.deleteRealm(Realm.getDefaultInstance().getConfiguration());
//
//            RealmConfiguration conf = new RealmConfiguration.Builder()
//                    .deleteRealmIfMigrationNeeded()
//                    .build();
//            Realm.setDefaultConfiguration(conf);
////            mRealm.close();
//            mRealm = Realm.getInstance(conf);
//            mMyAccounts = mRealm.where(MyAccount.class).findAll();;
////            Log.d("MainActivity", "result size = " + mMyAccounts.size() );
////            itemAdapter.notifyDataSetChanged();
//            itemAdapter.setDataRefresh(getcurrentList());
//            mRealm.refresh();



            // Restore default from backup file
//            Realm backup = Realm.getInstance(MyApplication.getBackupConfiguration());
//            File defaultDB = new File(realmPath(), MyApplication.REALM_NAME_DEFAULT);
//            try {
//                backup.writeCopyTo(defaultDB);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            backup.close();

            // Reboot app
//            Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
//            int mPendingIntentId = 123456;
//            PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//            AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//            System.exit(0);


            PackageManager packageManager = this.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(this.getPackageName());
            ComponentName componentName = intent.getComponent();
            Intent mainIntent = Intent.makeRestartActivityTask(componentName);
//            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(mainIntent);
            Runtime.getRuntime().exit(0);
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
