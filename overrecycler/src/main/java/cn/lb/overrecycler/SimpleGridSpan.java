package cn.lb.overrecycler;

import android.support.v7.widget.GridLayoutManager;

/**
 * Created by LiuBo on 2016-11-25.
 */

public class SimpleGridSpan extends GridLayoutManager.SpanSizeLookup {
    private BaseRecyclerAdapter mAdapter;
    private int mSpanSize;
    public SimpleGridSpan(BaseRecyclerAdapter adapter, int spanSize) {
        this.mAdapter = adapter;
        this.mSpanSize = spanSize;
    }

    @Override
    public int getSpanSize(int position) {
        return mAdapter.getItemSpanSize(mSpanSize, position);
    }
}
