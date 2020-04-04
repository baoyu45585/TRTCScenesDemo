package com.vr;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.view.Surface;

import com.google.android.apps.muzei.render.GLTextureView;

/**
 * SurfaceTexture管理类
 * by hzqiujiadi on 16/4/5.
 * hzqiujiadi ashqalcn@gmail.com
 */
public class VideoTexture  {

    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;
    private IOnSurfaceReadyCallback mOnSurfaceReadyListener;
    private int mode=GLTextureView.RENDERMODE_CONTINUOUSLY;
    private volatile boolean updateImage;

    GLSurfaceView glSurfaceView;
    public VideoTexture(IOnSurfaceReadyCallback onSurfaceReadyListener) {
        mOnSurfaceReadyListener = onSurfaceReadyListener;
    }

    public interface IOnSurfaceReadyCallback {
        void onSurfaceReady(Surface surface);
    }
    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setGlSurfaceView(GLSurfaceView glSurfaceView) {
        this.glSurfaceView = glSurfaceView;
    }

    public GLSurfaceView getGlSurfaceView() {
        return glSurfaceView;
    }

    public void destroy() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
        }
        mSurfaceTexture = null;

        if (mSurface != null) {
            mSurface.release();
        }
        mSurface = null;
    }

    public void onCreateSurface(int glSurfaceTextureId) {
        if (mSurfaceTexture != null){
            mSurfaceTexture.release();
        }
        mSurfaceTexture = new SurfaceTexture(glSurfaceTextureId);
        mSurface = new Surface(mSurfaceTexture);
        if (mOnSurfaceReadyListener != null){
            mOnSurfaceReadyListener.onSurfaceReady(mSurface);
        }
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                synchronized (this){
                    updateImage=true;
                }
                if (glSurfaceView.getRenderMode()!=GLSurfaceView.RENDERMODE_CONTINUOUSLY){
                    if (glSurfaceView!=null){
                        glSurfaceView.requestRender();
                    }
                }

            }
        });

    }


    public void onDrawFrame(float[] mSTMatrix){
        if (mSurfaceTexture!=null){
            try {
                synchronized (this){
                    if (updateImage){
                        mSurfaceTexture.updateTexImage();//获取新数据
                        mSurfaceTexture.getTransformMatrix(mSTMatrix);
                        updateImage=false;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public void notifyChanged() {
        if (mOnSurfaceReadyListener != null) {
            if (mSurfaceTexture != null){
                mSurface = new Surface(mSurfaceTexture);
            }
            mOnSurfaceReadyListener.onSurfaceReady(mSurface);
        }
    }

    public void releaseSurface() {
        if (mSurface != null){
            mSurface.release();
            mSurface = null;
        }
    }
}
