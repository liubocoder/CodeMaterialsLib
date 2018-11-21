package cn.lb.overrecycler;

import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;

/**
 * Holder默认创建工厂
 * Created by LiuBo on 2016-11-25.
 */

public class SimpleHolderFactory extends BaseHolderFactory {
    @SuppressWarnings("all")
    @Override
    public BaseHolder buildHolder(ViewGroup parent, int viewLayout) {
        try {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewLayout, parent, false);
            String className = (String) view.getTag();
            Class clazz = Class.forName(className);
            Constructor constructor = clazz.getConstructor(View.class);
            constructor.setAccessible(true);
            BaseHolder holder = (BaseHolder) constructor.newInstance(view);
            return holder;
        } catch (InflateException e) {
            e.printStackTrace();//将原有异常打印出来，便于分析
            if (viewLayout == 0) {
                throw new IllegalArgumentException("需要重写BaseHolderData.getLayoutId()");
            } else {
                throw new IllegalArgumentException("请检查布局中是否存在问题，例如自定义view路径异常等 inflate(id) "+viewLayout);
            }
        } catch (Exception e) {
            e.printStackTrace();//将原有异常打印出来，便于分析
            throw new IllegalArgumentException("需要在布局文件的根view中设置tag标签，内容为该布局绑定Holder类的全名，将用于反射出对象");
        }
    }
}
