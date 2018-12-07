package com.lb.baseui.log;

/**
 * 日志配置类
 * 初始化：日志缓存路径、日志enable标志、写入文件enable标志
 */
public class LoggerConfig {
    static final String ST_LOG_FILE1 = "st_log1.txt";
    static final String ST_LOG_FILE2 = "st_log2.txt";

    public static boolean WRITE_TO_FILE;
    public static boolean DEBUG_ALL = true;
    public static String LOG_PATH;
    public static long MAX_LOG_SIZE = 64 * 1024;
    public static int MAX_LOG_BUFFER = 100;

    public static void initLogger(boolean isDebug, boolean isWriteFile) {
        DEBUG_ALL = isDebug;
        WRITE_TO_FILE = isWriteFile;
    }

    /**
     * 初始化日志保存位置
     * @param path 路径
     */
    public static void initLoggerPath(String path) {
        LOG_PATH = path;
    }
}
