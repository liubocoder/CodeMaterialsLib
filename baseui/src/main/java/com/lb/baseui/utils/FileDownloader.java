package com.lb.baseui.utils;

import android.os.*;
import android.support.annotation.NonNull;
import com.lb.baseui.log.Log;
import com.lb.baseui.utils.net.CommHttpUtil;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LiuBo
 * @date 2018-11-16
 */
public class FileDownloader {
    private static final int EVENT_DOWNLOAD_START = 1001;
    private HandlerThread mThread;
    private Handler mTaskHandler;
    private OkHttpClient mClient;

    private Handler mCallBackHandler;
    private int mDownloadingTaskCounter;

    public FileDownloader(Handler callBackHandler) {
        mThread = new HandlerThread("FileDownloader");
        mThread.start();
        mTaskHandler = new FileDownloader.TaskHandler(mThread.getLooper());
        mClient = CommHttpUtil.getInstance().getHttpClient();
        mCallBackHandler = callBackHandler;
    }

    public void release() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mThread.quitSafely();
        } else {
            mThread.quit();
        }
    }

    /**
     *
     * @param tasks 任务列表
     * @param tag 描述tag 在反馈事件的message.arg1 中
     */
    public synchronized void download(@NonNull List<Task> tasks, int tag) {
        List<Task> temp = new ArrayList<>(tasks);
        Message msg = mTaskHandler.obtainMessage();
        msg.what = EVENT_DOWNLOAD_START;
        msg.obj = temp;
        msg.arg1 = tag;

        mDownloadingTaskCounter = mDownloadingTaskCounter + 1;
        mTaskHandler.sendMessage(msg);
    }

    public synchronized boolean isDownloading() {
        return mDownloadingTaskCounter > 0;
    }

    private class TaskHandler extends Handler {
        private TaskHandler(Looper looper) {
            super(looper);
        }

        private void sendEvent(Task task, int event, int tag) {
            if (event == UtilEvent.COMM_FILE_DOWNLOAD_FAILED) {
                new File(task.downPath).delete();
            }
            Message msg = mCallBackHandler.obtainMessage(event);
            msg.obj = task;
            msg.arg1 = tag;
            mCallBackHandler.sendMessage(msg);
        }

        private InputStream doRequest(String url) {
            Request request = new Request.Builder().get().url(url).build();
            Call call = mClient.newCall(request);
            Response response;
            InputStream is = null;
            try {
                response = call.execute();
                is = response.body().byteStream();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return is;
        }

        private void saveToDisk(byte[] buf, InputStream is, OutputStream os) {
            try {
                int i;
                while ((i = is.read(buf)) != -1) {
                    os.write(buf, 0, i);
                }
                os.flush();
            } catch (IOException e) {
                // ignore
            } finally {
                CloseUtils.closeSilent(is);
                CloseUtils.closeSilent(os);
            }
        }

        @Override
        public void handleMessage(Message msg) {
            List<Task> temp = (List<Task>) msg.obj;
            int tag = msg.arg1;
            byte[] buf = new byte[1024];
            String md5Str;

            for (Task task : temp) {
                OutputStream os = null;
                File taskFile = new File(task.getDownPath());
                try {
                    if (!taskFile.exists()) {
                        Log.FILE.d("create file \"%s\" "+taskFile.createNewFile(), task.getDownPath());
                    }
                    os = new FileOutputStream(taskFile);
                } catch (IOException e) {
                    // ignore
                }
                if (os == null) {
                    Log.FILE.e("file path invalid "+task.getDownPath());
                    sendEvent(task, UtilEvent.COMM_FILE_DOWNLOAD_FAILED, tag);
                    continue;
                }
                InputStream is = doRequest(task.url);
                if (is == null) {
                    Log.FILE.e("do download request failed url is "+task.url);
                    CloseUtils.closeSilent(os);
                    sendEvent(task, UtilEvent.COMM_FILE_DOWNLOAD_FAILED, tag);
                    continue;
                }

                saveToDisk(buf, is, os);
                md5Str = CryptUtil.encodeByMd5(taskFile);
                if (!StringUtils.isEmpty(md5Str) && md5Str.equals(task.md5)) {
                    sendEvent(task, UtilEvent.COMM_FILE_DOWNLOAD_SUCCESS, tag);
                } else {
                    Log.FILE.e("digest md5 failed, url=%s, sMD5=%s, lMD5=%s",task.url,task.md5, md5Str);
                    sendEvent(task, UtilEvent.COMM_FILE_DOWNLOAD_FAILED, tag);
                }
            }

            sendEvent(null, UtilEvent.COMM_FILE_DOWNLOAD_FINISH, tag);
            synchronized (this) {
                mDownloadingTaskCounter = mDownloadingTaskCounter - 1;
            }
        }
    }

    public static class Task {
        private String md5;
        private String url;
        private String downPath;

        public Task(@NonNull String md5, @NonNull String url, @NonNull String filePath) {
            this.md5 = md5;
            this.url = url;
            this.downPath = filePath;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Task) {
                return md5.equals(((Task) obj).md5) && url.equals(((Task) obj).url);
            }
            return false;
        }

        public String getMd5() {
            return md5;
        }

        public String getUrl() {
            return url;
        }

        public String getDownPath() {
            return downPath;
        }
    }
}
