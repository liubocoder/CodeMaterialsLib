package com.lb.baseui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.lb.baseui.utils.Colors;

/**
 * @author LiuBo
 * @date 2019-01-02
 */
public class PadValuePickerView extends View {
    private static final int DEF_COLOR_NUM = 12;

    private static final int MAX_COLD = 100;
    private static final float SCALE_COLOR_HEIGHT = 1.3F;

    private static final int CIRCLE_RAD = 7;

    private Rect mColorRect;
    private Rect mHuaRect;
    private Rect mColdShapeRect;

    private Paint satValPaint;
    private Paint satValTrackerPaint;
    private Paint huaColPaint;

    private Paint mColdPaint;

    private Shader valShader;
    private Shader satShader;
    private Shader coldShader;

    private PadValuePickerView.BitmapCache satValBackgroundCache;

    private float hue = 180F;
    private float sat = 0.5F;
    private final float val = 1F;

    private float cold = 50;

    private int[] mColdShaderColors = {0xFFFFF48E, Colors.WHITE, Colors.WHITE, 0xFF76D6FF};
    private int[] mDefRgb;

    private ItemAnim[] mAnims;

    /**
     * 是否是颜色选取style
     */
    private boolean mColorType = true;
    /**
     * 当前是否在选择颜色区域
     */
    private boolean mPickColor;

    private float mLastX;
    private float mLastY;

    private PadValuePickerView.OnColorChangedListener mColorListener;
    private PadValuePickerView.OnColdChangedListener mColdListener;

    private int mCurIdx = -1;

    public PadValuePickerView(Context context) {
        this(context, null);
    }

    public PadValuePickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mColorRect = new Rect();
        mHuaRect = new Rect();
        mColdShapeRect = new Rect();

        satValPaint = new Paint();
        satValTrackerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        satValTrackerPaint.setAntiAlias(true);
        satValTrackerPaint.setStyle(Paint.Style.STROKE);
        satValTrackerPaint.setColor(Color.WHITE);
        satValTrackerPaint.setStrokeWidth(dp2px(CIRCLE_RAD + 0.8F));
        huaColPaint = new Paint();
        huaColPaint.setStyle(Paint.Style.FILL);

        mDefRgb = new int[DEF_COLOR_NUM];
        mAnims = new ItemAnim[DEF_COLOR_NUM];

        int colorStep = 360 / DEF_COLOR_NUM;
        float[] hsv = new float[]{0, 1F, 1F};
        for (int i = 0; i < DEF_COLOR_NUM; i++) {
            hsv[0] = colorStep * i;
            mDefRgb[i] = Color.HSVToColor(hsv);
            mAnims[i] = new ItemAnim(i);
        }

        mColdPaint = new Paint();

        setBackgroundColor(Color.WHITE);
    }

    /** 将DP转换为PX */
    public int dp2px(float dpValue) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) (displayMetrics.density * dpValue + 0.5F);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        valShader = null;
        satShader = null;
        coldShader = null;

        satValBackgroundCache = null;

        mColdShapeRect.set(0, 0, w, h);
        int size = w / DEF_COLOR_NUM;
        mHuaRect.set(0, h - size, w, h);
        mColorRect.set(0, 0, w, (int) (h - mHuaRect.height() * SCALE_COLOR_HEIGHT));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (mColdShapeRect.width() == 0 || mColdShapeRect.height() == 0) {
            return;
        }

        if (mColorType) {
            drawSatValPanel(canvas);
            drawHuePanel(canvas);
        } else {
            drawColdPanel(canvas);
        }
    }

    private void drawColdPanel(Canvas canvas) {
        final Rect rect = mColdShapeRect;
        if (coldShader == null) {
            coldShader = new LinearGradient(rect.left, rect.top, rect.right, rect.top, mColdShaderColors,
                    null, Shader.TileMode.CLAMP);
            mColdPaint.setShader(coldShader);
        }
        canvas.drawRect(rect, mColdPaint);

        Point p = coldToPoint(cold);
        satValTrackerPaint.setColor(Colors.BLACK);
        canvas.drawCircle(p.x, p.y, dp2px(CIRCLE_RAD), satValTrackerPaint);
    }

    private Point coldToPoint(float cold) {

        final Rect rect = mColdShapeRect;
        Point p = new Point();

        p.x = (int) (cold / MAX_COLD * rect.width() + rect.left) ;
        p.y = mLastY == 0 ? rect.centerY() : (int) mLastY;

        return p;
    }

    private void drawSatValPanel(Canvas canvas) {
        final Rect rect = mColorRect;

        if (valShader == null) {
            valShader =
                    new LinearGradient(rect.left, rect.top, rect.left, rect.bottom, 0xFFFFFFFF,
                            0x00FFFFFF, Shader.TileMode.CLAMP);
        }

        if (satValBackgroundCache == null) {
            satValBackgroundCache = new PadValuePickerView.BitmapCache();

            if (satValBackgroundCache.bitmap == null) {
                satValBackgroundCache.bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
            }

            if (satValBackgroundCache.canvas == null) {
                satValBackgroundCache.canvas = new Canvas(satValBackgroundCache.bitmap);
            }

            float[] hsv = new float[]{0, 1F, 1F};
            int[] rgb = new int[360];
            for (int i = 0; i < rgb.length; i++) {
                hsv[0] = i;
                rgb[i] = Color.HSVToColor(hsv);
            }

            satShader = new LinearGradient(rect.left, rect.top, rect.right, rect.top, rgb, null, Shader.TileMode.CLAMP);

            ComposeShader mShader = new ComposeShader(valShader, satShader, PorterDuff.Mode.MULTIPLY);
            satValPaint.setShader(mShader);
            satValBackgroundCache.canvas.drawRect(0, 0, satValBackgroundCache.bitmap.getWidth(),
                    satValBackgroundCache.bitmap.getHeight(), satValPaint);
        }

        canvas.drawBitmap(satValBackgroundCache.bitmap, null, rect, null);
        Point p = satValToPoint(hue, sat);
        satValTrackerPaint.setColor(Colors.WHITE);
        canvas.drawCircle(p.x, p.y, dp2px(CIRCLE_RAD), satValTrackerPaint);
    }

    private Point satValToPoint(float hue, float sat) {

        final Rect rect = mColorRect;
        final float height = rect.height();
        final float width = rect.width();

        Point p = new Point();

        p.x = (int) (hue / 360 * width + rect.left);
        p.y = (int) ((1f - sat) * height + rect.top);

        return p;
    }

    private void drawHuePanel(Canvas canvas) {
        final Rect rect = mHuaRect;
        int rectHeight = mHuaRect.height();
        for (int i = 0; i < mCurIdx; i++) {
            huaColPaint.setColor(mDefRgb[i]);
            if (i == mDefRgb.length - 1) {
                drawRect(canvas, i,rectHeight * i, rect.top, rect.right, rect.bottom);
                //canvas.drawRect(rectHeight * i, rect.top, rect.right, rect.bottom, huaColPaint);
            } else {
                //canvas.drawRect(rectHeight * i, rect.top, rectHeight * (i + 1), rect.bottom, huaColPaint);
                drawRect(canvas, i, rectHeight * i, rect.top, rectHeight * (i + 1), rect.bottom);
            }
        }
        for (int i = mDefRgb.length - 1; i > mCurIdx; i--) {
            if (i == mDefRgb.length - 1) {
                drawRect(canvas, i,rectHeight * i, rect.top, rect.right, rect.bottom);
                //canvas.drawRect(rectHeight * i, rect.top, rect.right, rect.bottom, huaColPaint);
            } else {
                //canvas.drawRect(rectHeight * i, rect.top, rectHeight * (i + 1), rect.bottom, huaColPaint);
                drawRect(canvas, i, rectHeight * i, rect.top, rectHeight * (i + 1), rect.bottom);
            }
        }
        if (mCurIdx != -1) {
            int i = mCurIdx;
            if (i == mDefRgb.length - 1) {
                drawRect(canvas, i,rectHeight * i, rect.top, rect.right, rect.bottom);
                //canvas.drawRect(rectHeight * i, rect.top, rect.right, rect.bottom, huaColPaint);
            } else {
                //canvas.drawRect(rectHeight * i, rect.top, rectHeight * (i + 1), rect.bottom, huaColPaint);
                drawRect(canvas, i, rectHeight * i, rect.top, rectHeight * (i + 1), rect.bottom);
            }
        }

        huaColPaint.setColor(Colors.BLACK);
        canvas.drawRect(rect.left, mColorRect.bottom, rect.right, rect.top, huaColPaint);
    }

    private void drawRect(Canvas canvas, int idx, float left, float top, float right, float bottom) {
        ItemAnim anim = mAnims[idx];
        huaColPaint.setColor(mDefRgb[idx]);
        float dSize = (right - left) * (anim.value - 1F);
        canvas.drawRect(left - dSize / 2, top - dSize, right + dSize / 2, bottom, huaColPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        float x = event.getX();
        float y = event.getY();
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mPickColor = mColorRect.contains((int)x, (int)y);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_MOVE:
                mLastX = x;
                mLastY = y;

                if (mColorType) {
                    if (mPickColor) {
                        float[] result = pointToSatVal(x, y);
                        hue = result[0];
                        sat = result[1];
                    } else {
                        int idx = (int) (x / mHuaRect.height());
                        if (idx >=0 && idx < mDefRgb.length) {
                            setColor(mDefRgb[idx]);
                            if (action != MotionEvent.ACTION_MOVE) {
                                mAnims[idx].doAnimRestore();
                            } else {
                                mAnims[idx].doAnimBig();
                            }
                            mCurIdx = idx;
                        }
                    }
                    if (mColorListener != null) {
                        mColorListener.onColorChanged(getColor(), action != MotionEvent.ACTION_MOVE);
                    }
                } else {
                    cold = pointToCold(x);
                    if (mColdListener != null) {
                        mColdListener.onColdChanged(getCold(), action != MotionEvent.ACTION_MOVE);
                    }
                }
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }

    private float[] pointToSatVal(float x, float y) {

        final Rect rect = mColorRect;
        float[] result = new float[2];

        float width = rect.width();
        float height = rect.height();

        if (x < rect.left) {
            x = 0f;
        } else if (x > rect.right) {
            x = width;
        } else {
            x = x - rect.left;
        }

        if (y < rect.top) {
            y = 0f;
        } else if (y > rect.bottom) {
            y = height;
        } else {
            y = y - rect.top;
        }

        result[0] = 360 * x / width;
        result[1] = 1F - y / height;

        return result;
    }

    private float pointToCold(float x) {
        final Rect rect = mColdShapeRect;
        if (x < rect.left) {
            x = 0f;
        } else if (x > rect.right) {
            x = rect.width();
        } else {
            x = x - rect.left;
        }
        return x / rect.width() * MAX_COLD;
    }

    /**
     * 设置cold选取style
     * @param coldStyle true cold选取 false 颜色选取(默认)
     */
    public void setColdStyle(boolean coldStyle) {
        mColorType = !coldStyle;
        invalidate();
    }

    public int getCold() {
        return MAX_COLD - (int) cold;
    }

    public void setCold(int cold) {
        this.cold = MAX_COLD - cold;
        invalidate();
    }

    public int getColor() {
        return Color.HSVToColor(0xFF, new float[] { hue, sat, val });
    }

    public void setColor(int color) {
        int red = Color.red(color);
        int blue = Color.blue(color);
        int green = Color.green(color);

        float[] hsv = new float[3];

        Color.RGBToHSV(red, green, blue, hsv);

        hue = hsv[0];
        sat = hsv[1];

        invalidate();
    }

    private class BitmapCache {
        Canvas canvas;
        Bitmap bitmap;
    }

    public void setOnColorChangedListener(PadValuePickerView.OnColorChangedListener listener) {
        mColorListener = listener;
    }

    public void setOnColdChangedListener(PadValuePickerView.OnColdChangedListener listener) {
        mColdListener = listener;
    }

    public interface OnColorChangedListener {
        /**
         * 颜色值监听器
         *
         * @param newColor color
         * @param isFinished 手势结束
         */
        void onColorChanged(int newColor, boolean isFinished);
    }

    public interface OnColdChangedListener {
        /**
         * 色温值监听器
         *
         * @param newCold cold
         * @param isFinished true 手势结束
         */
        void onColdChanged(int newCold, boolean isFinished);
    }

    private class ItemAnim {
        static final float MIN_NORMAL = 1F;
        static final float MAX_BIG = 20.2F;
        static final int ANIM_IDL = 0;
        static final int ANIM_TO_BIG = 1;
        static final int ANIM_TO_SM = 2;
        float value = MIN_NORMAL;
        int idx;
        ValueAnimator anim;
        int stat = ANIM_IDL;
        public ItemAnim(int idx) {
            this.idx = idx;
            anim = new ValueAnimator();
            anim.setDuration(500);
            anim.setRepeatCount(0);
            anim.setInterpolator(new LinearInterpolator());
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    value = (float) animation.getAnimatedValue();
                    System.out.println("ddddddddddddddd " +value);
                    invalidate();
                }
            });
        }

        public void doAnimBig() {
            anim.cancel();
            if (value == MAX_BIG || stat == ANIM_TO_BIG) {
                return;
            }

            stat = ANIM_TO_BIG;
            anim.setFloatValues(value, MAX_BIG);
            anim.start();
        }

        public void doAnimRestore() {
            anim.cancel();
            if (value == MAX_BIG || stat == ANIM_TO_SM) {
                return;
            }

            stat = ANIM_TO_SM;
            anim.setFloatValues(value, MIN_NORMAL);
            anim.start();
        }

    }
}
