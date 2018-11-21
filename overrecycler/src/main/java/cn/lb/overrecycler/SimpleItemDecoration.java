package cn.lb.overrecycler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 简单绘制线条 浮在item之上，不占用item的空间
 * Created by LiuBo on 2016-11-25.
 */

public class SimpleItemDecoration extends RecyclerView.ItemDecoration {
    private static final float DEF_DECORATION_WIDTH = 0.5F;
    private int mSpace;
    private int mLineColor = 0xFF808080;
    private int mVerticalPadding;
    protected Paint mPaint;

    /**
     * 设置线条颜色
     * @param color 默认0x80808080
     */
    public void setLineColor(int color) {
        this.mLineColor = color;
        mPaint.setColor(mLineColor);
    }

    /**
     * 默认线条宽度0.5DP
     */
    public SimpleItemDecoration(@NonNull Context context) {
        this(context, DEF_DECORATION_WIDTH);
    }

    /**
     * 线条宽度
     * @param space dp
     */
    public SimpleItemDecoration(@NonNull Context context, float space) {
        float ds = context.getResources().getDisplayMetrics().density;
        int coreSpace = (int) (ds * space + 0.5F);
        mSpace = coreSpace / 2;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(coreSpace);
        mPaint.setColor(mLineColor);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    /**
     * 水平线的左右padding
     * @param padding px
     */
    public void setVerticalPadding(int padding) {
        this.mVerticalPadding = padding;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        final int N = parent.getChildCount();
        for (int i = 0; i < N; i++) {
            View child = parent.getChildAt(i);
            BaseHolder holder = (BaseHolder) parent.getChildViewHolder(child);
            int bottomLine = child.getBottom();
            int rightLine = child.getRight();
            if (holder.drawDecoration(c, parent,i, mPaint)) {
                continue;
            }
            if (bottomLine < parent.getBottom()) {
                int left = child.getLeft();
                int right = child.getRight();
                if (child.getLeft() == parent.getLeft()) {
                    left = left + mVerticalPadding;
                } else if (child.getRight() == parent.getRight()) {
                    right = rightLine - mVerticalPadding;
                }
                c.drawLine(left, bottomLine + mSpace, right, bottomLine + mSpace, mPaint);
            }
            if (rightLine < parent.getRight()) {
                c.drawLine(rightLine + mSpace, child.getTop(), rightLine + mSpace, child.getBottom(), mPaint);
            }
        }
    }
}
