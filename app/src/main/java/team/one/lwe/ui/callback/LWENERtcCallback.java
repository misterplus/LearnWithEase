package team.one.lwe.ui.callback;

import android.view.View;
import android.widget.LinearLayout;

import com.netease.lava.nertc.sdk.NERtcCallbackEx;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.stats.NERtcAudioVolumeInfo;
import com.netease.lava.nertc.sdk.video.NERtcRemoteVideoStreamType;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;
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

    public LWENERtcCallback(LWEUI rootView) {
        this.rootView = rootView;
        this.layoutUsers = rootView.findViewById(R.id.layoutUsers);
    }

    private void removeUserFromRoom(long uid) {
        viewIds.remove(uid);
        View userView = layoutUsers.findViewById(viewIds.get(uid));
        layoutUsers.removeViewInLayout(userView);
    }

    private void addUserToRoom(long uid) {
        //setup video

        View userView = rootView.getLayoutInflater().inflate(R.layout.lwe_layout_video, layoutUsers);
        viewIds.put(uid, userView.getId());
        NERtcVideoView remoteView = userView.findViewById(R.id.videoUser);
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
                HeadImageView avatarView = userView.findViewById(R.id.imageAvatarUser);
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

    /**
     * other user leaves room
     * @param uid left user uid
     * @param reason timeout / normal
     */
    @Override
    public void onUserLeave(long uid, int reason) {
        removeUserFromRoom(uid);
    }

    @Override
    public void onUserAudioStart(long l) {

    }

    @Override
    public void onUserAudioStop(long l) {

    }

    @Override
    public void onUserVideoStart(long uid, int maxProfile) {
        //TODO: profile options
        NERtcEx.getInstance().subscribeRemoteVideoStream(uid, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, true);
    }

    @Override
    public void onUserVideoStop(long uid) {
        NERtcEx.getInstance().subscribeRemoteVideoStream(uid, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, false);
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
