package com.ndk;

public class EffectNativeUtils {

    private static volatile boolean mIsLibLoaded = false;
    private volatile static EffectNativeUtils instance = null;

    public static EffectNativeUtils getInstance() {
        if (instance == null) {
            synchronized (EffectNativeUtils.class) {
                if (instance == null) {
                    instance = new EffectNativeUtils();
                }
            }
        }
        return instance;
    }

    private EffectNativeUtils()
    {
        synchronized (EffectNativeUtils.class) {
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

    public  native int createtextureid();

    public  native int effectinit();

    public  native int effectrender(float[] matrix,int textureid);

    public  native int setscreenwidthheight(int screenWidth,int screenHeight);

    public  native int setvideowidthheight(int videoWidth,int videoHeight);

    public  native int destroy();
}
