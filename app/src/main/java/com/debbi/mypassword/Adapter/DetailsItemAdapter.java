package com.debbi.mypassword.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.debbi.mypassword.Model.AccountData;
import com.debbi.mypassword.R;

import io.realm.RealmList;

public class DetailsItemAdapter extends RecyclerView.Adapter<DetailsItemAdapter.DetailsViewHolder> {

    private Context mContext;
    private RealmList<AccountData> mRealmList;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private int prePosition = -1;

    public DetailsItemAdapter(Context context, RealmList<AccountData> realmList) {
        this.mContext = context;
        this.mRealmList = realmList;
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

        detailsViewHolder.date_textview.setText(mRealmList.get(position).date);
        detailsViewHolder.id_textview.setText(mRealmList.get(position).id);
        detailsViewHolder.pw_textview.setText(mRealmList.get(position).pw);
        detailsViewHolder.note_textview.setText(mRealmList.get(position).note);

        if (TextUtils.isEmpty(mRealmList.get(position).note)) {

            detailsViewHolder.note_linear.setVisibility(View.GONE);
            detailsViewHolder.note_textview.setVisibility(View.GONE);

        }else {

            detailsViewHolder.note_linear.setVisibility(View.VISIBLE);
            detailsViewHolder.note_textview.setVisibility(View.GONE);

            detailsViewHolder.note_textview.setVisibility(selectedItems.get(position) ? View.VISIBLE : View.GONE);

        }

    }

    @Override
    public int getItemCount() {
        return mRealmList.size();
    }

    public class DetailsViewHolder extends RecyclerView.ViewHolder {

        private TextView date_textview;
        private TextView id_textview;
        private TextView pw_textview;
        private TextView option_menu_textview;
        private LinearLayout note_linear;
        private TextView note_textview;

        public DetailsViewHolder(@NonNull View itemView) {
            super(itemView);

            this.date_textview = itemView.findViewById(R.id.detail_item_date_textview);
            this.id_textview = itemView.findViewById(R.id.detail_item_id_textview);
            this.pw_textview = itemView.findViewById(R.id.detail_item_pw_textview);
            this.option_menu_textview = itemView.findViewById(R.id.detail_item_option_textview);
            this.note_linear = itemView.findViewById(R.id.detail_item_notes_linear);
            this.note_textview = itemView.findViewById(R.id.detail_item_notes_textview);

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
                                break;
                            case R.id.delete:
                                //handle menu2 click
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
                if (prePosition != -1) notifyItemChanged(prePosition);
                notifyItemChanged(getAdapterPosition());
                // 클릭된 position 저장
                prePosition = getAdapterPosition();

            });

        }
    }


}
