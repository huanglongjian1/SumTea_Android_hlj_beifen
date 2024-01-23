package com.sum.common.util;

import android.util.Log;

public class Loge {
    private static boolean cancel=false;

    public static void setLogeCancel(boolean cancel) {
        Loge.cancel = cancel;
    }

    public static void e(String s) {
        if (cancel) return;
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        String simpleClassName = className.substring(className.lastIndexOf(".") + 1);
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        Log.e(simpleClassName + "*" + methodName + "------", s);
    }

}
