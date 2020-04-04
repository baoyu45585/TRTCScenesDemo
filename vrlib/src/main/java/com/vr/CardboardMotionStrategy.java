package com.vr;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.asha.vrlib.common.MDGLHandler;
import com.google.vrtoolkit.cardboard.sensors.DeviceSensorLooper;
import com.google.vrtoolkit.cardboard.sensors.HeadTracker;
import com.google.vrtoolkit.cardboard.sensors.SystemClock;
import com.ndk.VrNativeUtils;

/**
 * Created by hzqiujiadi on 16/3/19.
 * hzqiujiadi ashqalcn@gmail.com
 */
public class CardboardMotionStrategy extends AbsStrategy implements SensorEventListener {

    private static final String TAG = "CardboardMotionStrategy";
    private  MDGLHandler mGLHandler;

    private boolean mRegistered = false;

    private Boolean mIsSupport = null;

    private float[] mTmpMatrix = new float[16];

    private final Object matrixLock = new Object();

    private HeadTracker headTracker;

    private DeviceSensorLooper mDeviceSensorLooper;

    private boolean isOn;

    private VrNativeUtils nativeUtils2=null;

    public CardboardMotionStrategy() {
        mGLHandler = new MDGLHandler();
    }

    public MDGLHandler getmGLHandler() {
        return mGLHandler;
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

    }

    @Override
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
            Sensor sensor1 = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor sensor2 = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            mIsSupport = (sensor1 != null || sensor2 != null);
        }
        return mIsSupport;
    }

    private void registerSensor(Context context){
        if (mRegistered) return;
        SensorManager mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor1 = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor sensor2 = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (sensor1 == null && sensor2 == null){
            Log.e(TAG,"TYPE_ACCELEROMETER TYPE_GYROSCOPE sensor not support!");
            return;
        }

        if (mDeviceSensorLooper == null){
            mDeviceSensorLooper = new DeviceSensorLooper(mSensorManager, SensorManager.SENSOR_DELAY_GAME);
        }

        if (headTracker == null){
            Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            headTracker = new HeadTracker(mDeviceSensorLooper, new SystemClock(), display);
        }

        // start the tracker
        mDeviceSensorLooper.registerListener(this);
        headTracker.startTracking();

        mRegistered = true;
    }

    private void unregisterSensor(Context context){
        if (!mRegistered) return;

        // stop the tracker
        mDeviceSensorLooper.unregisterListener(this);
        headTracker.stopTracking();

        mRegistered = false;
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {


        if (isOn && event.accuracy != 0){
            synchronized (matrixLock){
                Matrix.setIdentityM(mTmpMatrix, 0);
                headTracker.getLastHeadView(mTmpMatrix, 0);
                if(nativeUtils2==null)
                    nativeUtils2=VrNativeUtils.getInstance();
                nativeUtils2.sphere3dSetSensor(mTmpMatrix);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        synchronized (matrixLock){
            Matrix.setIdentityM(mTmpMatrix, 0);
            headTracker.getLastHeadView(mTmpMatrix, 0);
            if(nativeUtils2==null)
                nativeUtils2=VrNativeUtils.getInstance();
            nativeUtils2.sphere3dSetSensor(mTmpMatrix);
        }


    }


}
