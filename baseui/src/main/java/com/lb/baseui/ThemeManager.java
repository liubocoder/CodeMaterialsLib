package com.lb.baseui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.*;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Method;

/**
 * 主题管理  资源获取 color、drawable
 * Created by LiuBo on 2017-03-14.
 */

public final class ThemeManager {

    @Nullable
    public static ColorStateList getColorStateList(@ColorRes int colorRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColorStateList(colorRes, null);
        } else {
            return getResources().getColorStateList(colorRes);
        }
    }

    @ColorInt
    public static int getColor(@ColorRes int colorRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColor(colorRes, null);
        } else {
            return getResources().getColor(colorRes);
        }
    }

    @NonNull
    public static Drawable getDrawable(@DrawableRes int drawableRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getResources().getDrawable(drawableRes, null);
        } else {
            return getResources().getDrawable(drawableRes);
        }
        //return getDrawable(drawableRes, null);
    }

    @Dimension
    public static int getDimens(@DimenRes int dimens) {
        return getResources().getDimensionPixelSize(dimens);
    }

    @NonNull
    public static String getString(@StringRes int stringRes) {
        return getResources().getString(stringRes);
    }

    @NonNull
    public static String getString(@StringRes int stringRes, Object... formatArgs) {
        return getResources().getString(stringRes, formatArgs);
    }

    @NonNull
    public static String[] getStringArray(@ArrayRes int stringArrRes) {
        return getResources().getStringArray(stringArrRes);
    }

    @NonNull
    public static String getQuantityString(@PluralsRes int id, int quantity){
        return getResources().getQuantityString(id, quantity);
    }

    @NonNull
    public static String getQuantityString(@PluralsRes int id, int quantity, Object... formatArgs){
        return getResources().getQuantityString(id, quantity, formatArgs);
    }

    @NonNull
    public static int[] getIntArray(@ArrayRes int intArrRes) {
        return getResources().getIntArray(intArrRes);
    }

    public static boolean getBoolean(@BoolRes int boolRes) {
        return getResources().getBoolean(boolRes);
    }

    /**
     * 统一的资源类
     */
    @NonNull
    public static Resources getResources() {
        return BaseApplication.sApp.getResources();
    }

    public static void setTextAppearance(TextView view, int textAppearance) {
        Class<?> resourcesClass = view.getClass();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Method getColorMethod = resourcesClass.getMethod("setTextAppearance", Context.class, int.class);
                getColorMethod.setAccessible(true);
                getColorMethod.invoke(view, view.getContext(), textAppearance);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            try {
                Method getColorMethod = resourcesClass.getMethod("setTextAppearance", int.class);
                getColorMethod.setAccessible(true);
                getColorMethod.invoke(view, textAppearance);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static Drawable getDrawable(int drawableId, Resources.Theme theme) {
        Resources resources = getResources();
        Class<?> resourcesClass = resources.getClass();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Method getDrawableMethod = resourcesClass.getMethod("getDrawable", int.class, Resources.Theme.class);
                getDrawableMethod.setAccessible(true);
                return (Drawable) getDrawableMethod.invoke(resources, drawableId, theme);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            try {
                Method getDrawableMethod = resourcesClass.getMethod("getDrawable", int.class);
                getDrawableMethod.setAccessible(true);
                return (Drawable) getDrawableMethod.invoke(resources, drawableId);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return resources.getDrawable(drawableId);
    }

    public static void setBackground(View view, int drawableId) {
        setBackground(view, getDrawable(drawableId));
    }

    public static void setBackground(View view, Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground("setBackground", view, background);
        } else {
            setBackground("setBackgroundDrawable", view, background);
        }
    }

    private static void setBackground(String method, View view, Drawable background) {
        try {
            Method viewMethod = view.getClass().getMethod(method, Drawable.class);
            viewMethod.setAccessible(true);
            viewMethod.invoke(view, background);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
