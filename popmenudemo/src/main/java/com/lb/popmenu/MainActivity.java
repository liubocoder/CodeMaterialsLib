package com.lb.popmenu;

import android.app.Activity;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import cn.lb.overrecycler.BaseHolderData;
import cn.lb.overrecycler.BaseRecyclerAdapter;
import cn.lb.overrecycler.SimpleAdapterHelper;
import cn.lb.overrecycler.SimpleLoopAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private int counter;
    private LinearLayout ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ll = findViewById(R.id.container);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager llm = new LinearLayoutManager(this);

        final BaseRecyclerAdapter adapter = SimpleAdapterHelper.recyclerAdapter();

        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<BaseHolderData> listData = new ArrayList<>();
                int N = 5;
                for (int i = 0; i < N; i++) {
                    NestRecyclerData nestRecyclerData = new NestRecyclerData();
                    listData.add(nestRecyclerData);
                }
                adapter.updateAndNotifyData(listData);
                handler.postDelayed(this, 3000);

                counter++;
                ll.scrollBy(0, 0);
                testNewBtn(counter % 2 == 0);
            }
        },1000);
    }

    private void testNewBtn(boolean add) {
        ll.removeAllViews();
        if (add) {
            Button btn = new Button(this);
            btn.setText("添加按钮");
            btn.setOnClickListener(this);
            btn.setBackground(new MyD());
            ll.addView(btn);
        }
    }
    private View vTag;
    @Override
    public void onClick(View v) {
        final PopMenu popMenu = new PopMenu((MainActivity.this));
        popMenu.addItem(new PopMenuItem("xxx"));
        popMenu.addItem(new PopMenuItem("yyy"));
        popMenu.show(v);
        if (vTag == null) {
            vTag = v;
            v.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    Log.d("MainAct", "onScrollChanged: ");
                }
            });
        }
    }

    private class MyD extends Drawable {
        Paint paint;
        MyD() {
            paint = new Paint();
        }

        private Shader buildShader(int[] colors) {
            Rect rectF = getBounds();
            return new LinearGradient(rectF.left, rectF.top,
                    rectF.right, rectF.top, colors, null, Shader.TileMode.CLAMP);
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            paint.setShader(buildShader(new int[]{0xFFFF0000, 0xFF00FF00, 0xFFFF0000}));
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return 0;
        }
    }


}
