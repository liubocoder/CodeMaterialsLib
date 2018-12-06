package cn.lb.overrecycler;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;


/**
 * 模拟ViewPager循环滚动的Adapter
 * <pre>
 *  需要配合LoopLayoutManager、PageSnapHelper使用
 *  如果有嵌套的父控件是RecyclerView，需要将其Adapter设置到SimpleLoopAdapter中
 *  {@link SimpleLoopAdapter#setParentRecycler(BaseRecyclerAdapter)}
 * </pre>

 * Created by liubo on 2017-11-29.
 */

public class SimpleLoopAdapter extends BaseRecyclerAdapter {
    private static final int MAX_LEN = 0xFFFFFF;

    // 最小的循环的真实数据长度 2
    private static final int MIN_LOOP_REAL_SIZE = 2;
    // 默认循环间隔
    private static final int DEF_LOOP_TIME = 5000;
    //如果存在嵌套的父控件的Adapter
    private BaseRecyclerAdapter mParent;
    private Handler mHandler;
    private Runnable mRunner;
    private RecyclerView.OnScrollListener mScrollListener;
    private boolean mLooping;

    private int mLoopTime = DEF_LOOP_TIME;

    private OnPositionChange mPosChangeLs;

    public SimpleLoopAdapter(@NonNull BaseHolderFactory factory) {
        super(factory);
        mHandler = new Handler();
        mRunner = new Runnable() {
            @Override
            public void run() {
                if (mLooping) {
                    scrollToNext();
                    mHandler.postDelayed(this, mLoopTime);
                }
            }
        };
        mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mParent != null) {
                    mParent.setScrollState(newState);
                }

                if (!isLoopSize()) {
                    return;
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mPosChangeLs != null) {
                    notifyPositionListener();
                }
                int n = recyclerView.getChildCount();
                for (int i = 0; i < n; i++) {
                    BaseHolder holder =
                            (BaseHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                    if (holder instanceof OnScrollStateChange) {
                        ((OnScrollStateChange) holder).onScrollStateChange(newState);
                    }
                }
            }
        };
    }

    public void setParentRecycler(BaseRecyclerAdapter parent) {
        mParent = parent;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.removeOnScrollListener(mScrollListener);
        recyclerView.addOnScrollListener(mScrollListener);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        stopLoop();
        recyclerView.removeOnScrollListener(mScrollListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        RecyclerView recyclerView = getAttachedRecycler();
        if (holder instanceof OnScrollStateChange && recyclerView != null) {
            ((OnScrollStateChange) holder).onScrollStateChange(recyclerView.getScrollState());
        }
    }

    private void notifyPositionListener() {
        if (mPosChangeLs == null) {
            return;
        }
        RecyclerView rv = getAttachedRecycler();
        if (rv == null) {
            return;
        }
        RecyclerView.LayoutManager lm = rv.getLayoutManager();
        if (!(lm instanceof LoopLayoutManager)) {
            return;
        }
        if (mDatas.size() == 0) {
            return;
        }

        LoopLayoutManager llm = (LoopLayoutManager) lm;
        mPosChangeLs.onPositionChange(llm.findFirstVisibleItemPosition() % mDatas.size());
    }

    private boolean isLoopSize() {
        return mDatas.size() >= MIN_LOOP_REAL_SIZE;
    }

    public void setOnPositionChangeListener(OnPositionChange changeListener) {
        mPosChangeLs = changeListener;
    }

    /**
     * 设置滚动间隔时长 单位ms
     */
    public void setLoopTime(int time) {
        if (time > 0) {
            mLoopTime = time;
        }
    }

    public boolean isLooping() {
        return mLooping;
    }

    public void startLoop() {
        if (mLooping) {
            if (!isLoopSize()) {
                stopLoop();
            }
            return;
        }

        mHandler.removeCallbacks(mRunner);
        if (isLoopSize()) {
            mLooping = true;
            mHandler.postDelayed(mRunner, mLoopTime);
        }
    }

    public void stopLoop() {
        mLooping = false;
        mHandler.removeCallbacks(mRunner);
    }

    @Nullable
    @Override
    public BaseHolderData getItemData(int position) {
        position = position % mDatas.size();
        if (position >= 0 && position < mDatas.size()) {
            return mDatas.get(position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if (isLoopSize()) {
            return MAX_LEN;
        }
        return mDatas.size();
    }

    public void scrollToNext() {
        RecyclerView recyclerView = getAttachedRecycler();
        if (recyclerView == null) {
            return;
        }
        // 如果当前不是idle状态  不滚动
        if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
            return;
        }

        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        if (!(lm instanceof LinearLayoutManager && isLoopSize())) {
            return;
        }
        LinearLayoutManager llm = (LinearLayoutManager) lm;
        int position = llm.findFirstVisibleItemPosition() + 1;
        if (position >= getItemCount()) {
            return;
        }

        recyclerView.smoothScrollToPosition(position);
    }

    private static class SmoothScroller extends LinearSmoothScroller {
        static final int DEF_DECELERATION_TIME = 1000;
        SmoothScroller(Context context) {
            super(context);
        }
        @Override
        protected int calculateTimeForDeceleration(int dx) {
            return DEF_DECELERATION_TIME;
        }
    }

    public static class LoopLayoutManager extends LinearLayoutManager {
        public LoopLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        SmoothScroller linearSmoothScroller;
        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                           int position) {
            if (linearSmoothScroller == null) {
                linearSmoothScroller = new SmoothScroller(recyclerView.getContext()) ;
            }
            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }

        @Override
        public void onAttachedToWindow(RecyclerView view) {
            super.onAttachedToWindow(view);
            SimpleLoopAdapter adapter = (SimpleLoopAdapter) view.getAdapter();
            if (adapter == null){
                return;
            }
            int firstItem = findFirstVisibleItemPosition();
            int realItemCount = adapter.mDatas.size();
            if (firstItem < realItemCount && realItemCount > 0) {
                view.scrollToPosition(MAX_LEN / 2 - (MAX_LEN / 2) % adapter.mDatas.size());
            } else if (firstItem != RecyclerView.NO_POSITION) {
                view.scrollToPosition(firstItem);
            }
        }
    }

    public interface OnScrollStateChange {
        /**
         * @see RecyclerView#onScrollStateChanged(int)
         */
        void onScrollStateChange(int state);
    }

    /**
     * 用于监听滚动位置的回调，注意：初始化时没有回调
     *
     * @see #setOnPositionChangeListener(OnPositionChange)
     */
    public interface OnPositionChange {
        /**
         * @param newPosition 当前显示的item
         */
        void onPositionChange(int newPosition);
    }
}
