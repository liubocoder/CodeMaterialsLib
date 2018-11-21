package cn.lb.overrecycler.impl;

/**
 * 用于固定布局的item回调偏移量
 *
 * Created by liubo on 2017-11-24.
 */

public interface IFixItemOffsetInterface {
    /**
     * @param offset [0-1] 0完全不可见
     */
    void scrollOffset(float offset);
}
