package cn.lb.overrecycler;


import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.lb.overrecycler.impl.IItemClickListener;
import cn.lb.overrecycler.impl.IItemLongClickListener;
import cn.lb.overrecycler.impl.IItemSpanSize;

/**
 * RecyclerView基类的数据源
 *
 * <p>实现方案1.实现有参构造<code>BaseHolderData(@LayoutRes int)</code></p>
 * <p>实现方案2.重写<code>getLayoutId()</code>方法</p>
 *
 * Created by LiuBo on 2016-08-04.
 */
public abstract class BaseHolderData implements IItemSpanSize {
    public static final int SF_DEF_ITEM_SPAN = 1;
    private int mLayoutId;

    public BaseHolderData() {
    }

    public BaseHolderData(@LayoutRes int layoutId) {
        mLayoutId = layoutId;
    }

    /**
     * 原始数据 用于对比数据
     * 没有使用clone 只是持有引用,可能被外部改变
     */
    public Object mOriData;
    /**
     * 点击事件 设置在itemView，也可以在{@link BaseHolder#onBindView(BaseHolderData, int)}中重写
     */
    public IItemClickListener<BaseHolderData> mItemClickListener;
    /**
     * 长按事件 设置在itemView，也可以在{@link BaseHolder#onBindView(BaseHolderData, int)}中重写
     */
    public IItemLongClickListener<BaseHolderData> mItemLongClickListener;

    /**
     * 在Grid布局中，用于确认item所占用的格数
     * <br><i>不能在Holder的onBind中设置<i/><br/>
     */
    public int mItemSpanSize = SF_DEF_ITEM_SPAN;

    /** 额外数据，用于携带其他数据 */
    public Object extraObj = null;

    /** 额外数据，用于携带其他类型的多个数据 */
    private Map<String, Object> extraMaps = null;

    BaseHolder mHolder;

    /**
     * 通知数据改变 如果是new出来的数据 调用无效
     * <p>可以用于在点击事件中刷新</p>
     */
    public void notifyDataChanged() {
        if (mHolder != null) {
            mHolder.notifyDataChanged();
        }
    }

    /**
     * 放置额外的数据
     *
     * @param key   键
     * @param value 值
     */
    public void putExtra(@NonNull String key, @NonNull Object value) {
        if (extraMaps == null) {
            extraMaps = new HashMap<>();
        }
        extraMaps.put(key, value);
    }

    /**
     * 获取额外的数据
     *
     * @param key 键
     * @return 可以为空，找到则返回值
     */
    @Nullable
    public Object getExtra(@NonNull String key) {
        if (extraMaps == null) {
            return null;
        }
        return extraMaps.get(key);
    }

    @Override
    public int getItemSpanSize(int spanSize) {
        return mItemSpanSize;
    }

    public int getLayoutId() {
        return mLayoutId;
    }
}
