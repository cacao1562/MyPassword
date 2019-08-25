package com.debbi.mypassword;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildLayoutPosition(view);

        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        if (position == 0 || position == 1) {

            outRect.top = space;

        } else {

            outRect.top = 0;
        }

        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();

        int spanIndex = lp.getSpanIndex();

        if(spanIndex == 0) {
            //왼쪽 아이템

//            if (CommonApplication.getMode()) {
//                outRect.left = space * 2;
//                outRect.right = space ;
//            }else {
//                outRect.left = space;
//                outRect.right = space / 2;
//            }

            outRect.left = space;
            outRect.right = space / 2;


        } else if(spanIndex == 1) {
            //오른쪽 아이템

//            if (CommonApplication.getMode()) {
//                outRect.left = space;
//                outRect.right = space * 2;
//            }else {
//                outRect.left = space / 2;
//                outRect.right = space;
//            }

            outRect.left = space / 2;
            outRect.right = space;

        }

    }
}