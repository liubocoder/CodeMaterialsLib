package com.lb.baseui.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.lb.baseui.log.LoggerConfig.*;


/**
 *
 * 日志存储采用异步存储，synchronized (Log.class)，因此在存储、读取内存或者本地日志时注意死锁问题。
 *
 * @author LiuBo
 * @date 2018-09-29
 */
public final class Log {
    public static final Logger ACTIVITY = new Logger("ACTT");
    public static final Logger VIEW = new Logger("VIEW");
    public static final Logger HTTP = new Logger("HTTP");
    public static final Logger DB = new Logger("DB");
    public static final Logger FILE = new Logger("FILE");
    public static final Logger LIST = new Logger("LIST");

    private static StringBuilder sBuilder = new StringBuilder();
    private static int sLines = 0;

    static synchronized void printToFile(String msg) {
        sBuilder.append(System.currentTimeMillis())
                .append(":")
                .append(Thread.currentThread().getId())
                .append(":")
                .append(msg)
                .append("\n");
        sLines++;
        if (sLines > LoggerConfig.MAX_LOG_BUFFER) {
            doSaveToFile();
        }
    }

    private static void doSaveToFile() {
        try {
            FileOutputStream fos = new FileOutputStream(getLogFile(), true);
            fos.write(sBuilder.toString().getBytes());
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("save log, lines="+sLines);
        sBuilder.setLength(0);
        sLines = 0;
    }

    /**
     * 同步存储
     */
    public synchronized static void syncDoSaveToFile() {
        if (sLines > 0) {
            doSaveToFile();
        }
    }

    private static File getLogFile() {
        File log1 = new File(LOG_PATH+File.separator+ST_LOG_FILE1);
        createFile(log1);
        if (log1.length() < MAX_LOG_SIZE) {
            return log1;
        }

        File log2 = new File(LOG_PATH+File.separator+ST_LOG_FILE2);
        createFile(log2);
        if (log2.length() < MAX_LOG_SIZE) {
            return log2;
        }

        File tagFile = log1.lastModified() > log2.lastModified() ? log2 : log1;
        System.out.println(tagFile.getName()+" del "+tagFile.delete());
        createFile(tagFile);
        return tagFile;
    }

    private static void createFile(File file) {
        if (file.exists()) {
            return;
        }
        try {
            boolean createLogFile = file.createNewFile();
            System.out.println(file.getName()+" create "+createLogFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取日志文件，两个文件
     * @return File[]
     */
    public static File[] getLogFiles() {
        return new File[]{new File(LOG_PATH+File.separator+ST_LOG_FILE1),
                new File(LOG_PATH+File.separator+ST_LOG_FILE2)};
    }
}
