package com.tencent.liteav.trtcvideocalldemo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.squareup.picasso.Picasso;
import com.tencent.liteav.login.ProfileManager;
import com.tencent.liteav.login.UserModel;
import com.tencent.liteav.trtcvideocalldemo.R;
import com.tencent.liteav.trtcvideocalldemo.model.ITRTCVideoCall;
import com.tencent.liteav.trtcvideocalldemo.model.TRTCVideoCallImpl;
import com.tencent.liteav.trtcvideocalldemo.model.TRTCVideoCallListener;
import com.tencent.liteav.trtcvideocalldemo.ui.videolayout.TRTCVideoLayout;
import com.tencent.liteav.trtcvideocalldemo.ui.videolayout.TRTCVideoLayoutManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于展示视频通话的主界面，通话的接听和拒绝就是在这个界面中完成的。
 *
 * @author guanyifeng
 */
public class TRTCVideoCallActivity extends AppCompatActivity {
    public static final int TYPE_BEING_CALLED = 1;
    public static final int TYPE_CALL         = 2;

    public static final  String PARAM_TYPE                = "type";
    public static final  String PARAM_USER                = "user_model";
    public static final  String PARAM_BEINGCALL_USER      = "beingcall_user_model";
    public static final  String PARAM_OTHER_INVITING_USER = "other_inviting_user_model";
    private static final int    MAX_SHOW_INVITING_USER    = 4;

    /**
     * 界面元素相关
     */
    private ImageView              mMuteImg;
    private LinearLayout           mMuteLl;
    private ImageView              mHangupImg;
    private LinearLayout           mHangupLl;
    private ImageView              mHandsfreeImg;
    private LinearLayout           mHandsfreeLl;
    private ImageView              mDialingImg;
    private LinearLayout           mDialingLl;
    private TRTCVideoLayoutManager mLayoutManagerTrtc;
    private Group                  mInvitingGroup;
    private LinearLayout           mImgContainerLl;
    private TextView               mTimeTv;
    private Runnable               mTimeRunnable;
    private int                    mTimeCount;
    private Handler                mTimeHandler;
    private HandlerThread          mTimeHandlerThread;

    /**
     * 拨号相关成员变量
     */
    private UserModel              mSelfModel;
    private List<UserModel>        mCallUserModelList = new ArrayList<>(); // 呼叫方
    private Map<String, UserModel> mCallUserModelMap  = new HashMap<>();
    private UserModel              mSponsorUserModel; // 被叫方
    private List<UserModel>        mOtherInvitingUserModelList;
    private int                    mCallType;
    private ITRTCVideoCall         mITRTCVideoCall;
    private boolean                isHandsFree        = true;
    private boolean                isMuteMic          = false;

    /**
     * 拨号的回调
     */
    private TRTCVideoCallListener mTRTCVideoCallListener = new TRTCVideoCallListener() {
        @Override
        public void onError(int code, String msg) {
            //发生了错误，报错并退出该页面
            ToastUtils.showLong("发送错误[" + code + "]:" + msg);
            finish();
        }

        @Override
        public void onInvited(String sponsor, List<String> userIdList, boolean isFromGroup, int callType) {
        }

        @Override
        public void onGroupCallInviteeListUpdate(List<String> userIdList) {
        }

        @Override
        public void onUserEnter(final String userId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showCallingView();
                    //1.先造一个虚拟的用户添加到屏幕上
                    UserModel model = new UserModel();
                    model.userId = userId;
                    model.phone = "";
                    model.userName = userId;
                    model.userAvatar = "";
                    mCallUserModelList.add(model);
                    mCallUserModelMap.put(model.userId, model);
                    TRTCVideoLayout videoLayout = addUserToManager(model);
                    if (videoLayout == null) {
                        return;
                    }
                    videoLayout.setVideoAvailable(false);
                    //2. 再获取用户资料，如果搜索到了该用户，更新用户的信息
                    ProfileManager.getInstance().getUserInfoByUserId(userId, new ProfileManager.GetUserInfoCallback() {
                        @Override
                        public void onSuccess(UserModel model) {
                            UserModel oldModel = mCallUserModelMap.get(model.userId);
                            if (oldModel != null) {
                                oldModel.userName = model.userName;
                                oldModel.userAvatar = model.userAvatar;
                                oldModel.phone = model.phone;
                                TRTCVideoLayout videoLayout = mLayoutManagerTrtc.findCloudViewView(model.userId);
                                if (videoLayout != null) {
                                    Picasso.get().load(oldModel.userAvatar).into(videoLayout.getHeadImg());
                                    videoLayout.getUserNameTv().setText(oldModel.userName);
                                }
                            }
                        }

                        @Override
                        public void onFailed(int code, String msg) {
                            ToastUtils.showLong("获取用户" + userId + "的资料失败");
                        }
                    });
                }
            });
        }

        @Override
        public void onUserLeave(final String userId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //1. 回收界面元素
                    mLayoutManagerTrtc.recyclerCloudViewView(userId);
                    //2. 删除用户model
                    UserModel userModel = mCallUserModelMap.remove(userId);
                    if (userModel != null) {
                        mCallUserModelList.remove(userModel);
                    }
                }
            });
        }

        @Override
        public void onReject(final String userId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCallUserModelMap.containsKey(userId)) {
                        // 进入拒绝环节
                        //1. 回收界面元素
                        mLayoutManagerTrtc.recyclerCloudViewView(userId);
                        //2. 删除用户model
                        UserModel userModel = mCallUserModelMap.remove(userId);
                        if (userModel != null) {
                            mCallUserModelList.remove(userModel);
                            ToastUtils.showLong(userModel.userName + "拒绝通话");
                        }
                    }
                }
            });
        }

        @Override
        public void onNoResp(final String userId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCallUserModelMap.containsKey(userId)) {
                        // 进入无响应环节
                        //1. 回收界面元素
                        mLayoutManagerTrtc.recyclerCloudViewView(userId);
                        //2. 删除用户model
                        UserModel userModel = mCallUserModelMap.remove(userId);
                        if (userModel != null) {
                            mCallUserModelList.remove(userModel);
                            ToastUtils.showLong(userModel.userName + "无响应");
                        }
                    }
                }
            });
        }

        @Override
        public void onLineBusy(String userId) {
            if (mCallUserModelMap.containsKey(userId)) {
                // 进入无响应环节
                //1. 回收界面元素
                mLayoutManagerTrtc.recyclerCloudViewView(userId);
                //2. 删除用户model
                UserModel userModel = mCallUserModelMap.remove(userId);
                if (userModel != null) {
                    mCallUserModelList.remove(userModel);
                    ToastUtils.showLong(userModel.userName + "忙线");
                }
            }
        }

        @Override
        public void onCallingCancel() {
            if (mSponsorUserModel != null) {
                ToastUtils.showLong(mSponsorUserModel.userName + " 取消了通话");
            }
            finish();
        }

        @Override
        public void onCallingTimeout() {
            if (mSponsorUserModel != null) {
                ToastUtils.showLong(mSponsorUserModel.userName + " 通话超时");
            }
            finish();
        }

        @Override
        public void onCallEnd() {
            finish();
        }

        @Override
        public void onUserVideoAvailable(final String userId, final boolean isVideoAvailable) {
            //有用户的视频开启了
            TRTCVideoLayout layout = mLayoutManagerTrtc.findCloudViewView(userId);
            if (layout != null) {
                layout.setVideoAvailable(isVideoAvailable);
                if (isVideoAvailable) {
                    mITRTCVideoCall.startRemoteView(userId, layout.getVideoView());
                } else {
                    mITRTCVideoCall.stopRemoteView(userId);
                }
            } else {

            }
        }

        @Override
        public void onUserAudioAvailable(String userId, boolean isVideoAvailable) {

        }

        @Override
        public void onUserVoiceVolume(Map<String, Integer> volumeMap) {
            for (Map.Entry<String, Integer> entry : volumeMap.entrySet()) {
                String          userId = entry.getKey();
                TRTCVideoLayout layout = mLayoutManagerTrtc.findCloudViewView(userId);
                if (layout != null) {
                    layout.setAudioVolumeProgress(entry.getValue());
                }
            }
        }
    };
    private ImageView             mSponsorAvatarImg;
    private TextView              mSponsorUserNameTv;
    private Group                 mSponsorGroup;

    /**
     * 主动拨打给某个用户
     *
     * @param context
     * @param models
     */
    public static void startCallSomeone(Context context, List<UserModel> models) {
        Intent starter = new Intent(context, TRTCVideoCallActivity.class);
        starter.putExtra(PARAM_TYPE, TYPE_CALL);
        starter.putExtra(PARAM_USER, new IntentParams(models));
        context.startActivity(starter);
    }

    /**
     * 作为用户被叫
     *
     * @param context
     * @param beingCallUserModel
     */
    public static void startBeingCall(Context context, UserModel beingCallUserModel, List<UserModel> otherInvitingUserModel) {
        Intent starter = new Intent(context, TRTCVideoCallActivity.class);
        starter.putExtra(PARAM_TYPE, TYPE_BEING_CALLED);
        starter.putExtra(PARAM_BEINGCALL_USER, beingCallUserModel);
        starter.putExtra(PARAM_OTHER_INVITING_USER, new IntentParams(otherInvitingUserModel));
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 应用运行时，保持不锁屏、全屏化
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.videocall_activity_online_call);

        initView();
        initData();
        initListener();
    }

    @Override
    public void onBackPressed() {
        mITRTCVideoCall.hangup();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mITRTCVideoCall.closeCamera();
        mITRTCVideoCall.removeListener(mTRTCVideoCallListener);
        stopTimeCount();
        mTimeHandlerThread.quit();
    }

    private void initListener() {
        mMuteLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMuteMic = !isMuteMic;
                mITRTCVideoCall.setMicMute(isMuteMic);
                mMuteImg.setActivated(isMuteMic);
                ToastUtils.showLong(isMuteMic ? "开启静音" : "关闭静音");
            }
        });
        mHandsfreeLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHandsFree = !isHandsFree;
                mITRTCVideoCall.setHandsFree(isHandsFree);
                mHandsfreeImg.setActivated(isHandsFree);
                ToastUtils.showLong(isHandsFree ? "使用扬声器" : "使用听筒");
            }
        });
        mMuteImg.setActivated(isMuteMic);
        mHandsfreeImg.setActivated(isHandsFree);
    }

    private void initData() {
        // 初始化成员变量
        mITRTCVideoCall = TRTCVideoCallImpl.sharedInstance(this);
        mITRTCVideoCall.addListener(mTRTCVideoCallListener);
        mTimeHandlerThread = new HandlerThread("time-count-thread");
        mTimeHandlerThread.start();
        mTimeHandler = new Handler(mTimeHandlerThread.getLooper());
        // 初始化从外界获取的数据
        Intent intent = getIntent();
        //自己的资料
        mSelfModel = ProfileManager.getInstance().getUserModel();
        mCallType = intent.getIntExtra(PARAM_TYPE, TYPE_BEING_CALLED);
        if (mCallType == TYPE_BEING_CALLED) {
            // 作为被叫
            mSponsorUserModel = (UserModel) intent.getSerializableExtra(PARAM_BEINGCALL_USER);
            IntentParams params = (IntentParams) intent.getSerializableExtra(PARAM_OTHER_INVITING_USER);
            if (params != null) {
                mOtherInvitingUserModelList = params.mUserModels;
            }
            showWaitingResponseView();
        } else {
            // 主叫方
            IntentParams params = (IntentParams) intent.getSerializableExtra(PARAM_USER);
            if (params != null) {
                mCallUserModelList = params.mUserModels;
                for (UserModel userModel : mCallUserModelList) {
                    mCallUserModelMap.put(userModel.userId, userModel);
                }
                startInviting();
                showInvitingView();
            }
        }

    }

    private void startInviting() {
        List<String> list = new ArrayList<>();
        for (UserModel userModel : mCallUserModelList) {
            list.add(userModel.userId);
        }
        mITRTCVideoCall.groupCall(list, ITRTCVideoCall.TYPE_VIDEO_CALL, "");
    }

    private void initView() {
        mMuteImg = (ImageView) findViewById(R.id.img_mute);
        mMuteLl = (LinearLayout) findViewById(R.id.ll_mute);
        mHangupImg = (ImageView) findViewById(R.id.img_hangup);
        mHangupLl = (LinearLayout) findViewById(R.id.ll_hangup);
        mHandsfreeImg = (ImageView) findViewById(R.id.img_handsfree);
        mHandsfreeLl = (LinearLayout) findViewById(R.id.ll_handsfree);
        mDialingImg = (ImageView) findViewById(R.id.img_dialing);
        mDialingLl = (LinearLayout) findViewById(R.id.ll_dialing);
        mLayoutManagerTrtc = (TRTCVideoLayoutManager) findViewById(R.id.trtc_layout_manager);
        mInvitingGroup = (Group) findViewById(R.id.group_inviting);
        mImgContainerLl = (LinearLayout) findViewById(R.id.ll_img_container);
        mTimeTv = (TextView) findViewById(R.id.tv_time);
        mSponsorAvatarImg = (ImageView) findViewById(R.id.img_sponsor_avatar);
        mSponsorUserNameTv = (TextView) findViewById(R.id.tv_sponsor_user_name);
        mSponsorGroup = (Group) findViewById(R.id.group_sponsor);
    }


    /**
     * 等待接听界面
     */
    public void showWaitingResponseView() {
        //1. 展示自己的画面
        mLayoutManagerTrtc.setMySelfUserId(mSelfModel.userId);
        TRTCVideoLayout videoLayout = addUserToManager(mSelfModel);
        if (videoLayout == null) {
            return;
        }
        videoLayout.setVideoAvailable(true);
        mITRTCVideoCall.openCamera(true, videoLayout.getVideoView());

        //2. 展示对方的头像和蒙层
        mSponsorGroup.setVisibility(View.VISIBLE);
        Picasso.get().load(mSponsorUserModel.userAvatar).into(mSponsorAvatarImg);
        mSponsorUserNameTv.setText(mSponsorUserModel.userName);

        //3. 展示电话对应界面
        mHangupLl.setVisibility(View.VISIBLE);
        mDialingLl.setVisibility(View.VISIBLE);
        mHandsfreeLl.setVisibility(View.GONE);
        mMuteLl.setVisibility(View.GONE);
        //3. 设置对应的listener
        mHangupLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mITRTCVideoCall.reject();
                finish();
            }
        });
        mDialingLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //2.接听电话
                mITRTCVideoCall.accept();
                showCallingView();
            }
        });
        //4. 展示其他用户界面
        showOtherInvitingUserView();
    }

    /**
     * 展示邀请列表
     */
    public void showInvitingView() {
        //1. 展示自己的界面
        mLayoutManagerTrtc.setMySelfUserId(mSelfModel.userId);
        TRTCVideoLayout videoLayout = addUserToManager(mSelfModel);
        if (videoLayout == null) {
            return;
        }
        videoLayout.setVideoAvailable(true);
        mITRTCVideoCall.openCamera(true, videoLayout.getVideoView());
        //        for (UserModel userModel : mCallUserModelList) {
        //            TRTCVideoLayout layout = addUserToManager(userModel);
        //            layout.getShadeImg().setVisibility(View.VISIBLE);
        //        }
        //2. 设置底部栏
        mHangupLl.setVisibility(View.VISIBLE);
        mHangupLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mITRTCVideoCall.hangup();
                finish();
            }
        });
        mDialingLl.setVisibility(View.GONE);
        mHandsfreeLl.setVisibility(View.GONE);
        mMuteLl.setVisibility(View.GONE);
        //3. 隐藏中间他们也在界面
        hideOtherInvitingUserView();
        //4. sponsor画面也隐藏
        mSponsorGroup.setVisibility(View.GONE);
    }

    /**
     * 展示通话中的界面
     */
    public void showCallingView() {
        //1. 蒙版消失
        mSponsorGroup.setVisibility(View.GONE);
        //2. 底部状态栏
        mHangupLl.setVisibility(View.VISIBLE);
        mDialingLl.setVisibility(View.GONE);
        mHandsfreeLl.setVisibility(View.VISIBLE);
        mMuteLl.setVisibility(View.VISIBLE);

        mHangupLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mITRTCVideoCall.hangup();
                finish();
            }
        });
        showTimeCount();
        hideOtherInvitingUserView();
    }

    private void showTimeCount() {
        if (mTimeRunnable != null) {
            return;
        }
        mTimeCount = 0;
        mTimeTv.setText(getShowTime(mTimeCount));
        if (mTimeRunnable == null) {
            mTimeRunnable = new Runnable() {
                @Override
                public void run() {
                    mTimeCount++;
                    if (mTimeTv != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTimeTv.setText(getShowTime(mTimeCount));
                            }
                        });
                    }
                    mTimeHandler.postDelayed(mTimeRunnable, 1000);
                }
            };
        }
        mTimeHandler.postDelayed(mTimeRunnable, 1000);
    }

    private void stopTimeCount() {
        mTimeHandler.removeCallbacks(mTimeRunnable);
        mTimeRunnable = null;
    }

    private String getShowTime(int count) {
        return String.format("%02d:%02d", count / 60, count % 60);
    }

    private void showOtherInvitingUserView() {
        if (CollectionUtils.isEmpty(mOtherInvitingUserModelList)) {
            return;
        }
        mInvitingGroup.setVisibility(View.VISIBLE);
        int squareWidth = getResources().getDimensionPixelOffset(R.dimen.small_image_size);
        int leftMargin  = getResources().getDimensionPixelOffset(R.dimen.small_image_left_margin);
        for (int index = 0; index < mOtherInvitingUserModelList.size() && index < MAX_SHOW_INVITING_USER; index++) {
            UserModel                 userModel    = mOtherInvitingUserModelList.get(index);
            ImageView                 imageView    = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(squareWidth, squareWidth);
            if (index != 0) {
                layoutParams.leftMargin = leftMargin;
            }
            imageView.setLayoutParams(layoutParams);
            Picasso.get().load(userModel.userAvatar).into(imageView);
            mImgContainerLl.addView(imageView);
        }
    }

    private void hideOtherInvitingUserView() {
        mInvitingGroup.setVisibility(View.GONE);
    }

    private TRTCVideoLayout addUserToManager(UserModel userModel) {
        TRTCVideoLayout layout = mLayoutManagerTrtc.allocCloudVideoView(userModel.userId);
        if (layout == null) {
            return null;
        }
        layout.getUserNameTv().setText(userModel.userName);
        if (!TextUtils.isEmpty(userModel.userAvatar)) {
            Picasso.get().load(userModel.userAvatar).into(layout.getHeadImg());
        }
        return layout;
    }

    private static class IntentParams implements Serializable {
        public List<UserModel> mUserModels;

        public IntentParams(List<UserModel> userModels) {
            mUserModels = userModels;
        }
    }
}
