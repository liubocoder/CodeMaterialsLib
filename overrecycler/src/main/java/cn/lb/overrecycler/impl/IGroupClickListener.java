package cn.lb.overrecycler.impl;

import android.view.View;

import cn.lb.overrecycler.BaseHolderData;


/**
 * <p>RecyclerView的可展开条目的Group点击事件回调</p>
 *
 * Created by LiuBo on 2016-08-04.
 */
public interface IGroupClickListener<T extends BaseHolderData> {
    void onGroupItemClick(View v, T data);
}
