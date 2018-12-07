package com.lb.baseui.utils;

/**
 * @author LiuBo
 * @date 2018-09-30
 */
public final class UtilEvent {
    private static final int COMM_EVENT_CORE_BEGIN = 10000;
    /**
     * handlerThread退出
     */
    public static final int COMM_THREAD_EVENT_QUIT = COMM_EVENT_CORE_BEGIN + 1;

    /**
     * 文件资源下载成功
     */
    public static final int COMM_FILE_DOWNLOAD_SUCCESS = COMM_EVENT_CORE_BEGIN + 4;
    /**
     * 文件资源下载失败
     */
    public static final int COMM_FILE_DOWNLOAD_FAILED = COMM_EVENT_CORE_BEGIN + 5;
    /**
     * 文件资源下载完成
     */
    public static final int COMM_FILE_DOWNLOAD_FINISH = COMM_EVENT_CORE_BEGIN + 6;

    public UtilEvent() {
    }
    public UtilEvent(int event) {
        this.event = event;
    }
    public int event;
    public int arg;
    public Object data1;
    public Object data2;
}
