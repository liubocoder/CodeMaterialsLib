package com.lb.popmenu;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.lb.overrecycler.BaseHolder;
import cn.lb.overrecycler.BaseHolderData;
import com.lb.baseui.ThemeManager;

public class PopMenuData extends BaseHolderData {
    
    public PopMenuItem mItem;
    public boolean isFirst;
    public boolean isLast;

    @Override
    public int getLayoutId() {
        return R.layout.bsvw_layout_pop_menu_item;
    }
    
    public static class PopMenuHolder extends BaseHolder<PopMenuData> implements View.OnClickListener {

        ImageView mIvIcon;
        TextView mTvTitle;
        
        public PopMenuHolder(View itemView) {
            super(itemView);
            mIvIcon = findViewById(R.id.bsvw_tv_pop_menu_item_icon);
            mTvTitle = findViewById(R.id.bsvw_tv_pop_menu_item_text);
            this.itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            PopMenuData data = getBindData();
            if (data != null && data.mItemClickListener != null) {
                data.mItemClickListener.onItemClick(v, data);
            }
        }

        @Override
        public void onBindView(PopMenuData data, int position) {
            super.onBindView(data, position);
            PopMenuItem menu = data.mItem;
            if(null != menu && 0 != menu.mIconRes){
                mIvIcon.setVisibility(View.VISIBLE);
                mIvIcon.setImageResource(menu.mIconRes);
                mIvIcon.setColorFilter(ThemeManager.getColor(R.color.comm_white));
            } else {
                mIvIcon.setVisibility(View.GONE);
            }

            if(null != menu && !TextUtils.isEmpty(menu.mItem)){
                mTvTitle.setVisibility(View.VISIBLE);
                mTvTitle.setText(menu.mItem);
            } else {
                mTvTitle.setVisibility(View.GONE);
            }

            if (data.isFirst && data.isLast) { // 只有一项
                int bgRes = menu.isPopUp ? R.drawable.bsvw_pop_menu_one_item_bg_up : R.drawable.bsvw_pop_menu_one_item_bg;
                itemView.setBackgroundResource(bgRes);
            } else if (data.isFirst) { // 第一项
                int bgRes = menu.isPopUp ? R.drawable.bsvw_pop_menu_first_item_bg_up : R.drawable.bsvw_pop_menu_first_item_bg;
                itemView.setBackgroundResource(bgRes);
            } else if (data.isLast) { // 最后一项
                int bgRes = menu.isPopUp ? R.drawable.bsvw_pop_menu_last_item_bg_up : R.drawable.bsvw_pop_menu_last_item_bg;
                itemView.setBackgroundResource(bgRes);
            } else {
                itemView.setBackgroundColor(ThemeManager.getColor(R.color.comm_black_85));
            }
        }
    }
}
