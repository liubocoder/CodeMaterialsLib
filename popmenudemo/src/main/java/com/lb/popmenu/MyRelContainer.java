package com.lb.popmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * @author LiuBo
 * @date 2018-12-03
 */
public class MyRelContainer extends RelativeLayout {
    public MyRelContainer(Context context) {
        super(context);
    }

    public MyRelContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRelContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Log.d("MyRelContainer", String.format("onLayoutChange: l=%d, top=%d, r=%d,b=%d",left,top,right,oldBottom));
            }
        });
    }
}
