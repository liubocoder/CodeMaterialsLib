package cn.lb.overrecycler.impl;

import android.support.annotation.NonNull;

/**
 * Created by liubo on 2017/9/22.
 */

public interface ICheckStateSet {
    void setChecked(@NonNull CheckState check);
    @NonNull
    CheckState getChecked();
    void setCheckListener(@NonNull ICheckListener listener);
}
