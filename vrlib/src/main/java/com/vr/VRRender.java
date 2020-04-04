package com.vr;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Surface;

import com.asha.vrlib.R;
import com.ndk.VrNativeUtils;
import com.vr.utils.WlShaderUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class VRRender implements GLSurfaceView.Renderer {

    public static final int NO_TEXTURE = -1;
    private Context context;
    private float[] mSTMatrix = new float[16];
    private int textureId;
    private int videoWidth;
    private int videoHeight;
    private VrNativeUtils nativeUtils=null;
    VideoTexture videoTexture;
    public VRRender(Context context, GLSurfaceView surfaceView, SurfaceListener listener) {
        this.context = context;

        this.surfaceListener=listener;
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setRenderer(this);//设置renderer
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        if(nativeUtils==null){
            nativeUtils=VrNativeUtils.getInstance();
        }
        textureId = nativeUtils.createtextureid();
        videoTexture=new VideoTexture(new VideoTexture.IOnSurfaceReadyCallback() {
            @Override
            public void onSurfaceReady(Surface surface) {
                if (surfaceListener != null) surfaceListener.onSurface(surface);
            }
        });
        videoTexture.setGlSurfaceView(surfaceView);
        videoTexture.onCreateSurface(textureId);
    }

    public void setVideoWH(int videoWidth,int videoHeight){
        this.videoWidth=videoWidth;
        this.videoHeight=videoHeight;
        nativeUtils.sphere3dSetvideosize(videoWidth,videoHeight);
    }

    public void notifyChanged(){
        if (videoTexture!=null){
            videoTexture.notifyChanged();
        }
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String vertexSource = WlShaderUtil.readRawTxt(context, R.raw.vr_vertex_gl_shader);
        String fragmentSource = WlShaderUtil.readRawTxt(context, R.raw.vr_fragment_plane_shader);

        String barreldistortionvertexSource = WlShaderUtil.readRawTxt(context, R.raw.vr_vertex_gl_barreldistortion_shader);
        String barreldistortionfragmentSource = WlShaderUtil.readRawTxt(context, R.raw.vr_fragment_barreldistortion);

        String planevertexSource = WlShaderUtil.readRawTxt(context, R.raw.vr_vertex_gl_plane);
        String planefragmentSource = WlShaderUtil.readRawTxt(context, R.raw.vr_fragment_plane);
        if(nativeUtils!=null) {
            nativeUtils.createProgram(vertexSource, fragmentSource);
            nativeUtils.barreldistortioncreateProgram(barreldistortionvertexSource, barreldistortionfragmentSource);
            nativeUtils.PlanecreateProgram(planevertexSource, planefragmentSource);

        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.e("11122","ddddd"+width+""+height);

        nativeUtils.sphere3dinit(width, height);
        if(nativeUtils!=null)
        nativeUtils.sphere3dsurfacechange(width,height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (videoTexture!=null){
            videoTexture.onDrawFrame(mSTMatrix);
        }
        if(nativeUtils!=null){
            nativeUtils.sphere3drender(mSTMatrix);
        }
    }

    public void destroy() {
        if(nativeUtils!=null)
            nativeUtils.sphere3ddestroy();
        if (videoTexture!=null){
            videoTexture.destroy();
        }
    }

    SurfaceListener surfaceListener;

    public void setOnSurfacerListenerr(SurfaceListener surfaceListener) {
        this.surfaceListener = surfaceListener;
    }

    public interface SurfaceListener {
        void onSurface(Surface surface);
    }





}
