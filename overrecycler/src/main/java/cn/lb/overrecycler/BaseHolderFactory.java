package cn.lb.overrecycler;

import android.view.ViewGroup;

/**
 * Created by LiuBo on 2016-08-05.
 */
public abstract class BaseHolderFactory {
    abstract public BaseHolder buildHolder(ViewGroup parent, int viewLayout);
}
