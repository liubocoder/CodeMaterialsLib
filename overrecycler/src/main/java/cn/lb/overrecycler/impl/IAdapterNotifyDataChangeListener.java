package cn.lb.overrecycler.impl;


import cn.lb.overrecycler.BaseRecyclerAdapter;

/**
 * 当调用RecyclerAdapter调用<code>notifyDataSetChanged()</code>触发<p/>
 *
 * Created by LiuBo on 2017-08-18.
 */

public interface IAdapterNotifyDataChangeListener {
    void onDataChanged(BaseRecyclerAdapter adapter);
}
