package cn.lb.overrecycler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.lb.overrecycler.impl.IAdapterNotifyDataChangeListener;

/**
 * 通用的RecyclerView Adapter，为BaseHolder提供onBindView()、onUnbindView()等;
 * 为BaseHolderData提供updateData()、getItemSpanSize()等
 *
 * Created by LiuBo on 2016-08-04.
 */
public class BaseRecyclerAdapter extends RecyclerView.Adapter<BaseHolder> {
    private static final String TAG = "BaseRecyclerAdapter";
    protected BaseHolderFactory mHolderFactory;
    protected List<BaseHolderData> mDatas = new ArrayList<>();
    private IAdapterNotifyDataChangeListener mDataChangeListener;
    private RecyclerView mRecyclerView;

    public BaseRecyclerAdapter(@NonNull BaseHolderFactory factory) {
        mHolderFactory = factory;
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
        this.mDatas.clear();
        this.mDatas.addAll(datas);
    }

    /**
     * 更新数据 并 刷新ui
     * @see #updateData(List)
     */
    public void updateAndNotifyData(@NonNull List<? extends BaseHolderData> datas) {
        updateData(datas);
        notifyDataSetChanged();
        if (mDataChangeListener != null) {
            mDataChangeListener.onDataChanged(this);
        }
    }

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

    public void myNotifyItemRemoved(int pos) {
        if (pos >= 0 && pos < mDatas.size()) {
            mDatas.remove(pos);
            notifyItemRemoved(pos);
        }
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
    public void onBindViewHolder(BaseHolder holder, int position) {
        holder.onBindView(getItemData(position), position);
    }

    @Override
    public void onViewRecycled(BaseHolder holder) {
        super.onViewRecycled(holder);
        if (holder.mBindState) {
            holder.onUnbindView();
        }
        Log.d(TAG, "onViewRecycled: "+holder);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
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
}
