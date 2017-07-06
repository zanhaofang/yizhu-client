package com.sysu.yizhu.Util;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by QianZixuan on 2017/6/5.
 */
public final class AsyncRun {
    public static void run(Runnable runnable){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }
}
