package cn.lb.overrecycler.impl;

/**
 * <p>
 * 当使用gridLayout布局的RecycleList，可以实现本接口，用于设置item占用格数
 * <br>例如：grid一行显示3个item，return 3 合并三格为一item，return 1 默认的一格一item</br>
 * <br>注：已在BaseHolderData中实现，仅需在其子类中覆盖<code>getItemSpanSize(int)方法<code/></br>
 * </p>
 * Created by LiuBo on 2016-08-29.
 */
public interface IItemSpanSize {
    int getItemSpanSize(int spanSize);
}
