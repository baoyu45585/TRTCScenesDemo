package com.vr;

import android.content.Context;
import android.content.res.Resources;

import com.asha.vrlib.MD360Director;
import com.asha.vrlib.strategy.interactive.AbsInteractiveStrategy;
import com.asha.vrlib.strategy.interactive.InteractiveModeManager;
import com.ndk.VrNativeUtils;

/**
 * Created by hzqiujiadi on 16/3/19.
 * hzqiujiadi ashqalcn@gmail.com
 */
public class VrTouchStrategy extends AbsStrategy {

    private static final float sDensity =  Resources.getSystem().getDisplayMetrics().density;

    private static final float sDamping = 0.2f;

    private static final String TAG = "TouchStrategy";
    private  float mDeltaX=0.0f;
    private  float mDeltaY=0.0f;

    public VrTouchStrategy() {
        super();
    }

    @Override
    public void onResume(Context context) {}

    @Override
    public void onPause(Context context) {}

    @Override
    public boolean handleDrag(final int distanceX, final int distanceY) {
        mDeltaX = mDeltaX+distanceX / 30.0f * sDamping;
        mDeltaY = mDeltaY+distanceY / 30.0f * sDamping;
        VrNativeUtils.getInstance().sphere3dSetFingerRotation(mDeltaX,mDeltaY);

        return false;
    }

    @Override
    public void onOrientationChanged(Context context) {

    }

    @Override
    public void turnOnInGL(Context context) {

    }

    @Override
    public void turnOffInGL(Context context) {
    }

    @Override
    public boolean isSupport(Context context) {
        return true;
    }
}
