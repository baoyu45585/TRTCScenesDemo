package com.vr;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.WindowManager;

import com.asha.vrlib.MD360Director;
import com.asha.vrlib.common.MDGLHandler;
import com.asha.vrlib.common.MDMainHandler;
import com.asha.vrlib.common.VRUtil;

import com.asha.vrlib.strategy.interactive.InteractiveModeManager;
import com.ndk.VrNativeUtils;

/**
 * Created by hzqiujiadi on 16/3/19.
 * hzqiujiadi ashqalcngmail.com
 */
public class VrMotionStrategy  extends AbsStrategy implements SensorEventListener {

    private static final String TAG = "MotionStrategy";
    private  MDGLHandler mGLHandler;
    private WindowManager windowManager;
    private VrNativeUtils nativeUtils2=null;

    private float[] mSensorMatrix = new float[16];

    private float[] mTmpMatrix = new float[16];

    private boolean mRegistered = false;

    private Boolean mIsSupport = null;

    private final Object mMatrixLock = new Object();

    private boolean isOn;

    public VrMotionStrategy() {
        mGLHandler = new MDGLHandler();
    }


    @Override
    public void onResume(Context context) {
        registerSensor(context);
    }


    @Override
    public void onPause(Context context) {
        unregisterSensor(context);
    }


    @Override
    public boolean handleDrag(int distanceX, int distanceY) {
        return false;
    }


    @Override
    public void onOrientationChanged(Context context) {
    }


    @Override
    public void turnOnInGL(Context context) {
        isOn = true;
        windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);

    }


    public void turnOffInGL(final Context context) {
        isOn = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                unregisterSensor(context);
            }
        });
    }

    @Override
    public boolean isSupport(Context context) {
        if (mIsSupport == null){
            SensorManager mSensorManager = (SensorManager) context
                    .getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            mIsSupport = (sensor != null);
        }
        return mIsSupport;
    }

    protected void registerSensor(Context context){
        if (mRegistered) return;

        SensorManager mSensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        if (sensor == null){
            Log.e(TAG,"TYPE_ROTATION_VECTOR sensor not support!");
            return;
        }

        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME, MDMainHandler.sharedHandler());

        mRegistered = true;
    }

    protected void unregisterSensor(Context context){
        if (!mRegistered) return;

        SensorManager mSensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.unregisterListener(this);

        mRegistered = false;
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (isOn && event.accuracy != 0){
            int type = event.sensor.getType();
            switch (type){
                case Sensor.TYPE_ROTATION_VECTOR:
                    // post
                    if (windowManager != null){
                        VRUtil.sensorRotationVector2Matrix(event, windowManager.getDefaultDisplay().getRotation(), mSensorMatrix);
                    }

                    // mTmpMatrix will be used in multi thread.
                    synchronized (mMatrixLock){
                        System.arraycopy(mSensorMatrix, 0, mTmpMatrix, 0, 16);
                    }
                    synchronized (mMatrixLock){
                        if(nativeUtils2==null)
                            nativeUtils2= VrNativeUtils.getInstance();
                        nativeUtils2.sphere3dSetSensor(mTmpMatrix);
                    }
                    break;
            }
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
