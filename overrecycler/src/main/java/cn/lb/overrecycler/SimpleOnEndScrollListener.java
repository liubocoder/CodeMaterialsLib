package cn.lb.overrecycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import cn.lb.overrecycler.impl.IAdapterNotifyDataChangeListener;

/**
 * <p>
 * 用于监听RecyclerView是否滚动到End
 * <i>需要LayoutManager是LinearLayoutManager或者其子类</i>
 * </p>
 * Created by LiuBo on 2017-08-18.
 */

public abstract class SimpleOnEndScrollListener extends RecyclerView.OnScrollListener implements IAdapterNotifyDataChangeListener {

    private RecyclerView mRecyclerView;
    private boolean mScrollDown = true;

    /**
     * @param recyclerView 需要设置Adapter
     */
    public SimpleOnEndScrollListener(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        BaseRecyclerAdapter adapter = (BaseRecyclerAdapter) mRecyclerView.getAdapter();

        if (adapter != null) {
            adapter.setNotifyDataChangeListener(this);
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            checkLastItem(mScrollDown);
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        mScrollDown = (dy >= 0);
    }

    private void checkLastItem(boolean scrollDown) {
        RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
        if (lm instanceof LinearLayoutManager) {
            LinearLayoutManager manager = (LinearLayoutManager) lm;
            int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
            onEndScroll(lastVisibleItem == (manager.getItemCount() - 1) && scrollDown);
        }
    }

    @Override
    public void onDataChanged(BaseRecyclerAdapter adapter) {
        checkLastItem(true);
    }

    public abstract void onEndScroll(boolean isReachEnd);
}
