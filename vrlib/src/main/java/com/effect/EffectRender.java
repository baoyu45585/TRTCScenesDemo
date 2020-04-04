package com.effect;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.Surface;

import com.ndk.EffectNativeUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class EffectRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private Context context;
    private float[] mSTMatrix = new float[16];
    private int textureId=-1;
    private SurfaceTexture surfaceTexture;
    private Surface surface;
    private GLSurfaceView surfaceView;
    private SurfaceListener surfaceListener;
    public volatile boolean flag;

    public EffectRender(Context context, GLSurfaceView surfaceView, SurfaceListener surfaceListener) {
        this.context = context;
        this.surfaceView = surfaceView;
        this.surfaceListener=surfaceListener;
        surfaceView.setEGLConfigChooser(8, 8, 8, 8,
                16, 0);
        surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        surfaceView.setZOrderOnTop(true);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setRenderer(this);//设置renderer
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        textureId= EffectNativeUtils.getInstance().createtextureid();
        surfaceTexture = new SurfaceTexture(textureId);
        surface = new Surface(surfaceTexture);
        surfaceTexture.setOnFrameAvailableListener(this);
        if (surfaceListener != null) surfaceListener.onSurface(surface);

    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        EffectNativeUtils.getInstance().effectinit();

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        EffectNativeUtils.getInstance().setscreenwidthheight(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        renderonDrawFrame();
        synchronized (this){
            if (flag){
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            }
        }

    }


    private void renderonDrawFrame() {
        surfaceTexture.updateTexImage();//获取新数据
        surfaceTexture.getTransformMatrix(mSTMatrix);//让新的纹理和纹理坐标系能够正确的对应,mSTMatrix的定义是和projectionMatrix完全一样的。
        EffectNativeUtils.getInstance().effectrender(mSTMatrix,textureId);
    }


    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        surfaceView.requestRender();
    }

    public void setVideoWH(int videoWidth, int videoHeight) {
        EffectNativeUtils.getInstance().setvideowidthheight(videoWidth,videoHeight);
    }


    public void destroy() {
        if( surfaceTexture != null ) {
            surfaceTexture.release();
            surfaceTexture = null;
        }
        EffectNativeUtils.getInstance().destroy();
    }



    public interface SurfaceListener {
        void onSurface(Surface surface);
    }


    public void notifyChanged() {
        if (surfaceListener != null) {
            if (surfaceTexture != null){
                surface = new Surface(surfaceTexture);
            }
            surfaceListener.onSurface(surface);
        }
    }

    public void setFlag(boolean flag) {
        synchronized (this){
            this.flag = flag;
            if (flag){
                surfaceView.requestRender();
            }
        }
    }


    public void releaseSurface() {
        if (surface != null){
            surface.release();
            surface = null;
        }
    }

}
