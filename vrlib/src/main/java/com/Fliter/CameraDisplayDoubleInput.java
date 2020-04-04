package com.Fliter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.Fliter.camera.CameraImpl;
import com.Fliter.camera.CameraImplProxy;
import com.Fliter.utils.GlUtil;
import com.Fliter.utils.OpenGLUtils;
import com.Fliter.utils.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;




/**
 * CameraDisplayDoubleInput is used for camera preview
 */

/**
 * 渲染结果显示的Render, 用户最终看到的结果在这个类中得到并显示.
 * 请重点关注Camera.PreviewCallback的onPreviewFrame/onSurfaceCreated,onSurfaceChanged,onDrawFrame
 * 四个接口, 基本上所有的处理逻辑都是围绕这四个接口展开
 */
public class CameraDisplayDoubleInput implements Renderer {

    private String TAG = "CameraDisplayDoubleInput";
    private boolean DEBUG = false;

    /**
     * SurfaceTexure texture id
     */
    protected int mTextureId = OpenGLUtils.NO_TEXTURE;

    private int mImageWidth;
    private int mImageHeight;
    private GLSurfaceView mGlSurfaceView;
    private ChangePreviewSizeListener mListener;
    private AutoFocusListener mAutoFocusListener;
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    private Context mContext;

    public CameraImplProxy mCameraProxy;
    private SurfaceTexture mSurfaceTexture;


    private int mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private STGLRender mGLRender;



    private int[] mBeautifyTextureId;
    private int[] mTextureOutId;
    private int[] mFilterTextureOutId;
    private boolean mCameraChanging = false;
    private boolean mIsChangingPreviewSize = false;


    private boolean mNeedFaceAttribute = false;
    private boolean mNeedObject = false;



    private Handler mHandler;

    private boolean mIsPaused = false;

    private Object mHumanActionHandleLock = new Object();
    private Object mImageDataLock = new Object();

    private int mScreenIndexRectWidth = 0;

    private Rect mIndexRect = new Rect();

    private byte[] mImageData;


    private int[] mVideoEncoderTexture;

    private long mHandAction = 0;
    private long mBodyAction = 0;


    //滤镜
    private boolean isMagicGrpFilter = false;


    private int magicFilterType = 0;
    private FloatBuffer mTextureBuffer;
    private FloatBuffer gLCubeBuffer;

    public boolean isRecordStatus() {
        return isRecordStatus;
    }

    public void setRecordStatus(boolean recordStatus) {
        isRecordStatus = recordStatus;
    }

    private boolean isRecordStatus = false;//是否录制状态






    public interface ChangePreviewSizeListener {
        void onChangePreviewSize(int previewW, int previewH);
    }

    public interface AutoFocusListener {
        void setOnAutoFocus();
    }

    Activity mActivity;

    public CameraDisplayDoubleInput(Context context, ChangePreviewSizeListener listener, GLSurfaceView glSurfaceView, int cameraType) {
        this.mActivity = (Activity) context;
        if (cameraType == 0) {
            mCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
        } else if (cameraType == 1) {
            mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        mCameraProxy = new CameraImplProxy(context);
        mGlSurfaceView = glSurfaceView;
        mListener = listener;
        mContext = context;
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(this);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        mTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);
        gLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CORP.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        gLCubeBuffer.put(TextureRotationUtil.CORP).position(0);
        mGLRender = new STGLRender();


    }

    public boolean getFaceAttribute() {
        return mNeedFaceAttribute;
    }



    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    /**
     * 工作在opengl线程, 当前Renderer关联的view创建的时候调用
     *
     * @param gl
     * @param config
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if (mIsPaused == true) {
            return;
        }
        GLES20.glEnable(GL10.GL_DITHER);
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glEnable(GL10.GL_DEPTH_TEST);
        Log.e(TAG, "camera2--> onSurfaceCreated ");

    }







    /**
     * 工作在opengl线程, 当前Renderer关联的view尺寸改变的时候调用
     *
     * @param gl
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (mIsPaused == true) {
            return;
        }
        adjustViewPort(width, height);
        mGLRender.init(mImageWidth, mImageHeight);

    }

    /**
     * 根据显示区域大小调整一些参数信息
     *
     * @param width
     * @param height
     */
    private void adjustViewPort(int width, int height) {
        mSurfaceHeight = height;
        mSurfaceWidth = width;

        mCameraProxy.onSurfaceChanged();
        GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);
        Log.e("====","===mSurfaceWidth=="+mSurfaceWidth+"===mSurfaceHeight=="+mSurfaceHeight+"===mImageWidth=="+mImageWidth+"===mImageHeight=="+mImageHeight);
        mGLRender.calculateVertexBuffer(mSurfaceWidth, mSurfaceHeight, mImageWidth, mImageHeight);

    }

    private CameraImpl.Callback mCmeraCallback = new CameraImpl.Callback() {
        @Override
        public void onCameraOpened() {
        }

        @Override
        public void onCameraClosed() {

        }

        @Override
        public void onPreviewFrame(byte[] data) {

           // Log.e("====", "=========onPreviewFrame======");
            if (mCameraChanging || mCameraProxy.getCamera() == null) {
                return;
            }
            //TODO *3/2 的机制待去了解，不能删除
            if (mImageData == null || mImageData.length != mImageHeight * mImageWidth * 3 / 2) {
                mImageData = new byte[mImageWidth * mImageHeight * 3 / 2];
            }

            synchronized (mImageDataLock) {
//                Log.e(TAG, "mImageData --> " + mImageData.length);
//                Log.e(TAG, "data.length --> " + data.length);
                System.arraycopy(data, 0, mImageData, 0, data.length);
            }



            mGlSurfaceView.requestRender();
        }
    };

    public EGLContext currentContext = null;

    /**
     * 工作在opengl线程, 具体渲染的工作函数
     *
     * @param gl
     */
    @Override
    public void onDrawFrame(GL10 gl) {

        if (currentContext == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                currentContext = EGL14.eglGetCurrentContext();
            }
        }
        // during switch camera
        if (mCameraChanging) {
            return;
        }

        if (mCameraProxy.getCamera() == null) {
            return;
        }


        if (mBeautifyTextureId == null) {
            mBeautifyTextureId = new int[1];
            GlUtil.initEffectTexture(mImageWidth, mImageHeight, mBeautifyTextureId, GLES20.GL_TEXTURE_2D);
        }

        if (mTextureOutId == null) {
            mTextureOutId = new int[1];
            GlUtil.initEffectTexture(mImageWidth, mImageHeight, mTextureOutId, GLES20.GL_TEXTURE_2D);
        }

        if (mVideoEncoderTexture == null) {
            mVideoEncoderTexture = new int[1];
        }

        if (mSurfaceTexture != null) {
            mSurfaceTexture.updateTexImage();
        } else {
            return;
        }
        //清屏
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        int textureId = mGLRender.preProcess(mTextureId, null);//摄像头转向，输出FBO的id
        GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);
        mGLRender.onDrawFrame(textureId);

    }

    private void savePicture(int textureId) {
        ByteBuffer mTmpBuffer = ByteBuffer.allocate(mImageHeight * mImageWidth * 4);
        mGLRender.saveTextureToFrameBuffer(textureId, mTmpBuffer);

        mTmpBuffer.position(0);
        Message msg = Message.obtain(mHandler);
        msg.what = PatRecordConstant.PatRecordParamsType.MSG_SAVING_IMG;
        msg.obj = mTmpBuffer;
        Bundle bundle = new Bundle();
        bundle.putInt("imageWidth", mImageWidth);
        bundle.putInt("imageHeight", mImageHeight);
        msg.setData(bundle);
        msg.sendToTarget();
    }

    private void saveImageBuffer2Picture(byte[] imageBuffer) {
        ByteBuffer mTmpBuffer = ByteBuffer.allocate(mImageHeight * mImageWidth * 4);
        mTmpBuffer.put(imageBuffer);

        Message msg = Message.obtain(mHandler);
        msg.what = PatRecordConstant.PatRecordParamsType.MSG_SAVING_IMG;
        msg.obj = mTmpBuffer;
        Bundle bundle = new Bundle();
        bundle.putInt("imageWidth", mImageWidth);
        bundle.putInt("imageHeight", mImageHeight);
        msg.setData(bundle);
        msg.sendToTarget();
    }



    /**
     * camera设备startPreview
     */
    private void setUpCamera() {
        // 初始化Camera设备预览需要的显示区域(mSurfaceTexture)
        if (mTextureId == OpenGLUtils.NO_TEXTURE) {
            mTextureId = OpenGLUtils.getExternalOESTextureID();
            mSurfaceTexture = new SurfaceTexture(mTextureId);
//            mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
//                @Override
//                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
//                    mGlSurfaceView.requestRender();
//                }
//            });
        }
        // TODO: 2018/8/14 以下四条代码注释lyg
//        String size = mSupportedPreviewSizes.get(mCurrentPreview);
//        int index = size.indexOf('x');
//        mImageHeight = Integer.parseInt(size.substring(0, index));
//        mImageWidth = Integer.parseInt(size.substring(index + 1));

        mImageHeight = PatRecordConstant.PatRecordParamsType.HEIGHT;
        mImageWidth = PatRecordConstant.PatRecordParamsType.WIDTH;
        if (mIsPaused)
            return;

//        while (!mSetPreViewSizeSucceed) {
//            try {
//                mCameraProxy.setPreviewSize(mImageWidth, mImageHeight);
//                mSetPreViewSizeSucceed = true;
//            } catch (Exception e) {
//                mSetPreViewSizeSucceed = false;
//            }
//
//            try {
//                Thread.sleep(10);
//            } catch (Exception e) {
//
//            }
//        }
        mCameraProxy.setPreviewSize(mImageWidth, mImageHeight);

        if (mIsPaused)
            return;
        if (mSurfaceTexture != null) {//打开摄像机
            mCameraProxy.startPreview(mSurfaceTexture, mCmeraCallback);
        }

        boolean flipHorizontal = mCameraProxy.isFlipHorizontal();
        Log.e("000","======"+mCameraProxy.getOrientation());
        mGLRender.adjustTextureBuffer(mCameraProxy.getOrientation(), flipHorizontal);
    }






    public void onResume() {
//        Log.e(TAG,"onResume-->");
        mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraProxy.openCamera(mCameraID);
                if (mCameraProxy.getCamera() != null) {
                    setUpCamera();
                }
            }
        });

        mIsPaused = false;


//        mGLRender = new STGLRender();
//
//        mGlSurfaceView.onResume();
//        mGlSurfaceView.forceLayout();
//        mGlSurfaceView.requestRender();
    }

    public void onPause() {
        // TODO: 2018/12/11 不要放在 mGlSurfaceView.queueEvent线程里面，否则相机无法实时关闭。
        //mCurrentSticker = null;
        mIsPaused = true;
        mImageData = null;
        mCameraProxy.releaseCamera();


//        mGlSurfaceView.queueEvent(new Runnable() {
//            @Override
//            public void run() {
//                mSTHumanActionNative.reset();
//
//                mStBeautifyNative.destroyBeautify();
//                mStStickerNative.removeAvatarModel();
//                mStStickerNative.destroyInstance();
//                mSTMobileStreamFilterNative.destroyInstance();
//                mRGBABuffer = null;
//                mNv21ImageData = null;
//                deleteTextures();
//                if (mSurfaceTexture != null) {
//                    mSurfaceTexture.release();
//                }
//                mGLRender.destroyFrameBuffers();
//            }
//        });

//        mGlSurfaceView.onPause();
    }

    /**
     * Desc:这里负责销毁录制页面资源
     * <p>
     * Author: [lianyg]
     * Date: 2018-08-22
     */
    public void onDestroy() {
        //必须释放非opengGL句柄资源,负责内存泄漏
        synchronized (mHumanActionHandleLock) {
            mGlSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
//                    mSTHumanActionNative.reset(); 这里先注释。具体看reset注释，当前业务不需要这个


                    deleteTextures();
                    if (mSurfaceTexture != null) {
                        mSurfaceTexture.release();
                    }
                    mGLRender.destroyFrameBuffers();
                }
            });
            mCameraProxy.releaseCamera();

        }

    }


    /**
     * 释放纹理资源
     */
    protected void deleteTextures() {
        deleteCameraPreviewTexture();
        deleteInternalTextures();
    }

    // must in opengl thread
    private void deleteCameraPreviewTexture() {
        if (mTextureId != OpenGLUtils.NO_TEXTURE) {
            GLES20.glDeleteTextures(1, new int[]{
                    mTextureId
            }, 0);
        }
        mTextureId = OpenGLUtils.NO_TEXTURE;
    }

    private void deleteInternalTextures() {
        if (mBeautifyTextureId != null) {
            GLES20.glDeleteTextures(1, mBeautifyTextureId, 0);
            mBeautifyTextureId = null;
        }

        if (mTextureOutId != null) {
            GLES20.glDeleteTextures(1, mTextureOutId, 0);
            mTextureOutId = null;
        }

        if (mFilterTextureOutId != null) {
            GLES20.glDeleteTextures(1, mFilterTextureOutId, 0);
            mFilterTextureOutId = null;
        }

        if (mVideoEncoderTexture != null) {
            GLES20.glDeleteTextures(1, mVideoEncoderTexture, 0);
            mVideoEncoderTexture = null;
        }
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        if (Camera.getNumberOfCameras() == 1
                || mCameraChanging) {
            return;
        }

        // TODO 新风格滤镜目前切换摄像去需要销毁重建
        boolean isNeedReInitMagicGrpFliter = true;
//        isMagicGrpFilter = false;

        final int cameraID = 1 - mCameraID;
        mCameraChanging = true;
        mCameraProxy.openCamera(cameraID);

//        if (mCameraProxy.cameraOpenFailed()) {
//            return;
//        }


        if (mNeedObject) {
            resetIndexRect();
        } else {
            if (mHandler != null) {
                Message msg = mHandler.obtainMessage(PatRecordConstant.PatRecordParamsType.MSG_CLEAR_OBJECT);
                mHandler.sendMessage(msg);
            }
        }

        final boolean isFinalNeedReInitMagicGrpFliter = isNeedReInitMagicGrpFliter;
        Log.e(TAG, "switchCamera-->");
        mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                deleteTextures();
                if (mCameraProxy.getCamera() != null) {
                    setUpCamera();
                }
                mCameraChanging = false;
                mCameraID = cameraID;
            }
        });
        //fix 双输入camera changing时，贴纸和画点mirrow显示
        //mGlSurfaceView.requestRender();

    }

    public int getCameraID() {
        return mCameraID;
    }

    public void changePreviewSize(int currentPreview) {
        if (mCameraProxy.getCamera() == null || mCameraChanging
                || mIsPaused) {
            return;
        }

        mIsChangingPreviewSize = true;

        mCameraChanging = true;
        mCameraProxy.releaseCamera();
        mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {


                deleteTextures();
                if (mCameraProxy.getCamera() != null) {
                    setUpCamera();
                }

                mGLRender.init(mImageWidth, mImageHeight);
                if (DEBUG) {
                    mGLRender.initDrawPoints();
                }

                if (mNeedObject) {
                    resetIndexRect();
                }

                mGLRender.calculateVertexBuffer(mSurfaceWidth, mSurfaceHeight, mImageWidth, mImageHeight);
                if (mListener != null) {
                    mListener.onChangePreviewSize(mImageHeight, mImageWidth);
                }

                mCameraChanging = false;
                mIsChangingPreviewSize = false;
                //mGlSurfaceView.requestRender();
            }
        });


    }

    public void enableObject(boolean enabled) {
        mNeedObject = enabled;

        if (mNeedObject) {
            resetIndexRect();
        }
    }

    public void setIndexRect(int x, int y, boolean needRect) {
        mIndexRect = new Rect(x, y, x + mScreenIndexRectWidth, y + mScreenIndexRectWidth);

    }

    public Rect getIndexRect() {
        return mIndexRect;
    }






    public void resetIndexRect() {
        if (mImageWidth == 0) {
            return;
        }

        mScreenIndexRectWidth = mSurfaceWidth / 4;

        mIndexRect.left = (mSurfaceWidth - mScreenIndexRectWidth) / 2;
        mIndexRect.top = (mSurfaceHeight - mScreenIndexRectWidth) / 2;
        mIndexRect.right = mIndexRect.left + mScreenIndexRectWidth;
        mIndexRect.bottom = mIndexRect.top + mScreenIndexRectWidth;


    }

    /**
     * 用于humanActionDetect接口。根据传感器方向计算出在不同设备朝向时，人脸在buffer中的朝向
     *
     * @return 人脸在buffer中的朝向
     */

    public int getPreviewWidth() {
        return mImageWidth;
    }

    public int getPreviewHeight() {
        return mImageHeight;
    }



    private void processStMatrix(float[] matrix, boolean needMirror, boolean needFlip) {
        if (needMirror && matrix != null && matrix.length == 16) {
            for (int i = 0; i < 3; i++) {
                matrix[4 * i] = -matrix[4 * i];
            }

            if (matrix[4 * 3] == 0) {
                matrix[4 * 3] = 1.0f;
            } else if (matrix[4 * 3] == 1.0f) {
                matrix[4 * 3] = 0f;
            }
        }

        if (needFlip && matrix != null && matrix.length == 16) {
            matrix[0] = 1.0f;
            matrix[5] = -1.0f;
            matrix[12] = 0f;
            matrix[13] = 1.0f;
        }

        return;
    }


    public boolean isChangingPreviewSize() {
        return mIsChangingPreviewSize;
    }



    public long getHandActionInfo() {
        return mHandAction;
    }

    public long getBodyActionInfo() {
        return mBodyAction;
    }



    public interface OnTextureListener {

        int onTextureAvailable(int textureId, int textureWidth, int textureHeight, long timestampNanos);
    }

    private OnTextureListener onTextureListener;

    public void setTexutreListener(OnTextureListener listener) {
        onTextureListener = listener;
    }

    /**
     * 打开闪光灯
     *
     * @return
     */
    public void openLight() {
        if (mCameraProxy != null) {
            mCameraProxy.setFlash(CameraImplProxy.FLASH_TORCH);
        }
    }

    /**
     * Desc:获取闪光灯状态
     * <p>
     * Author: [lianyg]
     * Date: 2018-08-11
     *
     * @return string
     */
    public int getCameraLightStatus() {
        return mCameraProxy.getFlash();
    }

//    public Camera getCamera() {
//        return mCameraProxy.getCamera();
//    }

    /**
     * 关闭闪光灯
     *
     * @return
     */
    public void closeLight() {
        if (mCameraProxy != null && mCameraProxy.getCamera() != null) {
            mCameraProxy.setFlash(CameraImplProxy.FLASH_OFF);
        }
    }

    private OnFocusListener focusListener;

    public interface OnFocusListener {
        void onFocusSucess();

        void onFocusFail();
    }

    public void setFocus(float x, float y, OnFocusListener listener) {
        focusOnTouch(x, y, mSurfaceWidth, mSurfaceHeight, listener);
    }

    public void focusOnTouch(float x, float y, int width, int height, final OnFocusListener listener) {
        if (mCameraProxy != null) {
            Point point = new Point((int) x, (int) y);
            mCameraProxy.onFocus(point, listener);
        }
    }




}
