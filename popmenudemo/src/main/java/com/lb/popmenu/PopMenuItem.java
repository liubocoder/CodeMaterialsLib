package com.lb.popmenu;

import android.text.TextUtils;

public class PopMenuItem {

    public int mIconRes;
    public String mItem;
    /** 是否是向上弹出 */
    public boolean isPopUp;

    /**
     * 创建一个新的PopMenuItem对象
     * @param item
     */
    public PopMenuItem(String item){
        this.mItem = item;
    }

    /**
     * 创建一个新的PopMenuItem对象
     * @param iconRes
     * @param item
     */
    public PopMenuItem(int iconRes, String item) {
        this.mIconRes = iconRes;
        this.mItem = item;
    }

    /**
     * 判断两个PopMenuItem对象是否相同，暂时只比较mItem。重写该方法用于添加多项时判重。
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        PopMenuItem item = (PopMenuItem) o;
        if (item != null && !TextUtils.isEmpty(item.mItem) && item.mItem.equals(this.mItem)) {
            return true;
        }

        return false;
    }

    public boolean isPopUp() {
        return isPopUp;
    }

    public void setPopUp(boolean popUp) {
        isPopUp = popUp;
    }
}
