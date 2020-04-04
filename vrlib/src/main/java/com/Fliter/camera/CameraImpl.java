package com.Fliter.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.util.Set;

/**
 * Camera 策略模式 管理
 */
public abstract class CameraImpl {

    protected Context mContext;

    CameraImpl(Context context) {
        this.mContext = context;

        init();
    }


    /**
     * 初始化
     *
     */
    private void init() {
    }

    abstract void init(Callback callback, SurfaceTexture surfaceTexture);

    public boolean onStart() {
        return start();
    }

    public void onStop() {
        this.stop();
    }

    /**
     * 《========== Camera设置 ===========》
     */
    abstract boolean start();

    abstract void stop();

    abstract void display();

    abstract boolean isCameraOpened();

    abstract void setFacing(int facing);

    abstract int getFacing();

    abstract Set<AspectRatio> getSupportedAspectRatios();

    abstract int getOrientation();

    /**
     * 《========== Camera 参数变更 =========》
     */
    abstract boolean setAspectRatio(AspectRatio ratio);

    abstract AspectRatio getAspectRatio();

    abstract void setAutoFocus(boolean autoFocus);

    abstract boolean getAutoFocus();

    abstract void setFlash(int flash);

    abstract int getFlash();
    public  abstract Camera getCamera();

    abstract void takePicture();

    abstract void setDisplayOrientation(int displayOrientation);

    abstract void setSurfaceChanged();

    abstract void setBufferSize(int width, int height);

    abstract void setSize(int width, int height);

    //手动聚焦
    abstract boolean onFocus(Point point, AutoFocusCallback callback);

    /**
     * 《========= 数据回调 ==========》
     */
    public interface Callback {

        void onCameraOpened();

        void onCameraClosed();

        void onPreviewFrame(byte[] data);

    }

    public interface AutoFocusCallback{
        void onAutoFocus(boolean success);
    }
}
