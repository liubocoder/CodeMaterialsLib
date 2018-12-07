package com.lb.baseui.utils;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;

/**
 * 异步handler
 * @author LiuBo
 * @date 2018-10-30
 */
public class AsyncHandlerUtils {

    public static Handler buildAsyncHandler(String threadName, Handler.Callback callback) {
        HandlerThread thread = new HandlerThread(threadName);
        thread.start();
        LocalCallback localCallback = new LocalCallback(thread, callback);
        return new Handler(thread.getLooper(), localCallback);
    }

    private static class LocalCallback implements Handler.Callback {
        private HandlerThread mThread;
        private Handler.Callback mCallback;
        public LocalCallback(@NonNull HandlerThread thread, Handler.Callback callback) {
            mThread = thread;
            mCallback = callback;
        }
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == UtilEvent.COMM_THREAD_EVENT_QUIT) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    mThread.quitSafely();
                } else {
                    mThread.quit();
                }
                return true;
            }
            if (mCallback != null) {
                return mCallback.handleMessage(msg);
            }
            return false;
        }
    }

}
