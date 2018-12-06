package cn.lb.overrecycler;

/**
 * 用于创建Adapter实例
 * Created by LiuBo on 2016-11-25.
 */

public final class SimpleAdapterHelper {
    private SimpleAdapterHelper() {}

    /**
     * 普通的List
     */
    public static BaseRecyclerAdapter recyclerAdapter() {
        return new BaseRecyclerAdapter(new SimpleHolderFactory());
    }

    public static SimpleLoopAdapter loopAdapter() {
        return new SimpleLoopAdapter(new SimpleHolderFactory());
    }
}
