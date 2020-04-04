package com.tencent.liteav.liveroom.ui.anchor;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.opengl.EGLContext;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.Fliter.CameraDisplayDoubleInput;
import com.tencent.liteav.liveroom.R;
import com.tencent.liteav.liveroom.model.TRTCLiveRoomCallback;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * Module:   TCBaseAnchorActivity
 * <p>
 * Function: 主播推流的页面
 * <p>
 */
public class TRTCCameraActivity extends TCBaseAnchorActivity {
    private static final String TAG = TRTCCameraActivity.class.getSimpleName();
    private GLSurfaceView surfaceView;      // 主播本地预览的 View
    private CameraDisplayDoubleInput mCameraDisplay;
    private FrameLayout mPreviewFrameLayout;
    int mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.BeautyTheme);
        super.onCreate(savedInstanceState);

    }

    @Override
    public int getLayoutId() {
        return R.layout.liveroom_activity_trtc;
    }

    boolean isStart;
    private CameraDisplayDoubleInput.ChangePreviewSizeListener
            mDInputistener = new CameraDisplayDoubleInput.ChangePreviewSizeListener() {
        @Override
        public void onChangePreviewSize(int previewW, int previewH) {
            mPreviewFrameLayout.requestLayout();
        }

        @Override
        public int sendCustomVideoData(EGLContext context, int textureId, int textureWidth, int textureHeight) {

            if (isStart){
//                Log.e("========","=====sendCustomVideoData======"+textureId);
                mLiveRoom.sendCustomVideoData(context,textureId,textureWidth,textureHeight);
            }
            return 0;
        }
    };

    @Override
    protected void initView() {
        super.initView();
        surfaceView = (GLSurfaceView) findViewById(R.id.anchor_video_view);
        mPreviewFrameLayout = (FrameLayout)findViewById(com.asha.vrlib.R.id.id_preview_layout);
        mPreviewFrameLayout.setVisibility(View.VISIBLE);
        mCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
        mCameraDisplay = new CameraDisplayDoubleInput(this, mDInputistener, surfaceView, mCameraID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMainHandler != null) {
            mMainHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * /////////////////////////////////////////////////////////////////////////////////
     * //
     * //                      开始和停止推流相关
     * //
     * /////////////////////////////////////////////////////////////////////////////////
     */
    @Override
    protected void enterRoom() {

        mCameraDisplay.onResume();
        super.enterRoom();
    }

    @Override
    protected void exitRoom() {
        super.exitRoom();
    }

    @Override
    protected void onCreateRoomSuccess() {
        // 创建房间成功，开始推流
        mLiveRoom.startPublish(mSelfUserId + "_stream", new TRTCLiveRoomCallback.ActionCallback() {
            @Override
            public void onCallback(int code, String msg) {
                if (!isStart)isStart=true;
                if (code == 0) {
                    Log.d(TAG, "开播成功");
                } else {
                    Log.e(TAG, "开播失败" + msg);
                }
            }
        });
    }

    @Override
    public void onAnchorExit(String userId) {
        mLiveRoom.stopPlay(userId, null);

    }

    /**
     * /////////////////////////////////////////////////////////////////////////////////
     * //
     * //                      点击事件与调用函数相关
     * //
     * /////////////////////////////////////////////////////////////////////////////////
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.switch_cam) {
            if (mLiveRoom != null) {
                mLiveRoom.switchCamera();
            }
        }  else if (id == R.id.btn_close) {
            showExitInfoDialog("当前正在直播，是否退出直播？", false);
        } else {
            super.onClick(v);
        }
    }

    /**
     * /////////////////////////////////////////////////////////////////////////////////
     * //
     * //                      权限相关
     * //
     * /////////////////////////////////////////////////////////////////////////////////
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                for (int ret : grantResults) {
                    if (ret != PackageManager.PERMISSION_GRANTED) {
                        showErrorAndQuit(-1314, "获取权限失败");
                        return;
                    }
                }
                this.enterRoom();
                break;
            default:
                break;
        }
    }
}
