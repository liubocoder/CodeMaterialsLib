package com.lb.baseui.log;

import android.text.TextUtils;

import java.util.Locale;

public final class Logger {

    private String TAG;

    public Logger(String mTAG) {
    	TAG = mTAG;
    }

    /**
     * Send a LoggerConfig.DEBUG_ALL log message.
     * @param arr The message you would like logged.
     */
    public void dump(String prefix, byte[] arr){
        if (LoggerConfig.DEBUG_ALL || LoggerConfig.WRITE_TO_FILE) {
            if (TextUtils.isEmpty(prefix)){
                prefix = "";
            }
            StringBuilder sBuilder = new StringBuilder(prefix);
            for (byte item : arr){
                if (item >= 0 && item < 10){
                    sBuilder.append("0");
                }
                sBuilder.append(Integer.toHexString(item).toUpperCase()).append(" ");
            }
            String ms = buildMessage(sBuilder.toString());
            if (LoggerConfig.DEBUG_ALL){
                android.util.Log.d(TAG, ms);
            }
            if (LoggerConfig.WRITE_TO_FILE) {
                Log.printToFile(ms);
            }
        }


    }
    /**
     * Send a VERBOSE log message.
     * @param msg The message you would like logged.
     */
    public void v(String msg) {
        if (LoggerConfig.DEBUG_ALL || LoggerConfig.WRITE_TO_FILE) {
            String ms = buildMessage(msg);
            if (LoggerConfig.DEBUG_ALL) {
                android.util.Log.v(TAG, ms);
            }
            if (LoggerConfig.WRITE_TO_FILE) {
                Log.printToFile(ms);
            }
        }
    }

    /**
     * Send a VERBOSE log message and log the exception.
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public void v(String msg, Throwable thr) {
        if (LoggerConfig.DEBUG_ALL || LoggerConfig.WRITE_TO_FILE) {
            String ms = buildMessage(msg+" "+thr.getMessage());
            if (LoggerConfig.DEBUG_ALL) {
                android.util.Log.v(TAG, buildMessage(msg), thr);
            }
            if (LoggerConfig.WRITE_TO_FILE) {
                Log.printToFile(ms);
            }
        }
    }

    /**
     * Send a VERBOSE log message.
     *
     * @param format The message you would like logged.
     */
    public void v(String format, Object... args) {
        if (LoggerConfig.DEBUG_ALL || LoggerConfig.WRITE_TO_FILE) {
            String ms = buildMessage(formatArgs(format, args));
            if (LoggerConfig.DEBUG_ALL) {
                android.util.Log.v(TAG, ms);
            }
            if (LoggerConfig.WRITE_TO_FILE) {
                Log.printToFile(ms);
            }
        }
    }

    /**
     * Send a LoggerConfig.DEBUG_ALL log message.
     * @param msg
     */
    public void d(String msg) {
        if (LoggerConfig.DEBUG_ALL || LoggerConfig.WRITE_TO_FILE) {
            String ms = buildMessage(msg);
            if (LoggerConfig.DEBUG_ALL) {
                android.util.Log.d(TAG, ms);
            }
            if (LoggerConfig.WRITE_TO_FILE) {
                Log.printToFile(ms);
            }
        }
    }

    /**
     * Send a LoggerConfig.DEBUG_ALL log message and log the exception.
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public void d(String msg, Throwable thr) {
        if (LoggerConfig.DEBUG_ALL || LoggerConfig.WRITE_TO_FILE) {
            String ms = buildMessage(msg+" "+thr.getMessage());
            if (LoggerConfig.DEBUG_ALL) {
                android.util.Log.d(TAG, ms, thr);
            }

            if (LoggerConfig.WRITE_TO_FILE) {
                Log.printToFile(ms);
            }
        }
    }

    /**
     * Send a LoggerConfig.DEBUG_ALL log message.
     *
     * @param format The message you would like logged.
     */
    public void d(String format, Object... args) {
        if (LoggerConfig.DEBUG_ALL || LoggerConfig.WRITE_TO_FILE) {
            String ms = buildMessage(formatArgs(format, args));
            if (LoggerConfig.DEBUG_ALL) {
                android.util.Log.d(TAG, ms);
            }
            if (LoggerConfig.WRITE_TO_FILE) {
                Log.printToFile(ms);
            }
        }
    }

    /**
     * Send an INFO log message.
     * @param msg The message you would like logged.
     */
    public void i(String msg) {
        if (LoggerConfig.DEBUG_ALL || LoggerConfig.WRITE_TO_FILE) {
            String ms = buildMessage(msg);
            if (LoggerConfig.DEBUG_ALL) {
                android.util.Log.i(TAG, ms);
            }
            if (LoggerConfig.WRITE_TO_FILE) {
                Log.printToFile(ms);
            }
        }

    }

    /**
     * Send a INFO log message and log the exception.
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public void i(String msg, Throwable thr) {
        if (LoggerConfig.DEBUG_ALL || LoggerConfig.WRITE_TO_FILE) {
            String ms = buildMessage(msg+" "+thr.getMessage());
            if (LoggerConfig.DEBUG_ALL) {
                android.util.Log.i(TAG, ms, thr);
            }
            if (LoggerConfig.WRITE_TO_FILE) {
                Log.printToFile(ms);
            }
        }
    }

    /**
     * Send a INFO log message.
     *
     * @param format The message you would like logged.
     */
    public void i(String format, Object... args) {
        if (LoggerConfig.DEBUG_ALL || LoggerConfig.WRITE_TO_FILE) {
            String ms = buildMessage(formatArgs(format, args));
            if (LoggerConfig.DEBUG_ALL) {
                android.util.Log.i(TAG, ms);
            }
            if (LoggerConfig.WRITE_TO_FILE) {
                Log.printToFile(ms);
            }
        }
    }

    /**
     * Send an ERROR log message.
     * @param msg The message you would like logged.
     */
    public void e(String msg) {
        if (LoggerConfig.DEBUG_ALL || LoggerConfig.WRITE_TO_FILE) {
            String ms = buildMessage(msg);
            if (LoggerConfig.DEBUG_ALL) {
                android.util.Log.e(TAG, ms);
            }
            if (LoggerConfig.WRITE_TO_FILE) {
                Log.printToFile(ms);
            }
        }
    }

    /**
     * Send a WARN log message
     * @param msg The message you would like logged.
     */
    public void w(String msg) {
        if (LoggerConfig.DEBUG_ALL || LoggerConfig.WRITE_TO_FILE) {
            String ms = buildMessage(msg);
            if (LoggerConfig.DEBUG_ALL) {
                android.util.Log.w(TAG, ms);
            }
            if (LoggerConfig.WRITE_TO_FILE) {
                Log.printToFile(ms);
            }
        }
    }

    /**
     * Send a WARN log message and log the exception.
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public void w(String msg, Throwable thr) {
        if (LoggerConfig.DEBUG_ALL || LoggerConfig.WRITE_TO_FILE) {
            String ms = buildMessage(msg+" "+thr.getMessage());
            if (LoggerConfig.DEBUG_ALL) {
                android.util.Log.w(TAG, ms, thr);
            }
            if (LoggerConfig.WRITE_TO_FILE) {
                Log.printToFile(ms);
            }
        }
    }

    /**
     * Send an empty WARN log message and log the exception.
     * @param thr An exception to log
     */
    public void w(Throwable thr) {
        if (LoggerConfig.DEBUG_ALL || LoggerConfig.WRITE_TO_FILE) {
            String ms = buildMessage(thr.getMessage());
            if (LoggerConfig.DEBUG_ALL) {
                android.util.Log.w(TAG, ms, thr);
            }
            if (LoggerConfig.WRITE_TO_FILE) {
                Log.printToFile(ms);
            }
        }
    }

    /**
     * Send a WARN log message.
     *
     * @param format The message you would like logged.
     */
    public void w(String format, Object... args) {
        if (LoggerConfig.DEBUG_ALL || LoggerConfig.WRITE_TO_FILE) {
            String ms = buildMessage(formatArgs(format, args));
            if (LoggerConfig.DEBUG_ALL) {
                android.util.Log.w(TAG, ms);
            }
            if (LoggerConfig.WRITE_TO_FILE) {
                Log.printToFile(ms);
            }
        }

    }

    /**
     * Send an ERROR log message and log the exception.
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public void e(String msg, Throwable thr) {
        if (LoggerConfig.DEBUG_ALL || LoggerConfig.WRITE_TO_FILE) {
            String ms = buildMessage(msg+" "+thr.getMessage());
            if (LoggerConfig.DEBUG_ALL) {
                android.util.Log.e(TAG, ms, thr);
            }
            if (LoggerConfig.WRITE_TO_FILE) {
                Log.printToFile(ms);
            }
        }
    }

    /**
     * Send a ERROR log message.
     *
     * @param format The message you would like logged.
     */
    public void e(String format, Object... args) {
        if (LoggerConfig.DEBUG_ALL || LoggerConfig.WRITE_TO_FILE) {
            String ms = buildMessage(formatArgs(format, args));
            if (LoggerConfig.DEBUG_ALL) {
                android.util.Log.e(TAG, ms);
            }
            if (LoggerConfig.WRITE_TO_FILE) {
                Log.printToFile(ms);
            }
        }

    }

    /**
     * Send a System.out log message.
     *
     * @param format The message you would like logged.
     */
    public void p(String format, Object... args) {
        if (LoggerConfig.DEBUG_ALL || LoggerConfig.WRITE_TO_FILE) {
            String ms = buildMessage(formatArgs(format, args));
            if (LoggerConfig.DEBUG_ALL) {
                System.out.println(ms);
            }
            if (LoggerConfig.WRITE_TO_FILE) {
                Log.printToFile(ms);
            }
        }
    }

    /**
     * 获取格式化的字符串
     */
    private String formatArgs(String format, Object... args) {
        //判断参数
        if (args == null || args.length == 0) {
            return format;
        } else {
            return String.format(Locale.getDefault(),format, args);
        }
    }

    /**
     * Building Message
     * @param msg The message you would like logged.
     * @return Message String
     */
    private String buildMessage(String msg) {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
        return new StringBuilder()
                .append(caller.getFileName() != null ? caller.getFileName().replace(".java", "") : "null")
                .append(".")
                .append(caller.getMethodName())
                .append("(").append(caller.getLineNumber()).append("):")
                .append(msg).toString();
    }
}
