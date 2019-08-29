package com.debbi.mypassword.Adapter;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.debbi.mypassword.CallbackItemclick;
import com.debbi.mypassword.CommonApplication;
import com.debbi.mypassword.Model.MyAccount;
import com.debbi.mypassword.R;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.realm.RealmResults;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private Context mContext;
    private CallbackItemclick mCallback;
    private SparseBooleanArray mSelectedItems = new SparseBooleanArray(0);
    private RealmResults<MyAccount> mAccountsData;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    public ItemAdapter(Context mContext, RealmResults<MyAccount> myAccounts, CallbackItemclick callbackItemclick) {
        this.mContext = mContext;
        this.mAccountsData = myAccounts;
        this.mCallback = callbackItemclick;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout, viewGroup, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int position) {

        if (CommonApplication.getMode()) {
            itemViewHolder.constraintLayout.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.item_small));
            itemViewHolder.checkImageview.setVisibility(View.VISIBLE);
        }else {
            itemViewHolder.itemView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.item_scale_animation));
            itemViewHolder.checkImageview.setVisibility(View.INVISIBLE);
        }


        String domain = mAccountsData.get(position).getDomain();
        String title = String.valueOf(domain.charAt(0)).toUpperCase();

        itemViewHolder.titleTextView.setText(title);
        itemViewHolder.nameTextView.setText(domain);

        if (CommonApplication.getMode()) {

            itemViewHolder.itemView.setSelected(isItemSelected(position));

        }else {

            clearSelectedItem();
            itemViewHolder.itemView.setSelected(isItemSelected(position));
        }

    }



    @Override
    public int getItemCount() {
        return mAccountsData.size();
//        return 20;
    }

    public void setDataRefresh(List<String> list) {

    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        Log.d("aaa", "onDetachedFromRecyclerView");
        compositeDisposable.dispose();
//        super.onDetachedFromRecyclerView(recyclerView);
    }

    private long mLastClickTime;

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView nameTextView;
        private CardView cardView;
        private ConstraintLayout constraintLayout;
        private ImageButton checkImageview;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            this.titleTextView = itemView.findViewById(R.id.item_title_textView);
            this.nameTextView = itemView.findViewById(R.id.item_name_textView);
            this.cardView = itemView.findViewById(R.id.item_layout_cardview);
            this.constraintLayout = itemView.findViewById(R.id.item_layout_constraintlayout);
            this.checkImageview = itemView.findViewById(R.id.item_check_imagebutton);

//            itemView.setOnClickListener(v -> {
//
//
//                if (!CommonApplication.getMode()) {
//
//                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
//
//                        return;
//                    }
//                    mLastClickTime = SystemClock.elapsedRealtime();
//
//                }
//
//
//                if (CommonApplication.getMode()) {
//
//                    int position = getAdapterPosition();
//                    toggleItemSelected(position);
//                    mCallback.onselectedremoveItem(mSelectedItems.size());
//
//                }else {
//
//                    View[] views = {this.cardView, this.nameTextView};
//                    mCallback.onclickItem(getAdapterPosition(), views, mAccountsData.get(getAdapterPosition()).domain );
//                }
//
//            });

            Observable<Object> item = RxView.clicks(itemView);
            Observable<Object> check = RxView.clicks(this.checkImageview);
            Disposable disposable = Observable.merge(item, check).subscribe(e -> {

                if (!CommonApplication.getMode()) {

                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {

                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                }


                if (CommonApplication.getMode()) {

                    int position = getAdapterPosition();
                    toggleItemSelected(position);

                    mCallback.onselectedremoveItem(mSelectedItems.size(), getSeletedDomain() );

                }else {

                    View[] views = {this.cardView, this.nameTextView};
                    mCallback.onclickItem(getAdapterPosition(), views, mAccountsData.get(getAdapterPosition()).getDomain() );
                }

            }, Throwable::printStackTrace);

            compositeDisposable.add(disposable);


//            RxView.clicks(itemView)
//                    .throttleFirst(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(e -> {
//
//                        if (CommonApplication.getMode()) {
//
//                            int position = getAdapterPosition();
//                            toggleItemSelected(position);
//                            mCallback.onselectedremoveItem(mSelectedItems.size());
//
//                        }else {
//
//                            View[] views = {this.cardView, this.nameTextView};
//                            mCallback.onclickItem(getAdapterPosition(), views, mAccountsData.get(getAdapterPosition()).domain );
//                        }
//
//                    });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (!CommonApplication.getMode()) {

                        CommonApplication.setMode(true);

                        int position = getAdapterPosition();
                        toggleItemSelected(position);
                        mCallback.onselectedremoveItem(mSelectedItems.size(), getSeletedDomain() );
                        notifyDataSetChanged();
                    }

                    return true;
                }
            });

        }

    }

    private void toggleItemSelected(int position) {

        if (mSelectedItems.get(position, false)) {
            mSelectedItems.delete(position);
            notifyItemChanged(position);
//            if (mSelectedItems.size() == 0) {
//                CommonApplication.setMode(false);
//            }

        } else {
            mSelectedItems.put(position, true);
            notifyItemChanged(position);

        }
    }

    private boolean isItemSelected(int position) {
        return mSelectedItems.get(position, false);
    }

    private String[] getSeletedDomain() {

        List<String> seletedDomain = new ArrayList<>();

        for (int i=0;  i<mSelectedItems.size(); i++) {
            int itemIndex = mSelectedItems.keyAt(i);
            seletedDomain.add(mAccountsData.get(itemIndex).getDomain() );
        }
        return seletedDomain.toArray(new String[seletedDomain.size()]);
    }

    public void clearSelectedItem() {
        int position;

        for (int i = 0; i < mSelectedItems.size(); i++) {
            position = mSelectedItems.keyAt(i);
            mSelectedItems.put(position, false);
//            notifyItemChanged(position);
        }

        mSelectedItems.clear();
    }

}
