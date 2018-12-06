package cn.lb.overrecycler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.lb.overrecycler.impl.IAdapterNotifyDataChangeListener;

import static android.support.v7.widget.RecyclerView.NO_ID;

/**
 * 通用的RecyclerView Adapter，为BaseHolder提供onBindView()、onUnbindView()等;
 * 为BaseHolderData提供updateData()、getItemSpanSize()等
 *
 * Created by LiuBo on 2016-08-04.
 */
public class BaseRecyclerAdapter extends RecyclerView.Adapter<BaseHolder> {
    private BaseHolderFactory mHolderFactory;
    private IAdapterNotifyDataChangeListener mDataChangeListener;
    private RecyclerView mRecyclerView;
    private List<? extends BaseHolderData> mBackupListData;
    private int mScrollState = RecyclerView.SCROLL_STATE_IDLE;
    private boolean mRefreshEnable = true;

    protected List<BaseHolderData> mDatas = new ArrayList<>();

    public BaseRecyclerAdapter(@NonNull BaseHolderFactory factory) {
        mHolderFactory = factory;
        setHasStableIds(true);
    }

    /**
     * 设置数据变化回调
     *
     * @see #updateAndNotifyData(List)
     */
    public void setNotifyDataChangeListener(IAdapterNotifyDataChangeListener listener) {
        mDataChangeListener = listener;
    }

    /**
     * 仅更新数据，未刷新ui
     */
    public void updateData(@NonNull List<? extends BaseHolderData> datas) {
        if (isIdleScrollState() && isRefreshEnable()) {
            this.mDatas.clear();
            this.mDatas.addAll(datas);
        } else {
            mBackupListData = new ArrayList<>(datas);
        }
    }

    /**
     * 更新数据 并 刷新ui
     * @see #updateData(List)
     */
    public void updateAndNotifyData(@NonNull List<? extends BaseHolderData> datas) {
        if (isIdleScrollState() && isRefreshEnable()) {
            updateData(datas);
            notifyDataSetChanged();
            if (mDataChangeListener != null) {
                mDataChangeListener.onDataChanged(this);
            }
        } else {
            mBackupListData = new ArrayList<>(datas);
        }
    }

    /**
     * 比较数据 找到第一个不相同的item项 进行刷新
     * 强制刷新，会清除缓存
     * @param datas 待更新数据
     */
    public void updateRange(@NonNull List<? extends BaseHolderData> datas) {
        int firstDiffIdx = -1;
        List<BaseHolderData> list = new ArrayList<>(mDatas);

        for (int i = 0; i < list.size() && i < datas.size(); i++) {
            if (list.get(i) != datas.get(i)) {
                firstDiffIdx = i;
                break;
            }
        }
        if (firstDiffIdx <= 0) {
            updateAndNotifyData(datas);
        } else {
            mScrollState = RecyclerView.SCROLL_STATE_IDLE;
            mBackupListData = null;
            updateData(datas);

            int offsetOrc = list.size() - firstDiffIdx;
            int offsetDest = datas.size() - firstDiffIdx;
            int minOffset = Math.min(offsetOrc, offsetDest);

            notifyItemRangeChanged(firstDiffIdx, minOffset);
            if (offsetOrc > offsetDest) {
                notifyItemRangeRemoved(datas.size(), list.size());
            } else if (offsetOrc < offsetDest) {
                notifyItemRangeInserted(list.size(), datas.size());
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return hasStableIds() ? position : NO_ID;
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseHolder holder = mHolderFactory.buildHolder(parent, viewType);
        holder.mAdapter = this;
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        BaseHolderData data = getItemData(position);
        return data != null ? data.getLayoutId() : 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(@NonNull BaseHolder holder, int position) {
        holder.onBindView(getItemData(position), position);
    }

    @Override
    public void onViewRecycled(@NonNull BaseHolder holder) {
        super.onViewRecycled(holder);
        if (holder.mBindState) {
            holder.onUnbindView();
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (mRecyclerView != null) {
            mRecyclerView.removeOnScrollListener(mScrollListener);
        }
        mRecyclerView = recyclerView;
        mRecyclerView.removeOnScrollListener(mScrollListener);
        mRecyclerView.addOnScrollListener(mScrollListener);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (mRecyclerView != null) {
            mRecyclerView.removeOnScrollListener(mScrollListener);
        }
        mRecyclerView = null;
    }

    @Nullable
    public RecyclerView getAttachedRecycler() {
        return mRecyclerView;
    }

    /**
     * 获取position的数据
     * @param position 在adapter中的位置
     * @return null 如果无效位置
     */
    @Nullable
    public BaseHolderData getItemData(int position) {
        if (position >= 0 && position < mDatas.size()) {
            return mDatas.get(position);
        }
        return null;
    }

    /**
     * Grid布局时有效
     *
     * @see BaseHolderData#getItemSpanSize(int)
     */
    public int getItemSpanSize(int spanSize, int position) {
        if (position < 0 || position>= mDatas.size()) {
            return BaseHolderData.SF_DEF_ITEM_SPAN;
        }
        BaseHolderData data = getItemData(position);
        if (data != null) {
            return data.getItemSpanSize(spanSize);
        }
        return BaseHolderData.SF_DEF_ITEM_SPAN;//default value
    }

    /**
     * 当前是否是idle状态
     * @return true 是
     */
    protected boolean isIdleScrollState() {
        return mScrollState == RecyclerView.SCROLL_STATE_IDLE;
    }

    /**
     * 设置当前的滚动状态 可以是当前RecyclerView 或者子RecyclerView来设置
     */
    protected void setScrollState(int state) {
        mScrollState = state;
    }

    /**
     * 设置是否可以刷新，默认true
     * @param enable true 可刷新 false 不能刷新
     */
    public void setRefreshEnable(boolean enable) {
        mRefreshEnable = enable;
    }

    public boolean isRefreshEnable() {
        return mRefreshEnable;
    }

    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            mScrollState = newState;
            if (newState == RecyclerView.SCROLL_STATE_IDLE && mBackupListData != null) {
                updateAndNotifyData(mBackupListData);
                mBackupListData = null;
            }
        }
    };
}
