package com.Fliter;

import android.content.res.Configuration;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.asha.vrlib.R;


/**
 * @author shenjb@china
 * @since 2019/4/12
 */
public class FliterCameraActivity extends AppCompatActivity implements View.OnClickListener {

    private GLSurfaceView glSurfaceView;
    private FrameLayout mPreviewFrameLayout;
    private CameraDisplayDoubleInput mCameraDisplay;
    int mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fliter_camera);
        glSurfaceView = (GLSurfaceView)findViewById(R.id.cameraview);
        findViewById(R.id.btn0).setOnClickListener(this);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        findViewById(R.id.btn5).setOnClickListener(this);
        findViewById(R.id.btn6).setOnClickListener(this);
        mPreviewFrameLayout = (FrameLayout)findViewById(R.id.id_preview_layout);
        mCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
        mCameraDisplay = new CameraDisplayDoubleInput(this, mDoubleInputChangePreviewSizeListener, glSurfaceView, mCameraID);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraDisplay.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraDisplay != null) {
            mCameraDisplay.onPause();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }


    public void setType(int pos){

    }


    private CameraDisplayDoubleInput.ChangePreviewSizeListener mDoubleInputChangePreviewSizeListener = new CameraDisplayDoubleInput.ChangePreviewSizeListener() {
        @Override
        public void onChangePreviewSize(final int previewW, final int previewH) {
            mPreviewFrameLayout.requestLayout();
        }
    };


    public void switchCarmer() {

        if (mCameraDisplay != null) {
            mCameraDisplay.switchCamera();
        }
    }

    @Override
    public void onClick(View view) {

        int i = view.getId();
        if (i == R.id.btn0) {
            setType(0);

        } else if (i == R.id.btn1) {
            setType(1);

        } else if (i == R.id.btn2) {
            setType(2);

        } else if (i == R.id.btn3) {
            setType(3);

        } else if (i == R.id.btn4) {
            setType(4);

        } else if (i == R.id.btn5) {
            setType(5);

        } else if (i == R.id.btn6) {
            switchCarmer();

        }
    }
}
