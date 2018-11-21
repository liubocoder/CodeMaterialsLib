package cn.lb.overrecycler;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author LiuBo
 * @date 2018-07-06
 */
public class SimpleSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int mSpace;
    public SimpleSpaceItemDecoration(int space) {
        mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(mSpace, mSpace, mSpace, mSpace);
    }
}
