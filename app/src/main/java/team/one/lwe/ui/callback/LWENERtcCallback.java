package team.one.lwe.ui.callback;

import com.netease.lava.nertc.sdk.NERtcCallbackEx;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.stats.NERtcAudioVolumeInfo;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

import java.util.ArrayList;
import java.util.List;

import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.util.APIUtils;

public class LWENERtcCallback implements NERtcCallbackEx {

    private final LWEUI rootView;
    private final List<Long> uidOthers = new ArrayList<>();
    private static final int[] VIDEO_VIEW_IDS = {R.id.videoUser1, R.id.videoUser2, R.id.videoUser3};
    private static final int[] AVATAR_VIEW_IDS = {R.id.imageAvatarUser1, R.id.imageAvatarUser2, R.id.imageAvatarUser3};

    public LWENERtcCallback(LWEUI rootView) {
        this.rootView = rootView;
    }

    private void addUserToRoom(long uid) {
        //setup video
        uidOthers.add(uid);
        int pos = uidOthers.indexOf(uid);
        NERtcVideoView remoteView = rootView.findViewById(VIDEO_VIEW_IDS[pos]);
        NERtcEx.getInstance().setupRemoteVideoCanvas(remoteView, uid);

        //setup avatar
        new NetworkThread(remoteView) {

            @Override
            public ASResponse doRequest() {
                return APIUtils.getAccid(uid);
            }

            @Override
            public void onSuccess(ASResponse asp) {
                String accid = asp.getInfo().getStr("accid");
                HeadImageView avatarView = rootView.findViewById(AVATAR_VIEW_IDS[pos]);
                avatarView.loadBuddyAvatar(accid);
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
     * @param uid other user's uid
     */
    @Override
    public void onUserJoined(long uid) {
        addUserToRoom(uid);
    }

    @Override
    public void onUserLeave(long l, int i) {

    }

    @Override
    public void onUserAudioStart(long l) {

    }

    @Override
    public void onUserAudioStop(long l) {

    }

    @Override
    public void onUserVideoStart(long l, int i) {

    }

    @Override
    public void onUserVideoStop(long l) {

    }

    @Override
    public void onDisconnect(int i) {

    }

    @Override
    public void onUserAudioMute(long l, boolean b) {

    }

    @Override
    public void onUserVideoMute(long l, boolean b) {

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
