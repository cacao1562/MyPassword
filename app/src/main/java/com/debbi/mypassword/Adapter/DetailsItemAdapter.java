package com.debbi.mypassword.Adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.debbi.mypassword.Activity.InputActivity;
import com.debbi.mypassword.Model.AccountData;
import com.debbi.mypassword.R;
import com.jakewharton.rxbinding2.view.RxView;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmList;

import static android.content.Context.CLIPBOARD_SERVICE;

public class DetailsItemAdapter extends RecyclerView.Adapter<DetailsItemAdapter.DetailsViewHolder> {

    private Context mContext;
    private RealmList<AccountData> mRealmList;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private int prePosition = -1;
    private String mDomain;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public DetailsItemAdapter(Context context, RealmList<AccountData> realmList, String domain) {
        this.mContext = context;
        this.mRealmList = realmList;
        this.mDomain = domain;
    }

    @NonNull
    @Override
    public DetailsItemAdapter.DetailsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.details_item, viewGroup, false);
        DetailsItemAdapter.DetailsViewHolder detailsViewHolder = new DetailsItemAdapter.DetailsViewHolder(view);
        return detailsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsItemAdapter.DetailsViewHolder detailsViewHolder, int position) {


        detailsViewHolder.id_textview.setText(mRealmList.get(position).getAccount_ID(mContext));
        detailsViewHolder.pw_textview.setText(mRealmList.get(position).getAccount_PW(mContext));
        detailsViewHolder.note_textview.setText(mRealmList.get(position).getAccount_Note(mContext));
        detailsViewHolder.date_textview.setText(mRealmList.get(position).getAccount_Date(mContext));

//        if (TextUtils.isEmpty(mRealmList.get(position).getAccount_Note(mContext))) {
//
//            detailsViewHolder.note_linear.setVisibility(View.GONE);
//            detailsViewHolder.note_textview.setVisibility(View.GONE);
//
//        }else {
//
//            detailsViewHolder.note_linear.setVisibility(View.VISIBLE);
////            detailsViewHolder.note_textview.setVisibility(View.GONE);
//            detailsViewHolder.note_linear.setVisibility(TextUtils.isEmpty(mRealmList.get(position).getAccount_Note(mContext)) ? View.GONE : View.VISIBLE );
//            detailsViewHolder.note_textview.setVisibility(selectedItems.get(position) ? View.VISIBLE : View.GONE);
//
//        }

        detailsViewHolder.note_linear.setVisibility(TextUtils.isEmpty(mRealmList.get(position).getAccount_Note(mContext)) ? View.GONE : View.VISIBLE );
        detailsViewHolder.note_textview.setVisibility(selectedItems.get(position) ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return mRealmList.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        Log.d("aaa", "Detail onDetachedFromRecyclerView");
        compositeDisposable.dispose();
//        super.onDetachedFromRecyclerView(recyclerView);
    }

    public class DetailsViewHolder extends RecyclerView.ViewHolder {

        private TextView date_textview;
        private TextView id_textview;
        private TextView pw_textview;
        private TextView option_menu_textview;
        private LinearLayout note_linear;
        private TextView note_textview;
        private ImageButton idCopyButton, pwCopyButton;

        public DetailsViewHolder(@NonNull View itemView) {
            super(itemView);

            this.date_textview = itemView.findViewById(R.id.detail_item_date_textview);
            this.id_textview = itemView.findViewById(R.id.detail_item_id_textview);
            this.pw_textview = itemView.findViewById(R.id.detail_item_pw_textview);
            this.option_menu_textview = itemView.findViewById(R.id.detail_item_option_textview);
            this.note_linear = itemView.findViewById(R.id.detail_item_notes_linear);
            this.note_textview = itemView.findViewById(R.id.detail_item_notes_textview);
            this.idCopyButton = itemView.findViewById(R.id.detail_item_id_imgButton);
            this.pwCopyButton = itemView.findViewById(R.id.detail_item_pw_imgButton);

            this.option_menu_textview.setOnClickListener(v -> {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, this.option_menu_textview);
                //inflating menu from xml resource
                popup.inflate(R.menu.detail_options);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.modify:
                                //handle menu1 click
                                Intent intent = new Intent(mContext, InputActivity.class);
                                intent.putExtra("domain", mDomain);
                                intent.putExtra("id", mRealmList.get(getAdapterPosition()).getAccount_ID(mContext));
                                intent.putExtra("pw", mRealmList.get(getAdapterPosition()).getAccount_PW(mContext));
                                intent.putExtra("note", mRealmList.get(getAdapterPosition()).getAccount_Note(mContext));
                                mContext.startActivity(intent);
                                break;

                            case R.id.delete:
                                //handle menu2 click
                                Realm realm = Realm.getDefaultInstance();
                                        realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        mRealmList.get(getAdapterPosition()).deleteFromRealm();
                                    }
                                });
                                        realm.close();
                                popup.dismiss();
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();


            });

            this.note_linear.setOnClickListener(v -> {

                if (selectedItems.get(getAdapterPosition())) {

                    selectedItems.delete(getAdapterPosition());

                }else {

                    selectedItems.delete(prePosition);
                    selectedItems.put(getAdapterPosition(), true);
                }
                // 해당 포지션의 변화를 알림
                if (prePosition != -1) {
                    notifyItemChanged(prePosition);
                }

                if (prePosition != getAdapterPosition()) {
                    notifyItemChanged(getAdapterPosition());
                }

                // 클릭된 position 저장
                prePosition = getAdapterPosition();

            });


            Observable<String> copyID = RxView.clicks(idCopyButton).map(str -> id_textview.getText().toString() );
            Observable<String> copyPW = RxView.clicks(pwCopyButton).map(str -> pw_textview.getText().toString() );

            Disposable disposable = Observable.merge(copyID, copyPW).subscribe(str -> copyToClipboard(str));
            compositeDisposable.add(disposable);

        }

        public void copyToClipboard(String str) {

            // 클립보드 복사
            ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("label", str );
            clipboardManager.setPrimaryClip(clipData);

            Toast.makeText(mContext,"Copied to the clipboard", Toast.LENGTH_SHORT).show();

        }
    }


}
