package com.uitis;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.view.Surface;
import android.view.View;

import com.effect.EffectRender;

import java.io.IOException;

/**
 * @author shenjb@china
 * @since 2020-04-04
 */
public class EffectUtils {

    private MediaPlayer mMediaWrapper = new MediaPlayer();

    EffectRender effectRender;

    /**
     * 特效
     */
    public  EffectUtils(Context context,GLSurfaceView effectSurfaceView) {
        String filePath = "file:///android_asset/output.mp4";
       // String filePath = "/storage/emulated/0/DCIM/output.mp4";
        try {
            AssetFileDescriptor afd = context.getResources().getAssets().openFd("output.mp4");
            mMediaWrapper.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
           // mMediaWrapper.setDataSource(filePath);
            mMediaWrapper.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        effectSurfaceView.setVisibility(View.VISIBLE);
        mMediaWrapper.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                effectRender.setFlag(true);

            }
        });
        effectRender = new EffectRender(context, effectSurfaceView, new EffectRender.SurfaceListener() {
            @Override
            public void onSurface(Surface surface) {
                mMediaWrapper.setSurface(surface);
            }
        });

        mMediaWrapper.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                if (effectRender!=null)
                    effectRender.setVideoWH(width, height);
            }
        });

    }

    public void play(Callback callback){
        if (!mMediaWrapper.isPlaying()) {
            mMediaWrapper.seekTo(0);
            mMediaWrapper.start();
            effectRender.setFlag(false);
            if (callback != null) {
                callback.onCallback();
            }
        }
    }


    public interface Callback {
        void onCallback();
    }

    public void onDestroy() {
        mMediaWrapper.stop();
        effectRender.destroy();
    }

}
