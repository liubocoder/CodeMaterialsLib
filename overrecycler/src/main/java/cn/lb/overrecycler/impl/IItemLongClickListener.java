package cn.lb.overrecycler.impl;

import android.view.View;

import cn.lb.overrecycler.BaseHolderData;


/**
 * <p>RecyclerView条目长按事件回调</p>
 *
 * Created by LiuBo on 2016-08-24.
 */
public interface IItemLongClickListener<T extends BaseHolderData> {
    boolean onItemLongClick(View v, T data);
}
