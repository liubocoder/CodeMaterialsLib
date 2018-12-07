package com.lb.baseui.utils.net;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import rx.Scheduler;
import rx.Subscription;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Action0;
import rx.plugins.RxJavaPlugins;
import rx.subscriptions.Subscriptions;

import java.util.concurrent.TimeUnit;

/**
 * @author LiuBo
 * @date 2018-10-17
 */
public class WorkSchedulers extends Scheduler {
    private static WorkSchedulers sInstance;
    private Handler handler;
    private HandlerThread thread;
    public WorkSchedulers() {
        thread = new HandlerThread("WorkSchedulers");
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    public static void initWorkThread() {
        if (sInstance == null) {
            synchronized (WorkSchedulers.class) {
                if (sInstance == null) {
                    sInstance = new WorkSchedulers();
                }
            }
        }
    }

    public static WorkSchedulers getWorkSchedulers() {
        if (sInstance == null) {
            throw new IllegalStateException("you need init work thread first!!!");
        }
        return sInstance;
    }

    @Override
    public Worker createWorker() {
        return new WorkSchedulers.HandlerWorker(handler);
    }

    static class HandlerWorker extends Worker {
        private final Handler handler;
        private final RxAndroidSchedulersHook hook;
        private volatile boolean unsubscribed;

        HandlerWorker(Handler handler) {
            this.handler = handler;
            this.hook = RxAndroidPlugins.getInstance().getSchedulersHook();
        }

        @Override
        public void unsubscribe() {
            unsubscribed = true;
            handler.removeCallbacksAndMessages(this /* token */);
        }

        @Override
        public boolean isUnsubscribed() {
            return unsubscribed;
        }

        @Override
        public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            if (unsubscribed) {
                return Subscriptions.unsubscribed();
            }

            action = hook.onSchedule(action);

            WorkSchedulers.ScheduledAction scheduledAction = new WorkSchedulers.ScheduledAction(action, handler);

            Message message = Message.obtain(handler, scheduledAction);
            message.obj = this; // Used as token for unsubscription operation.

            handler.sendMessageDelayed(message, unit.toMillis(delayTime));

            if (unsubscribed) {
                handler.removeCallbacks(scheduledAction);
                return Subscriptions.unsubscribed();
            }

            return scheduledAction;
        }

        @Override
        public Subscription schedule(final Action0 action) {
            return schedule(action, 0, TimeUnit.MILLISECONDS);
        }
    }

    static final class ScheduledAction implements Runnable, Subscription {
        private final Action0 action;
        private final Handler handler;
        private volatile boolean unsubscribed;

        ScheduledAction(Action0 action, Handler handler) {
            this.action = action;
            this.handler = handler;
        }

        @Override public void run() {
            try {
                action.call();
            } catch (Throwable e) {
                // nothing to do but print a System error as this is fatal and there is nowhere else to throw this
                IllegalStateException ie;
                if (e instanceof OnErrorNotImplementedException) {
                    ie = new IllegalStateException("Exception thrown on Scheduler.Worker thread. Add `onError` handling.", e);
                } else {
                    ie = new IllegalStateException("Fatal Exception thrown on Scheduler.Worker thread.", e);
                }
                RxJavaPlugins.getInstance().getErrorHandler().handleError(ie);
                Thread thread = Thread.currentThread();
                thread.getUncaughtExceptionHandler().uncaughtException(thread, ie);
            }
        }

        @Override public void unsubscribe() {
            unsubscribed = true;
            handler.removeCallbacks(this);
        }

        @Override public boolean isUnsubscribed() {
            return unsubscribed;
        }
    }
}
