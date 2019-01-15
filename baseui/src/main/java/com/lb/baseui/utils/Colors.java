package com.lb.baseui.utils;

/**
 * 常见的一些颜色  及颜色工具类
 * 属性命名：单词后的数字代表该颜色的透明度 例如：WHITE10 白色 透明度10%（Alpha值255 * 0.1）
 * Created by LiuBo on 2017-03-14.
 */

public class Colors {

    public static final int WHITE   = 0xFFFFFFFF;
    public static final int WHITE5  = 0x0DFFFFFF;
    public static final int WHITE10 = 0x1AFFFFFF;
    public static final int WHITE15 = 0x26FFFFFF;
    public static final int WHITE20 = 0x33FFFFFF;
    public static final int WHITE30 = 0x4DFFFFFF;
    public static final int WHITE40 = 0x66FFFFFF;
    public static final int WHITE50 = 0x7FFFFFFF;
    public static final int WHITE60 = 0x99FFFFFF;
    public static final int WHITE80 = 0xCCFFFFFF;
    public static final int WHITE95 = 0xF3FFFFFF;
    public static final int TRANSPARENT = 0x00000000;
    public static final int BLACK   = 0xFF000000;
    public static final int BLACK3  = 0x08000000;
    public static final int BLACK5  = 0x0D000000;
    public static final int BLACK10 = 0x1A000000;
    public static final int BLACK15 = 0x26000000;
    public static final int BLACK20 = 0x33000000;
    public static final int BLACK30 = 0x4D000000;
    public static final int BLACK40 = 0x66000000;
    public static final int BLACK50 = 0x80000000;
    public static final int BLACK60 = 0x99000000;
    public static final int BLACK70 = 0xB2000000;
    public static final int BLACK80 = 0xCC000000;
    public static final int BLACK85 = 0xD9000000;
    public static final int BLACK90 = 0xE6000000;
    public static final int BLACK95 = 0xF3000000;

    /**
     * 调整透明度
     * @param alpha 透明度，例如：0.1F 代表alpha通道值为0.1*255
     * @param color 需要调整透明度的颜色
     * @return recolor
     */
    public static int alphaColor(float alpha, int color) {
        return (Math.round(alpha * 255) << 24 )| (color & 0x00FFFFFF);
    }

}
