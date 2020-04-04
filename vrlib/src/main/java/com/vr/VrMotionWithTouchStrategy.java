package com.vr;

import android.content.res.Resources;
import android.util.Log;

import com.ndk.VrNativeUtils;


/**
 * Created by hzqiujiadi on 16/6/10.
 * hzqiujiadi ashqalcn@gmail.com
 */
public class VrMotionWithTouchStrategy extends VrMotionStrategy {

    private static final float sDensity =  Resources.getSystem().getDisplayMetrics().density;

    private static final float sDamping = 0.2f;
    private  float mDeltaX=0.0f;
    private  float mDeltaY=0.0f;

    private VrNativeUtils nativeUtils3=null;
    public VrMotionWithTouchStrategy() {
        super();
    }

    @Override
    public boolean handleDrag(int distanceX, int distanceY) {
        mDeltaX = mDeltaX+distanceX / 30.0f * sDamping;
        mDeltaY = mDeltaY+distanceY / 30.0f * sDamping;

        if(nativeUtils3==null)
            nativeUtils3=VrNativeUtils.getInstance();
        nativeUtils3.sphere3dSetFingerRotation(mDeltaX,mDeltaY);
        return false;
    }

}
