package cn.lb.overrecycler;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * 基础的ViewHolder
 * Created by LiuBo on 2016-08-04.
 */
public class BaseHolder<T extends BaseHolderData> extends RecyclerView.ViewHolder {
    public BaseHolder(View itemView) {
        super(itemView);
    }

    /** 当前的adapter */
    BaseRecyclerAdapter mAdapter;
    /** 当前是否绑定 */
    boolean mBindState;
    /** 当前位置 */
    private int mBindPosition = RecyclerView.NO_POSITION;
    /** 当前绑定的数据 */
    private T mData;

    /** 绑定数据 刷新显示，可能多次调用 */
    public void onBindView(T data, int position) {
        this.mBindState = true;
        this.mBindPosition = position;
        this.mData = data;

        itemView.setOnClickListener(getItemClickListener());
        itemView.setOnLongClickListener(getItemLongClickListener());

        data.mHolder = this;
    }

    /**
     * 取消绑定, 这个holder将不能与用户交互<br/>
     * 销毁页面才会调用holder的onUnbindView</i>
     */
    public void onUnbindView() {
        mBindState = false;
        mBindPosition = getAdapterPosition();
    }

    public T getBindData() {
        return mData;
    }

    /**
     * 自定义绘制线条, true自定义绘制
     *
     * @param paint 相关属性已设置，如果有自定义的属性，可以考虑在holder中new paint
     */
    public boolean drawDecoration(Canvas canvas, RecyclerView parent, int index, Paint paint) {
        return false;
    }

    /** 通知数据改变 */
    public final void notifyDataChanged() {
        if (isBindView() && mAdapter != null) {
            mAdapter.notifyItemChanged(mBindPosition);
        }
    }

    public final boolean isBindView() {
        return mBindState;
    }

    /**
     * 获取整个item的点击事件
     *
     * @return OnClickListener
     */
    protected View.OnClickListener getItemClickListener() {
        if (mData.mItemClickListener != null) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    T bindData = getBindData();
                    if (bindData != null && bindData.mItemClickListener != null) {
                        bindData.mItemClickListener.onItemClick(v, bindData);
                    }
                }
            };
        } else {
            return null;
        }
    }

    protected View.OnLongClickListener getItemLongClickListener() {
        if (mData.mItemLongClickListener != null) {
            return new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    T bindData = getBindData();
                    if (bindData != null && bindData.mItemLongClickListener != null) {
                        return bindData.mItemLongClickListener.onItemLongClick(v, bindData);
                    }
                    return false;
                }
            };
        } else {
            return null;
        }
    }

    /**
     * 找到view
     *
     * @param id  子控件id
     * @param <T> 子控件类型
     * @return view
     * @see View#findViewById(int)
     */
    @SuppressWarnings("all")
    protected final <T extends View> T findViewById(int id) {
        return (T) itemView.findViewById(id);
    }

    /**
     * 找到view
     *
     * @param id  子控件id
     * @param container  父控件
     * @param <T> 子控件类型
     * @return view
     * @see View#findViewById(int)
     */
    @SuppressWarnings("all")
    protected final <T extends View> T findViewById(@NonNull View container, int id) {
        return (T) container.findViewById(id);
    }
}
