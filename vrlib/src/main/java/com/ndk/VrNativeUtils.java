package com.ndk;

/**
 * vr底层工具类
 */
public class VrNativeUtils {

    private static volatile boolean mIsLibLoaded = false;
    private volatile static VrNativeUtils instance = null;

    public static VrNativeUtils getInstance() {
        if (instance == null) {
            synchronized (VrNativeUtils.class) {
                if (instance == null) {
                    instance = new VrNativeUtils();
                }
            }
        }
        return instance;
    }


    private VrNativeUtils()
    {
        synchronized (VrNativeUtils.class) {
            if (!mIsLibLoaded) {
                try {
                    System.loadLibrary("vr-so");
                }catch (Exception e){
                    e.printStackTrace();
                }
                mIsLibLoaded = true;
            }
        }
    }



    public  native int createProgram(String vertexShader,String fragShader);

    public  native int barreldistortioncreateProgram(String vertexShader,String fragShader);

    public  native int PlanecreateProgram(String vertexShader,String fragShader);

    public  native int createtextureid();

    public  native int sphere3dinit(int videoWidth,int videoHeight);//视图宽高

    public  native int sphere3drender(float[] matrix);

    public  native int sphere3dsurfacechange(int screenWidth,int screenHeight);

    public  native int sphere3ddestroy();

    public  native int sphere3dSetSensor(float[] matrix);

    public  native int sphere3dSettype(int type);//0：3d全景模式 1：3d眼镜模式 2:2d全景2模式 3:d2d眼镜模式 //4:原视频为上下两层全屏显示
    //5:原视频为上下两层 左右显示 6:原视频为左右两边 全屏显示 7:原视频为左右两边 左右显示

    public  native int sphere3dSetvideosize(int videoWidth,int videoHeigh);//视频宽高

    public  native int sphere3dSetFingerRotation(float fingerX,float fingerY);//传递触控位置

    public  native int sphere3dSetTouchMotionStatus(boolean touchstatus,boolean motionstatus);//设置触摸和陀螺仪功能



}
