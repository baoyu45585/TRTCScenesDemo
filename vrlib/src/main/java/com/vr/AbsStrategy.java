package com.vr;

import android.os.Handler;
import android.os.Looper;

import com.asha.vrlib.MD360Director;
import com.asha.vrlib.strategy.IModeStrategy;
import com.asha.vrlib.strategy.interactive.IInteractiveMode;
import com.asha.vrlib.strategy.interactive.InteractiveModeManager;

import java.util.List;

/**
 * Created by hzqiujiadi on 16/3/19.
 * hzqiujiadi ashqalcn@gmail.com
 */
public abstract class AbsStrategy implements IModeStrategy, IInteractiveMode {


    private Handler mMainHandler = null;

    protected Handler getMainHandler() {
        if (null == mMainHandler) {
            synchronized (this) {
                if (null == mMainHandler) {
                    mMainHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return mMainHandler;
    }

    protected void runOnUiThread(Runnable runnable) {
        getMainHandler().post(runnable);
    }


}
