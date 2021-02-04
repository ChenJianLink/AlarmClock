package cn.chenjianlink.android.alarmclock.utils;

import android.util.Log;


/**
 * @author chenjian
 * 日志输出控制工具
 */
public class LogUtil {
    private static final int VERBOSE = 1;
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARN = 4;
    private static final int ERROR = 5;
    private static int level;

    static {
        level = Integer.parseInt(PropertiesUtil.getConfig("log_level"));
    }

    public static void v(String tag, String msg) {
        if (level <= VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (level <= DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (level <= INFO) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (level <= WARN) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg, Exception e) {
        if (level <= ERROR) {
            Log.e(tag, msg, e);
        }
    }
}
