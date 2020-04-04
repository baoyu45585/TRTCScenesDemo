package com.vr;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.ndk.VrNativeUtils;

/**
 * vr工具类
 *
 * @author shenjb@china
 * @since 2020-03-03
 */
public class VRLibraryUtils {

    public static final int MODE_MOTION = 0;//vr全屏模式
    public static final int DISPLAY_MODE_GLASS = 1;//vr眼镜模式
    public static final int MODE_PLANE = 2;//平面模式
    public static final int MODE_PLANE_EYES = 3;//d2d眼镜模式
    public static final int MODE_UPDOWN_MOTION = 4;//原视频为上下两层 全屏显示
    public static final int MODE_UPDOWN_GLASS = 5;//原视频为上下两层 左右显示
    public static final int MODE_LEFTRIGHT_MOTION = 6;//原视频为左右两边 全屏显示
    public static final int MODE_LEFTRIGHT_GLASS = 7;//原视频为左右两边 左右显示


    private AbsStrategy strategy;
    private boolean isResume;
    private GLSurfaceView surfaceView;
    private Context context;
    private volatile int type = -1;
    private boolean isUseGoogleMion;

    /**
     * 初始化
     *
     * @param context         上下文
     * @param type            模式 //0：3d全景模式 1：3d眼镜模式 2:2d全景模式 3:2d眼镜模式
     * @param isUseGoogleMion 是否使用谷歌的陀螺仪
     * @param surfaceView
     */
    public void initMotion(Context context, int type, boolean isUseGoogleMion, GLSurfaceView surfaceView) {

        if (this.type == type) return;
        this.type = type;
        if (surfaceView == null || context == null) return;
        if (strategy != null) {
            strategy.turnOffInGL(context);
        }
        this.surfaceView = surfaceView;
        this.context = context;
        this.isUseGoogleMion = isUseGoogleMion;
        if (type == MODE_PLANE || type == MODE_PLANE_EYES) {//平面模式活2d眼镜
            VrNativeUtils.getInstance().sphere3dSettype(type);
            surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        } else {
            VrNativeUtils.getInstance().sphere3dSettype(type);
            if (isUseGoogleMion) {//判断使用什么陀螺仪
                strategy = new CardboardMotionStrategy();
            } else {
                strategy = new VrMotionStrategy();
            }
            strategy.turnOnInGL(context);
            if (strategy.isSupport(context)) {
                surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            }
        }
    }


    /**
     * 切换模式
     *
     * @param type
     */
    public void setMode(int type) {
        if (this.type == type) return;
        VrNativeUtils.getInstance().sphere3dSettype(type);
        if (type == MODE_PLANE || type == MODE_PLANE_EYES) {
            if (strategy != null && isResume) {
                onPause();
            }
            this.type = type;
            surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            surfaceView.requestRender();
        } else {
            if (strategy == null) {
                if (isUseGoogleMion) {
                    strategy = new CardboardMotionStrategy();
                } else {
                    strategy = new VrMotionStrategy();
                }
                strategy.turnOnInGL(context);
                if (strategy.isSupport(context)) {
                }
            }
            if (surfaceView.getRenderMode() == GLSurfaceView.RENDERMODE_WHEN_DIRTY) {
                surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            }
            this.type = type;
            if (!isResume) {
                onResume();
            }
        }


    }

    public void onResume() {
        if (type != MODE_PLANE && type != MODE_PLANE_EYES && strategy != null && strategy.isSupport(context)) {
            isResume = true;
            strategy.onResume(context);
        }
    }

    public void onPause() {
        if (strategy != null && strategy.isSupport(context)) {
            isResume = false;
            strategy.onPause(context);
        }
    }

    public int getMode() {
        return type;
    }
}
