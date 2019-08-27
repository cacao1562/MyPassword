package com.debbi.mypassword;

import android.view.View;

import java.util.List;

public interface CallbackItemclick {

    /** 삭제 모드 아닐때 detail 뷰로 이동 */
    void onclickItem(int index, View[] views, String domain);

    /** 삭제 모드 일때 클릭한 개수 넘겨줌 */
    void onselectedremoveItem(int size, String[] selectedDomain);
}
