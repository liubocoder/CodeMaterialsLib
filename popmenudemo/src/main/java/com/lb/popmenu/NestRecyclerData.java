package com.lb.popmenu;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import cn.lb.overrecycler.BaseHolder;
import cn.lb.overrecycler.BaseHolderData;
import cn.lb.overrecycler.SimpleAdapterHelper;
import cn.lb.overrecycler.SimpleLoopAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LiuBo
 * @date 2018-12-03
 */
public class NestRecyclerData extends BaseHolderData {

    @Override
    public int getLayoutId() {
        return R.layout.item_recycler;
    }

    public static class NestRecyclerHolder extends BaseHolder<NestRecyclerData> {
        private RecyclerView mRecyclerView;
        private SimpleLoopAdapter mAdapter;
        public NestRecyclerHolder(View itemView) {
            super(itemView);

            RecyclerView recyclerView = findViewById(R.id.item_recycler);
            LinearLayoutManager llm = new LinearLayoutManager(itemView.getContext());
            llm.setOrientation(LinearLayoutManager.HORIZONTAL);

            mAdapter = SimpleAdapterHelper.loopAdapter();

            recyclerView.setLayoutManager(llm);
            recyclerView.setAdapter(mAdapter);

            PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
            pagerSnapHelper.attachToRecyclerView(recyclerView);
        }

        @Override
        public void onBindView(NestRecyclerData data, int position) {
            super.onBindView(data, position);

            int N = 10;
            List<BaseHolderData> dataList = new ArrayList<>();
            for (int i = 0; i < N; i++) {
                PopMenuData menuData = new PopMenuData();
                menuData.mItem = new PopMenuItem("item "+i);
                dataList.add(menuData);
            }

            mAdapter.updateAndNotifyData(dataList);
            mAdapter.startLoop();
        }
    }
}
