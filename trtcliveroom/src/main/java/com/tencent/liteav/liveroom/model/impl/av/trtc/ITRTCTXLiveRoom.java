package com.tencent.liteav.liveroom.model.impl.av.trtc;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.EGLContext;

import com.tencent.liteav.beauty.TXBeautyManager;
import com.tencent.liteav.liveroom.model.TRTCBGMManager;
import com.tencent.liteav.liveroom.model.impl.base.TXCallback;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.List;

public interface ITRTCTXLiveRoom {
    void init(Context context);

    void setDelegate(ITXTRTCLiveRoomDelegate deleagte);

    void enterRoom(int sdkAppId, String roomId, String userId, String userSign, int role, TXCallback callback);

    void exitRoom(TXCallback callback);

    void startCameraPreview(boolean isFront, TXCloudVideoView view, TXCallback callback);

    void stopCameraPreview();

    void switchCamera();

    void setMirror(boolean isMirror);

    void muteLocalAudio(boolean mute);

    int sendCustomVideoData(EGLContext context, int textureId, int textureWidth, int textureHeight);

    void startPublish(String streamId, TXCallback callback);

    void stopPublish(TXCallback callback);

    void startPlay(String userId, TXCloudVideoView view, TXCallback callback);

    void stopPlay(String userId, TXCallback callback);

    void stopAllPlay();

    void muteRemoteAudio(String userId, boolean mute);

    void muteAllRemoteAudio(boolean mute);

    void showVideoDebugLog(boolean isShow);

    void startPK(String roomId, String userId, TXCallback callback);

    void stopPK();

    boolean isEnterRoom();

    void setMixConfig(List<TXTRTCMixUser> list, boolean isPK);

    TXBeautyManager getTXBeautyManager();

    TRTCBGMManager getTRTCBgmManager();

    void setFilter(Bitmap filterImage);

    void setGreenScreenFile(String path);

    void setFilterConcentration(float concentration);
}
