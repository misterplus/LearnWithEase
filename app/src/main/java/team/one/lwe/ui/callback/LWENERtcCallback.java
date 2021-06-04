package team.one.lwe.ui.callback;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.netease.lava.nertc.sdk.NERtcCallbackEx;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.stats.NERtcAudioVolumeInfo;
import com.netease.lava.nertc.sdk.video.NERtcRemoteVideoStreamType;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

import java.util.HashMap;
import java.util.Map;

import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.util.APIUtils;

public class LWENERtcCallback implements NERtcCallbackEx {

    private final LWEUI rootView;
    private final LinearLayout layoutUsers;
    private final Map<Long, Integer> viewIds = new HashMap<>();
    private final Map<Long, Boolean> videoSub = new HashMap<>();
    private final Map<Long, Boolean> audioSub = new HashMap<>();

    public LWENERtcCallback(LWEUI rootView) {
        this.rootView = rootView;
        this.layoutUsers = rootView.findViewById(R.id.layoutUsers);
    }

    private void removeUserFromRoom(long uid) {
        View userView = layoutUsers.findViewById(viewIds.get(uid));
        NERtcEx.getInstance().subscribeRemoteAudioStream(uid, false);
        NERtcEx.getInstance().subscribeRemoteVideoStream(uid, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, false);
        viewIds.remove(uid);
        layoutUsers.removeViewInLayout(userView);
    }

    private void addUserToRoom(long uid) {
        //setup video

        //TODO: rework layout, fix padding
        View userView = rootView.getLayoutInflater().inflate(R.layout.lwe_layout_video, layoutUsers, false);
        //TODO: set listeners
        ImageButton buttonVideoUser = userView.findViewById(R.id.buttonVideoUser);
        NERtcVideoView remoteView = userView.findViewById(R.id.videoUser);
        ImageButton buttonAudioUser = userView.findViewById(R.id.buttonAudioUser);
        HeadImageView imageAvatarUser = userView.findViewById(R.id.imageAvatarUser);
        FrameLayout videoPlaceholder = userView.findViewById(R.id.videoPlaceholder);
        //TODO: rework remote muting, should come with notification
        //TODO: avatar not showing up, should be put on top(?)
        buttonVideoUser.setOnClickListener(view -> {
            boolean subbed = videoSub.get(uid);
            if (subbed) {
                //unsub, show avatar, change icon
                imageAvatarUser.setVisibility(View.VISIBLE);
                videoPlaceholder.setVisibility(View.VISIBLE);
                remoteView.setVisibility(View.GONE);
                buttonVideoUser.setBackgroundResource(R.drawable.lwe_icon_video_disabled);
                ToastHelper.showToast(rootView, R.string.lwe_text_video_mute_remote);
                NERtcEx.getInstance().subscribeRemoteVideoStream(uid, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, false);
            }
            else {
                //resub, hide avatar, change icon
                imageAvatarUser.setVisibility(View.GONE);
                videoPlaceholder.setVisibility(View.GONE);
                remoteView.setVisibility(View.VISIBLE);
                buttonVideoUser.setBackgroundResource(R.drawable.lwe_icon_video);
                ToastHelper.showToast(rootView, R.string.lwe_text_video_unmute_remote);
                NERtcEx.getInstance().subscribeRemoteVideoStream(uid, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, true);
            }
            videoSub.put(uid, !subbed);
        });
        buttonAudioUser.setOnClickListener(view -> {
            boolean subbed = audioSub.get(uid);
            if (subbed) {
                //unsub, change icon
                NERtcEx.getInstance().subscribeRemoteAudioStream(uid, false);
                buttonAudioUser.setBackgroundResource(R.drawable.lwe_icon_mic_muted);
                ToastHelper.showToast(rootView, R.string.lwe_text_audio_mute_remote);
            }
            else {
                //resub, change icon
                NERtcEx.getInstance().subscribeRemoteAudioStream(uid, true);
                buttonAudioUser.setBackgroundResource(R.drawable.lwe_icon_mic);
                ToastHelper.showToast(rootView, R.string.lwe_text_audio_unmute_remote);
            }
            audioSub.put(uid, !subbed);
        });

        layoutUsers.addView(userView);
        viewIds.put(uid, userView.getId());

        NERtcEx.getInstance().setupRemoteVideoCanvas(remoteView, uid);

        //setup avatar
        new NetworkThread(userView) {

            @Override
            public ASResponse doRequest() {
                return APIUtils.getAccid(uid);
            }

            @Override
            public void onSuccess(ASResponse asp) {
                String accid = asp.getInfo().getStr("accid");
                imageAvatarUser.loadBuddyAvatar(accid);
            }

            @Override
            public void onFailed(int code, String desc) {
                //TODO: on failed
                super.onFailed(code, desc);
            }
        };
    }

    /**
     * user joins room
     *
     * @param result    0 if success
     * @param channelId room id
     * @param elapsed   not really useful
     */
    @Override
    public void onJoinChannel(int result, long channelId, long elapsed) {
        //TODO: user join channel
        //success
        //failed
        //others
    }

    @Override
    public void onLeaveChannel(int i) {

    }

    /**
     * other user joins room / show existing users in room
     *
     * @param uid other user's uid
     */
    @Override
    public void onUserJoined(long uid) {
        addUserToRoom(uid);
    }

    /**
     * other user leaves room
     *
     * @param uid    left user uid
     * @param reason timeout / normal
     */
    @Override
    public void onUserLeave(long uid, int reason) {
        removeUserFromRoom(uid);
    }

    @Override
    public void onUserAudioStart(long uid) {
        NERtcEx.getInstance().subscribeRemoteAudioStream(uid, true);
        audioSub.put(uid, true);
    }

    @Override
    public void onUserAudioStop(long l) {

    }

    @Override
    public void onUserVideoStart(long uid, int maxProfile) {
        //TODO: profile options
        NERtcEx.getInstance().subscribeRemoteVideoStream(uid, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, true);
        videoSub.put(uid, true);
    }

    @Override
    public void onUserVideoStop(long uid) {

    }

    @Override
    public void onDisconnect(int i) {

    }

    @Override
    public void onUserAudioMute(long uid, boolean muted) {
        //TODO: remote audio mute
        View userView = layoutUsers.findViewById(viewIds.get(uid));
        ImageButton buttonAudioUser = userView.findViewById(R.id.buttonAudioUser);
        boolean subbed = audioSub.get(uid);
        if (subbed && muted) {
            //already subbed, but user muted, we unsub him
            buttonAudioUser.callOnClick();
        }
        else if (!subbed && !muted) {
            //not subbed, user unmuted, we sub him
            buttonAudioUser.callOnClick();
        }
    }

    @Override
    public void onUserVideoMute(long uid, boolean muted) {
        //TODO: remote video mute
        View userView = layoutUsers.findViewById(viewIds.get(uid));
        ImageButton buttonVideoUser = userView.findViewById(R.id.buttonVideoUser);
        boolean subbed = videoSub.get(uid);
        if (subbed && muted) {
            //already subbed, but user muted, we unsub him
            buttonVideoUser.callOnClick();
        }
        else if (!subbed && !muted) {
            //not subbed, user unmuted, we sub him
            buttonVideoUser.callOnClick();
        }
    }

    @Override
    public void onClientRoleChange(int i, int i1) {

    }

    @Override
    public void onFirstAudioDataReceived(long l) {

    }

    @Override
    public void onFirstVideoDataReceived(long l) {

    }

    @Override
    public void onFirstAudioFrameDecoded(long l) {

    }

    @Override
    public void onFirstVideoFrameDecoded(long l, int i, int i1) {
    }

    @Override
    public void onUserVideoProfileUpdate(long l, int i) {

    }

    @Override
    public void onAudioDeviceChanged(int i) {

    }

    @Override
    public void onAudioDeviceStateChange(int i, int i1) {

    }

    @Override
    public void onVideoDeviceStageChange(int i) {

    }

    @Override
    public void onConnectionTypeChanged(int i) {

    }

    @Override
    public void onReconnectingStart() {

    }

    @Override
    public void onReJoinChannel(int i, long l) {

    }

    @Override
    public void onAudioMixingStateChanged(int i) {

    }

    @Override
    public void onAudioMixingTimestampUpdate(long l) {

    }

    @Override
    public void onAudioEffectFinished(int i) {

    }

    @Override
    public void onLocalAudioVolumeIndication(int i) {

    }

    @Override
    public void onRemoteAudioVolumeIndication(NERtcAudioVolumeInfo[] neRtcAudioVolumeInfos, int i) {

    }

    @Override
    public void onLiveStreamState(String s, String s1, int i) {

    }

    @Override
    public void onError(int i) {

    }

    @Override
    public void onWarning(int i) {

    }
}
