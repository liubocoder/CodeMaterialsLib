package com.lb.popmenu;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import cn.lb.overrecycler.BaseHolderData;
import cn.lb.overrecycler.BaseRecyclerAdapter;
import cn.lb.overrecycler.SimpleAdapterHelper;
import cn.lb.overrecycler.SimpleItemDecoration;
import cn.lb.overrecycler.impl.IItemClickListener;
import com.lb.baseui.ThemeManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Function: 下拉弹出选择框<br>
 *     <p>适用场景：位置空间受限，多项类似功能整合到同一按钮下。点击按钮出现子集功能选项。主要用于导航栏，更多按钮</p>
 *     使用示例：<pre>
 *     {@code
 *     PopMenu menu = new PopMenu(MainActivity.this);
 *     View v = findViewById(R.id.btn_test);
 *     menu.addItem(new PopMenuItem(R.drawable.comm_cross_delete, "xxx"));
 *     menu.addItem(new PopMenuItem(R.drawable.comm_cross_delete, "yyy"));
 *     menu.addItem(new PopMenuItem(R.drawable.comm_cross_delete, "zzz"));
 *     menu.setOnItemClickListener(new PopMenuItemClickListener() {
 *          #@Override
 *          public void onItemClick(String item) {
 *              Log.d("sy", "item:" + item);
 *              // do more things
 *          }
 *      });
 *      menu.show(v);
 *      }</pre>
 */
public class PopMenu extends PopupWindow implements View.OnFocusChangeListener, IItemClickListener {

    /** 向下弹出 */
    public static final int POP_DOWN = 1;
    /** 向上弹出 */
    public static final int POP_UP = 2;

    private Context mContext;

    private LinearLayout mLayoutRoot;
    private ImageView mIvCorner;
    private RecyclerView mLvItem;

    private BaseRecyclerAdapter mRecyclerAdapter;
    private List<PopMenuItem> mItemData = new ArrayList<>();
    private List<PopMenuData> mShowData = new ArrayList<>();
    private PopMenuItemClickListener mOuterClickListener;

    /** 相对锚点视图的x轴偏移（单位px），负数，达到显示在视图左下方的效果 */
    private int mAnchorXOffset;
    private int mAnchorYOffset;


    private int mItemH;
    private int mPopMenuWidth;
    private int mPopType;
    private int mPopMenuMaxHeight;

    /**
     * 创建一个新的PopMenu实例对象
     * @param context activity上下文
     */
    public PopMenu(Activity context) {
        this(context, null);
    }

    /**
     * 创建一个新的PopMenu实例对象
     * @param context activity上下文
     * @param listener 条目点击回调接口
     */
    public PopMenu(Activity context, @NonNull PopMenuItemClickListener listener) {
        super(context);
        mContext = context;
        mPopMenuWidth = ThemeManager.getDimens(R.dimen.bsvw_pop_menu_width);
        mItemH = ThemeManager.getDimens(R.dimen.bsvw_pop_menu_item_height);
        mOuterClickListener = listener;
        mRecyclerAdapter = SimpleAdapterHelper.recyclerAdapter();
        //最大为屏幕高度的一半
        mPopMenuMaxHeight = 1024;
        // 窗口布局，默认向下弹出
        mPopType = POP_DOWN;
        setPopLayout(mPopType);
        setContentView(mLayoutRoot);
        setWidth(mPopMenuWidth);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置背景透明
        setBackgroundDrawable(new ColorDrawable(0));
        setFocusable(true);
        getContentView().setOnFocusChangeListener(this);
    }

    /**
     * 设置弹出菜单布局
     * @param popType 取值：{@link #POP_DOWN} or {@link #POP_UP}
     */
    private void setPopLayout(int popType) {
        mLayoutRoot = mLayoutRoot == null ? new LinearLayout(mContext) : mLayoutRoot;
        mLayoutRoot.setOrientation(LinearLayout.VERTICAL);
        mLayoutRoot.removeAllViews();

        mIvCorner = mIvCorner == null ? new ImageView(mContext) : mIvCorner;
        mIvCorner.setImageResource(R.drawable.bsvw_pop_menu_corner);
        LinearLayout.LayoutParams ivParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mLvItem = mLvItem == null ? new RecyclerView(mContext) : mLvItem;
        mLvItem.setLayoutManager(new LinearLayoutManager(mContext));
        mLvItem.setBackgroundColor(ThemeManager.getColor(R.color.comm_transparent));
        SimpleItemDecoration itemDecoration = new SimpleItemDecoration(mContext);
        itemDecoration.setLineColor(ThemeManager.getColor(R.color.comm_white_30));
        mLvItem.addItemDecoration(itemDecoration);
        mLvItem.setAdapter(mRecyclerAdapter);
        LinearLayout.LayoutParams lvParam = new LinearLayout.LayoutParams(mPopMenuWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (popType == POP_DOWN) {
            ivParam.gravity = Gravity.RIGHT;
            // 设置显示和隐藏动画
            setAnimationStyle(R.style.bsvw_pop_menu_anim);
            mLayoutRoot.addView(mIvCorner, ivParam);
            mLayoutRoot.addView(mLvItem, lvParam);
        } else if (popType == POP_UP) {
            setAnimationStyle(R.style.bsvw_pop_menu_anim_up);
            mLayoutRoot.addView(mLvItem, lvParam);
        }
    }

    /**
     * 设置弹出类型，可选向上弹出或者向下弹出
     * @param popType 取值：{@link #POP_DOWN} or {@link #POP_UP}
     */
    public void setPopType(int popType) {
        if (mPopType == popType) {
            return;
        }
        mPopType = popType;
        setPopLayout(mPopType);
        if (mItemData.size() > 0) {
            for (PopMenuItem item : mItemData) {
                item.isPopUp = mPopType == POP_UP;
            }
        }
    }

    /**
     * 设置条目点击监听器
     * @param itemClickListener
     */
    public void setOnItemClickListener(PopMenuItemClickListener itemClickListener) {
        mOuterClickListener = itemClickListener;
    }

    /**
     * 增加一条菜单项
     * @param item 菜单项
     */
    public void addItem(PopMenuItem item) {
        if (!mItemData.contains(item)) {
            mItemData.add(item);
        }
        updateLocal();
    }

    /**
     * 增加多个菜单项
     * @param items 菜单项
     */
    public void addItems(PopMenuItem[] items) {
        for (PopMenuItem item : items) {
            if (!mItemData.contains(item)) {
                mItemData.add(item);
            }
        }
        updateLocal();
    }

    /**
     * 更新显示
     */
    public void updateLocal() {
        convertItemData();
        if (mRecyclerAdapter != null) {
            mRecyclerAdapter.updateAndNotifyData(mShowData);
            //调整布局
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mLvItem.getLayoutParams();
            if (mShowData.size() * mItemH > mPopMenuMaxHeight) {
                layoutParams.height = mPopMenuMaxHeight;
            } else {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            mLvItem.setLayoutParams(layoutParams);
            mLvItem.scrollToPosition(0);
        }
    }

    /**
     * 焦点变化处理，失去焦点时隐藏对话框
     * @param v
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            dismiss();
        }
    }

    private void convertItemData() {
        mShowData.clear();
        for (int i = 0; i < mItemData.size(); i++) {
            PopMenuData data = new PopMenuData();
            data.mItem = mItemData.get(i);
            data.isFirst = i == 0;
            data.isLast = i == (mItemData.size() - 1);
            data.mItemClickListener = this;
            mShowData.add(data);
        }
    }

    /**
     * 显示弹出菜单
     * @param anchor 锚点View，即位于哪个视图的下方
     */
    public void show(View anchor) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            int[] locaRoot = new int[2];
            int[] loca = new int[2];

            View root = anchor.getRootView();
            root.getLocationInWindow(locaRoot);
            anchor.getLocationInWindow(loca);

            int x = (loca[0]-locaRoot[0]) + anchor.getWidth() / 2 - mPopMenuWidth;
            int y = -(root.getHeight() - (loca[1] - locaRoot[1]) - anchor.getHeight());
            showAsDropDown(root, x, y);
        } else {
            // x方向的偏移 居中显示
            int xOffset = getXOffset(anchor);
            showAsDropDown(anchor, xOffset, 0);
        }
    }

    /**
     * 显示弹出菜单，对齐在锚点View的右侧
     * @param anchor 锚点View
     */
    public void showAlignRight(View anchor){
        mAnchorXOffset = anchor.getWidth() - mPopMenuWidth;
        showAsDropDown(anchor, mAnchorXOffset, 0);
    }

    /**
     * 显示弹出菜单，菜单在锚点view上方
     * @param anchor 锚点View，即位于哪个视图的上方
     */
    public void showAbove(View anchor) {
        if (mPopType == POP_DOWN) {
            return;
        }
        int[] anchorLoc = new int[2];
        int listH = mItemH * mItemData.size();
        anchor.getLocationOnScreen(anchorLoc);
        int x = anchorLoc[0];
        int y = anchorLoc[1] - listH;
        showAtLocation(anchor, Gravity.NO_GRAVITY, x, y);
    }

    private int getXOffset(View anchor) {
        int anchorW = anchor.getWidth();
        if (mPopMenuWidth > anchorW) {
            return -(mPopMenuWidth - anchorW / 2);
        } else {// 锚点View宽度大于Menu
            return -anchorW / 2;
        }
    }

    @Override
    public void onItemClick(View v, BaseHolderData data) {
        dismiss();
        if (mOuterClickListener != null && data instanceof PopMenuData) {
            PopMenuData menuData = (PopMenuData) data;
            if (menuData.mItem != null) {
                mOuterClickListener.onItemClick(menuData.mItem.mItem);
            }
        }
    }
}
