package com.lb.baseui.utils;


import android.os.Handler;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

/**
 * 粗略的时间计数  计数间隔1s
 * 使用系统时钟可以减少误差
 * Created by LiuBo on 2016-12-22.
 */

public class TimeCounter {
    private static final int SF_COUNT_INTERVAL = 1000; //1s

    private boolean mUseSystemClock = false;
    private boolean mStopped = true;
    public TimeCounter() {}
    public TimeCounter(boolean ueSystemClock) {
        mUseSystemClock = ueSystemClock;
    }

    private ITimeCallBack mCallBack;
    public void setTimeCallBack(ITimeCallBack callBack) {
        mCallBack = callBack;
    }

    public void start() {
        if (mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        mHandler.postDelayed(mRunnable = buildRunnable(), SF_COUNT_INTERVAL);
        clearCounter();
        mSystemClock = SystemClock.elapsedRealtime();
        mStopped = false;
    }
    public void stop() {
        clearCounter();
        mStopped = true;
        if (mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
            mRunnable = null;
        }
    }

    private List<Long> mPausedTimeList = new ArrayList<>();
    private long mPausedTimeStamp;
    private boolean mPaused = false;
    /**暂停 true 暂停  false 重新开始*/
    public void pause(boolean pause) {
        if (mPaused == pause || mStopped) {
            return;
        }
        mPaused = pause;
        if (mPaused) {
            mPausedTimeStamp = SystemClock.elapsedRealtime();
        } else {
            mPausedTimeList.add(SystemClock.elapsedRealtime() - mPausedTimeStamp);
        }
    }

    public boolean isRunning() {
        return !(mStopped || mPaused);
    }

    /**清除计时器*/
    private void clearCounter() {
        resetTimer();
        mStopped = true;
        mPaused = false;
    }

    /**重置计时器*/
    public void resetTimer() {
        mCounter = 0;
        mPausedTimeList.clear();
    }

    /**获取已经暂停多久 单位s*/
    private int getPausedTimeInterval() {
        long interval = 0L;
        for (Long aLong : mPausedTimeList) {
            interval = (interval + aLong);
        }
        return (int) Math.round(interval / 1000D);
    }

    private int getSytClockPassed() {
        int totalPs = (int) Math.round((SystemClock.elapsedRealtime() - mSystemClock) / 1000D);
        return totalPs - getPausedTimeInterval();
    }

    private long mSystemClock;
    private int mCounter;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private Runnable buildRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                if (mStopped || mRunnable != this) {
                    return;
                }
                if (!mPaused) {
                    counter();
                }
                mHandler.postDelayed(this, SF_COUNT_INTERVAL);
            }
        };
    }
    private void counter() {
        int passTime;
        if (mUseSystemClock) {
            passTime = getSytClockPassed();
        } else {
            mCounter++;
            passTime = mCounter;
        }

        if (mCallBack != null) {
            mCallBack.timeCallBack(passTime);
        }
    }

    public interface ITimeCallBack {
        /**
         * 时钟回调
         * @param passTime 从开始到现在过去了多少时间，单位s
         */
        void timeCallBack(int passTime);
    }
}
