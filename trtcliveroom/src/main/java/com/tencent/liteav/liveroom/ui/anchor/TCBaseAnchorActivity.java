package com.tencent.liteav.liveroom.ui.anchor;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.tencent.liteav.liveroom.R;
import com.tencent.liteav.liveroom.model.TRTCLiveRoom;
import com.tencent.liteav.liveroom.model.TRTCLiveRoomCallback;
import com.tencent.liteav.liveroom.model.TRTCLiveRoomDef;
import com.tencent.liteav.liveroom.model.TRTCLiveRoomDelegate;
import com.tencent.liteav.liveroom.ui.common.msg.TCChatEntity;
import com.tencent.liteav.liveroom.ui.common.msg.TCChatMsgListAdapter;
import com.tencent.liteav.liveroom.ui.common.utils.TCConstants;
import com.tencent.liteav.liveroom.ui.common.utils.TCUtils;
import com.tencent.liteav.liveroom.ui.widget.InputTextMsgDialog;
import com.tencent.liteav.liveroom.ui.widget.danmaku.TCDanmuMgr;
import com.tencent.liteav.liveroom.ui.widget.like.TCHeartLayout;
import com.tencent.liteav.login.ProfileManager;
import com.tencent.liteav.login.RoomManager;
import com.tencent.liteav.login.UserModel;
import com.uitis.EffectUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import master.flame.danmaku.controller.IDanmakuView;

/**
 * Module:   TCBaseAnchorActivity
 * <p>
 * Function: 主播推流的页面
 **/
public abstract class TCBaseAnchorActivity extends Activity implements TRTCLiveRoomDelegate, View.OnClickListener, InputTextMsgDialog.OnTextSendListener {
    private static final String TAG = TCBaseAnchorActivity.class.getSimpleName();

    // 消息列表相关
    private ListView                mLvMessage;             // 消息控件
    private InputTextMsgDialog      mInputTextMsgDialog;    // 消息输入框
    private TCChatMsgListAdapter    mChatMsgListAdapter;    // 消息列表的Adapter
    private ArrayList<TCChatEntity> mArrayListChatEntity;   // 消息内容

    private TCHeartLayout mHeartLayout;           // 点赞动画的布局

    protected String mCoverPicUrl;           // 直播封面图
    protected String mSelfAvatar;          // 个人头像地址
    protected String mSelfName;              // 个人昵称
    protected String mSelfUserId;                // 个人用户id
    protected String mRoomName;// 直播标题
    protected int    mRoomId;   //房间号
    protected long   mTotalMemberCount   = 0;  // 总进房观众数量
    protected long   mCurrentMemberCount = 0;// 当前观众数量
    protected long   mHeartCount         = 0;        // 点赞数量

    private TCDanmuMgr mDanmuMgr;              // 弹幕管理类

    protected TRTCLiveRoom mLiveRoom;              // 组件类

    protected Handler mMainHandler = new Handler(Looper.getMainLooper());

    // 定时的 Timer 去更新开播时间
    private   Timer              mBroadcastTimer;        // 定时的 Timer
    private   BroadcastTimerTask mBroadcastTimerTask;    // 定时任务
    protected long               mSecond = 0;            // 开播的时间，单位为秒
    private   AlertDialog        mErrorDialog;
    public EffectUtils effectUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutId());

        Intent    intent    = getIntent();
        UserModel userModel = ProfileManager.getInstance().getUserModel();
        mSelfUserId = userModel.userId;
        mSelfName = userModel.userName;
        mSelfAvatar = userModel.userAvatar;
        // 直播封面使用用户的头像
        mCoverPicUrl = userModel.userAvatar;
        mRoomName = intent.getStringExtra(TCConstants.ROOM_TITLE);
        mRoomId = intent.getIntExtra(TCConstants.GROUP_ID, 0);

        mArrayListChatEntity = new ArrayList<>();
        mLiveRoom = TRTCLiveRoom.sharedInstance(this);

        initView();
        if (TextUtils.isEmpty(mSelfName)) {
            mSelfName = mSelfUserId;
        }

        enterRoom();
    }

    public abstract int getLayoutId();

    /**
     * 特别注意，以下几个 findViewById 由于是依赖于子类
     * {@link TCCameraAnchorActivity}
     * 的布局，所以id要保持一致。 若id发生改变，此处id也要同时修改
     */
    protected void initView() {

        GLSurfaceView effectSurfaceView = (GLSurfaceView) findViewById(R.id.effectSurfaceView);
        effectUtils=new EffectUtils(this,effectSurfaceView);
        mLvMessage = (ListView) findViewById(R.id.im_msg_listview);
        mHeartLayout = (TCHeartLayout) findViewById(R.id.heart_layout);

        mInputTextMsgDialog = new InputTextMsgDialog(this, R.style.InputDialog);
        mInputTextMsgDialog.setmOnTextSendListener(this);

        mChatMsgListAdapter = new TCChatMsgListAdapter(this, mLvMessage, mArrayListChatEntity);
        mLvMessage.setAdapter(mChatMsgListAdapter);

        IDanmakuView danmakuView = (IDanmakuView) findViewById(R.id.anchor_danmaku_view);
        mDanmuMgr = new TCDanmuMgr(this);
        mDanmuMgr.setDanmakuView(danmakuView);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_close) {
            showExitInfoDialog("当前正在直播，是否退出直播？", false);
        } else if (id == R.id.btn_message_input) {
            showInputMsgDialog();
        }
    }


    /**
     * /////////////////////////////////////////////////////////////////////////////////
     * //
     * //                      Activity声明周期相关
     * //
     * /////////////////////////////////////////////////////////////////////////////////
     */

    @Override
    public void onBackPressed() {
        showExitInfoDialog("当前正在直播，是否退出直播？", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDanmuMgr != null) {
            mDanmuMgr.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDanmuMgr != null) {
            mDanmuMgr.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (effectUtils!=null)
        effectUtils.onDestroy();
        stopTimer();
        if (mDanmuMgr != null) {
            mDanmuMgr.destroy();
            mDanmuMgr = null;
        }
        exitRoom();
    }

    /**
     * /////////////////////////////////////////////////////////////////////////////////
     * //
     * //                      开始和停止推流相关
     * //
     * /////////////////////////////////////////////////////////////////////////////////
     */
    protected void enterRoom() {
        mLiveRoom.setDelegate(this);
        TRTCLiveRoomDef.TRTCCreateRoomParam param = new TRTCLiveRoomDef.TRTCCreateRoomParam();
        param.roomName = mRoomName;
        // 这里简单把封面图设置成用户头像
        param.coverUrl = mSelfAvatar;
        mLiveRoom.createRoom(mRoomId, param, new TRTCLiveRoomCallback.ActionCallback() {
            @Override
            public void onCallback(int code, String msg) {
                if (code == 0) {
                    //创建成功
                    startTimer();
                    onCreateRoomSuccess();
                } else {
                    Log.w(TAG, String.format("创建直播间错误, code=%s,error=%s", code, msg));
                    showErrorAndQuit(code, "创建直播房间失败,Error:" + msg);
                }
            }
        });
    }

    /**
     * 创建直播间成功
     */
    protected abstract void onCreateRoomSuccess();

    protected void exitRoom() {
        RoomManager.getInstance().destroyRoom(mRoomId, TCConstants.TYPE_LIVE_ROOM, new RoomManager.ActionCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: 后台销毁房间成功");
            }

            @Override
            public void onFailed(int code, String msg) {
                Log.d(TAG, "onFailed: 后台销毁房间失败[" + code);
            }
        });
        mLiveRoom.destroyRoom(new TRTCLiveRoomCallback.ActionCallback() {
            @Override
            public void onCallback(int code, String msg) {
                if (code == 0) {
                    Log.d(TAG, "IM销毁房间成功");
                } else {
                    Log.d(TAG, "IM销毁房间失败:" + msg);
                }
            }
        });
        mLiveRoom.setDelegate(null);
    }

    /**
     * /////////////////////////////////////////////////////////////////////////////////
     * //
     * //                      处理接收到的各种信息
     * //
     * /////////////////////////////////////////////////////////////////////////////////
     */
    protected void handleTextMsg(TRTCLiveRoomDef.TRTCLiveUserInfo userInfo, String text) {
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName(userInfo.userName);
        entity.setContent(text);
        entity.setType(TCConstants.TEXT_TYPE);
        notifyMsg(entity);
    }

    /**
     * 处理观众加入信息
     *
     * @param userInfo
     */
    protected void handleMemberJoinMsg(TRTCLiveRoomDef.TRTCLiveUserInfo userInfo) {
        mTotalMemberCount++;
        mCurrentMemberCount++;
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("通知");
        if (TextUtils.isEmpty(userInfo.userName))
            entity.setContent(userInfo.userId + "加入直播");
        else
            entity.setContent(userInfo.userName + "加入直播");
        entity.setType(TCConstants.MEMBER_ENTER);
        notifyMsg(entity);
    }

    /**
     * 处理观众退出信息
     *
     * @param userInfo
     */
    protected void handleMemberQuitMsg(TRTCLiveRoomDef.TRTCLiveUserInfo userInfo) {
        if (mCurrentMemberCount > 0)
            mCurrentMemberCount--;
        else
            Log.d(TAG, "接受多次退出请求，目前人数为负数");

        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("通知");
        if (TextUtils.isEmpty(userInfo.userName))
            entity.setContent(userInfo.userId + "退出直播");
        else
            entity.setContent(userInfo.userName + "退出直播");
        entity.setType(TCConstants.MEMBER_EXIT);
        notifyMsg(entity);
    }

    /**
     * 处理点赞信息
     *
     * @param userInfo
     */
    protected void handlePraiseMsg(TRTCLiveRoomDef.TRTCLiveUserInfo userInfo) {
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("通知");
        if (TextUtils.isEmpty(userInfo.userName)) {
            entity.setContent(userInfo.userId + "点了个赞");
        } else {
            entity.setContent(userInfo.userName + "点了个赞");
        }

        mHeartLayout.addFavor();
        mHeartCount++;

        entity.setType(TCConstants.PRAISE);
        notifyMsg(entity);
    }

    /**
     * 处理弹幕信息
     *
     * @param userInfo
     * @param text
     */
    protected void handleDanmuMsg(TRTCLiveRoomDef.TRTCLiveUserInfo userInfo, String text) {
//        TCChatEntity entity = new TCChatEntity();
//        entity.setSenderName(userInfo.userName);
//        entity.setContent(text);
//        entity.setType(TCConstants.TEXT_TYPE);
//        notifyMsg(entity);

        if (mDanmuMgr != null) {
            mDanmuMgr.addDanmu(userInfo.userAvatar, userInfo.userName, text);
        }
    }


    /**
     *     /////////////////////////////////////////////////////////////////////////////////
     *     //
     *     //                      发送文本信息
     *     //
     *     /////////////////////////////////////////////////////////////////////////////////
     */
    /**
     * 发消息弹出框
     */
    private void showInputMsgDialog() {
        WindowManager              windowManager = getWindowManager();
        Display                    display       = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp            = mInputTextMsgDialog.getWindow().getAttributes();
        lp.width = display.getWidth(); //设置宽度
        mInputTextMsgDialog.getWindow().setAttributes(lp);
        mInputTextMsgDialog.setCancelable(true);
        mInputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mInputTextMsgDialog.show();
    }


    @Override
    public void onTextSend(String msg, boolean danmuOpen) {
        if (msg.length() == 0) {
            return;
        }
        byte[] byte_num = msg.getBytes(StandardCharsets.UTF_8);
        if (byte_num.length > 160) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }

//        //消息回显
//        TCChatEntity entity = new TCChatEntity();
//        entity.setSenderName("我:");
//        entity.setContent(msg);
//        entity.setType(TCConstants.TEXT_TYPE);
//        notifyMsg(entity);

        if (danmuOpen) {
            if (mDanmuMgr != null) {
                mDanmuMgr.addDanmu(mSelfAvatar, mSelfName, msg);
            }

            mLiveRoom.sendRoomCustomMsg(String.valueOf(TCConstants.IMCMD_DANMU), msg, new TRTCLiveRoomCallback.ActionCallback() {
                @Override
                public void onCallback(int code, String msg) {

                }
            });
        } else {
            mLiveRoom.sendRoomTextMsg(msg, new TRTCLiveRoomCallback.ActionCallback() {
                @Override
                public void onCallback(int code, String msg) {
                    if (code == 0) {
                        ToastUtils.showShort("发送成功");
                    } else {
                        ToastUtils.showShort("发送消息失败[" + code + "]" + msg);
                    }
                }
            });
        }
    }


    private void notifyMsg(final TCChatEntity entity) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mArrayListChatEntity.size() > 1000) {
                    while (mArrayListChatEntity.size() > 900) {
                        mArrayListChatEntity.remove(0);
                    }
                }
                mArrayListChatEntity.add(entity);
                mChatMsgListAdapter.notifyDataSetChanged();
            }
        });
    }


    /**
     *     /////////////////////////////////////////////////////////////////////////////////
     *     //
     *     //                      弹窗相关
     *     //
     *     /////////////////////////////////////////////////////////////////////////////////
     */

    /**
     * 显示直播结果的弹窗
     * <p>
     * 如：观看数量、点赞数量、直播时长数
     */
    protected void showPublishFinishDetailsDialog() {
        //确认则显示观看detail
        FinishDetailDialogFragment dialogFragment = new FinishDetailDialogFragment();
        Bundle                     args           = new Bundle();
        args.putString("time", TCUtils.formattedTime(mSecond));
        args.putString("heartCount", String.format(Locale.CHINA, "%d", mHeartCount));
        args.putString("totalMemberCount", String.format(Locale.CHINA, "%d", mTotalMemberCount));
        dialogFragment.setArguments(args);
        dialogFragment.setCancelable(false);
        if (dialogFragment.isAdded())
            dialogFragment.dismiss();
        else
            dialogFragment.show(getFragmentManager(), "");
    }

    /**
     * 显示确认消息
     *
     * @param msg     消息内容
     * @param isError true错误消息（必须退出） false提示消息（可选择是否退出）
     */
    public void showExitInfoDialog(String msg, Boolean isError) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.liveroom_dialog_fragment);
        builder.setCancelable(true);
        builder.setTitle(msg);

        if (!isError) {
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    exitRoom();
                    showPublishFinishDetailsDialog();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            //当情况为错误的时候，直接停止推流
            exitRoom();
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    showPublishFinishDetailsDialog();
                }
            });
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 显示错误并且退出直播的弹窗
     *
     * @param errorCode
     * @param errorMsg
     */
    protected void showErrorAndQuit(int errorCode, String errorMsg) {
        if (mErrorDialog == null) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this, R.style.LiveRoomDialogTheme)
                    .setTitle("错误")
                    .setMessage(errorMsg)
                    .setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mErrorDialog.dismiss();
                            stopTimer();
                            exitRoom();
                            finish();
                        }
                    });

            mErrorDialog = builder.create();
        }
        if (mErrorDialog.isShowing()) {
            mErrorDialog.dismiss();
        }
        mErrorDialog.show();
    }

    /**
     * /////////////////////////////////////////////////////////////////////////////////
     * //
     * //                      开播时长相关
     * //
     * /////////////////////////////////////////////////////////////////////////////////
     */
    protected void onBroadcasterTimeUpdate(long second) {

    }

    /**
     * 记时器
     */
    private class BroadcastTimerTask extends TimerTask {
        public void run() {
            //Log.i(TAG, "timeTask ");
            ++mSecond;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onBroadcasterTimeUpdate(mSecond);
                }
            });
        }
    }

    private void startTimer() {
        //直播时间
        if (mBroadcastTimer == null) {
            mBroadcastTimer = new Timer(true);
            mBroadcastTimerTask = new BroadcastTimerTask();
            mBroadcastTimer.schedule(mBroadcastTimerTask, 1000, 1000);
        }
    }

    private void stopTimer() {
        //直播时间
        if (null != mBroadcastTimer) {
            mBroadcastTimerTask.cancel();
        }
    }


    @Override
    public void onError(int code, String message) {

    }

    @Override
    public void onWarning(int code, String message) {

    }

    @Override
    public void onDebugLog(String message) {

    }

    @Override
    public void onRoomInfoChange(TRTCLiveRoomDef.TRTCLiveRoomInfo roomInfo) {
        Log.d(TAG, "onRoomInfoChange: " + roomInfo);
    }

    @Override
    public void onRoomDestroy(String roomId) {

    }

    @Override
    public void onAnchorEnter(String userId) {
    }

    @Override
    public void onAnchorExit(String userId) {

    }

    @Override
    public void onAudienceEnter(TRTCLiveRoomDef.TRTCLiveUserInfo userInfo) {
        handleMemberJoinMsg(userInfo);
    }

    @Override
    public void onAudienceExit(TRTCLiveRoomDef.TRTCLiveUserInfo userInfo) {
        handleMemberQuitMsg(userInfo);
    }

    @Override
    public void onRequestJoinAnchor(TRTCLiveRoomDef.TRTCLiveUserInfo userInfo, String reason, int timeout) {

    }

    @Override
    public void onKickoutJoinAnchor() {

    }

    @Override
    public void onRequestRoomPK(TRTCLiveRoomDef.TRTCLiveUserInfo userInfo, int timeout) {

    }

    @Override
    public void onQuitRoomPK() {

    }

    @Override
    public void onRecvRoomTextMsg(String roomId, String message, TRTCLiveRoomDef.TRTCLiveUserInfo userInfo) {
        handleTextMsg(userInfo, message);
    }

    @Override
    public void onRecvRoomCustomMsg(String roomId, String cmd, String message, TRTCLiveRoomDef.TRTCLiveUserInfo userInfo) {
        int type = Integer.valueOf(cmd);
        switch (type) {
            case TCConstants.IMCMD_PRAISE:
                handlePraiseMsg(userInfo);
                break;
            case TCConstants.IMCMD_DANMU:
                handleDanmuMsg(userInfo, message);
                break;

            case TCConstants.IMCMD_EFFECT:
                effectUtils.play(null);
           //   Toast.makeText(getApplicationContext(),"特效",Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }


}
