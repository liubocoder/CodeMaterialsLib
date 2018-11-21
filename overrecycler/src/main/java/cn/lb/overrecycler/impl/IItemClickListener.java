package cn.lb.overrecycler.impl;

import android.view.View;

import cn.lb.overrecycler.BaseHolderData;


/**
 * <p>RecyclerView条目点击事件回调</p>
 *
 * @since Created by LiuBo on 2016-08-04.
 */
public interface IItemClickListener<T extends BaseHolderData> {
    void onItemClick(View v, T data);
}
