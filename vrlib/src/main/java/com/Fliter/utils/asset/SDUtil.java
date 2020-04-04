package com.Fliter.utils.asset;/**
 * Created by del on 17/4/21.
 */

import android.content.Context;
import android.os.Environment;

/**
 * created by lbw at 17/4/21
 */
public class SDUtil {
    /**
     * 当sd卡存在的时候和不存在的时候返回的路径
     * @param context
     * @return
     */
    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }
}
