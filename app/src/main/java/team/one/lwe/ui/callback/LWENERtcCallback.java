package team.one.lwe.ui.callback;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
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
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.config.Preferences;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.activity.friend.FriendInfoActivity;
import team.one.lwe.ui.activity.room.RoomActivity;
import team.one.lwe.util.APIUtils;

public class LWENERtcCallback implements NERtcCallbackEx {

    private static final LWENERtcCallback instance = new LWENERtcCallback();
    private final Map<Long, Integer> viewIds = new HashMap<>();
    private final Map<Long, Boolean> videoSub = new HashMap<>();
    private final Map<Long, Boolean> audioSub = new HashMap<>();
    private RoomActivity rootView;
    private long creator;

    public static LWENERtcCallback getInstance() {
        return instance;
    }

    public int getUsers() {
        return viewIds.size() + 1;
    }

    private LinearLayout getLayoutUsers() {
        return rootView.findViewById(R.id.layoutUsers);
    }

    private void removeUserFromRoom(long uid) {
        View userView = getLayoutUsers().findViewById(viewIds.get(uid));
        NERtcEx.getInstance().subscribeRemoteAudioStream(uid, false);
        NERtcEx.getInstance().subscribeRemoteVideoStream(uid, getVideoQuality(), false);
        viewIds.remove(uid);
        videoSub.remove(uid);
        audioSub.remove(uid);
        getLayoutUsers().removeView(userView);
    }

    private void addUserToRoom(long uid) {

        View userView = rootView.getLayoutInflater().inflate(R.layout.lwe_layout_video, getLayoutUsers(), false);

        ImageButton buttonVideoUser = userView.findViewById(R.id.buttonVideoUser);
        NERtcVideoView remoteView = userView.findViewById(R.id.videoUser);
        ImageButton buttonAudioUser = userView.findViewById(R.id.buttonAudioUser);
        HeadImageView imageAvatarUser = userView.findViewById(R.id.imageAvatarUser);
        FrameLayout videoPlaceholder = userView.findViewById(R.id.videoPlaceholder);
        ImageButton buttonKick = userView.findViewById(R.id.buttonKick);
        ImageButton buttonInfo = userView.findViewById(R.id.buttonInfo);

        buttonVideoUser.setOnClickListener(view -> {
            boolean subbed = videoSub.get(uid);
            if (subbed) {
                //unsub, show avatar, change icon
                imageAvatarUser.setVisibility(View.VISIBLE);
                videoPlaceholder.setVisibility(View.VISIBLE);
                remoteView.setVisibility(View.GONE);
                buttonVideoUser.setBackgroundResource(R.drawable.lwe_icon_video_disabled);
                NERtcEx.getInstance().subscribeRemoteVideoStream(uid, getVideoQuality(), false);
            } else {
                //resub, hide avatar, change icon
                imageAvatarUser.setVisibility(View.GONE);
                videoPlaceholder.setVisibility(View.GONE);
                remoteView.setVisibility(View.VISIBLE);
                buttonVideoUser.setBackgroundResource(R.drawable.lwe_icon_video);
                NERtcEx.getInstance().subscribeRemoteVideoStream(uid, getVideoQuality(), true);
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
            } else {
                //resub, change icon
                NERtcEx.getInstance().subscribeRemoteAudioStream(uid, true);
                buttonAudioUser.setBackgroundResource(R.drawable.lwe_icon_mic);
                ToastHelper.showToast(rootView, R.string.lwe_text_audio_unmute_remote);
            }
            audioSub.put(uid, !subbed);
        });

        getLayoutUsers().addView(userView);
        viewIds.put(uid, userView.getId());

        NERtcEx.getInstance().setupRemoteVideoCanvas(remoteView, uid);

        new NetworkThread(userView) {

            @Override
            public ASResponse doRequest() {
                return APIUtils.getAccid(uid);
            }

            @Override
            public void onSuccess(ASResponse asp) {
                String accid = asp.getInfo().getStr("accid");
                NIMClient.getService(UserService.class).fetchUserInfo(Collections.singletonList(accid)).setCallback(new MissingInfoCallback(rootView) {
                    @Override
                    public void onSuccess(List<NimUserInfo> param) {
                        imageAvatarUser.loadBuddyAvatar(accid);
                    }
                });
                buttonInfo.setOnClickListener(view -> {
                    Intent intent = new Intent(rootView, FriendInfoActivity.class);
                    intent.putExtra("accid", accid);
                    rootView.startActivity(intent);
                });
                if (rootView.isCreator())
                    buttonKick.setOnClickListener(view -> {
                        EasyAlertDialogHelper.createOkCancelDiolag(rootView, rootView.getString(R.string.lwe_title_room_kick), rootView.getString(R.string.lwe_text_room_kick_desc), rootView.getString(R.string.lwe_button_confirm), rootView.getString(R.string.lwe_button_cancel), false, new EasyAlertDialogHelper.OnDialogActionListener() {
                            @Override
                            public void doCancelAction() {
                                //do nothing
                            }

                            @Override
                            public void doOkAction() {
                                Map<String, Object> reason = new HashMap<>();
                                reason.put("reason", "kick");
                                NIMClient.getService(ChatRoomService.class).kickMember(rootView.getRoomId(), accid, reason).setCallback(new RegularCallback<Void>(rootView) {
                                    @Override
                                    public void onSuccess(Void param) {
                                        ToastHelper.showToast(rootView, R.string.lwe_success_room_kick);
                                    }

                                    @Override
                                    public void onFailed(int code) {
                                        ToastHelper.showToast(rootView, R.string.lwe_error_room_kick);
                                    }
                                });
                            }
                        }).show();
                    });
                else
                    buttonKick.setVisibility(View.GONE);
            }

            @Override
            public void onFailed(int code, String desc) {
                ToastHelper.showToast(rootView, R.string.lwe_error_avatar);
            }
        }.start();
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
        // not success
        if (result != 0 && rootView != null) {
            rootView.onJoinChannelFailed();
        }
    }

    @Override
    public void onLeaveChannel(int i) {
        rootView = null;
        viewIds.clear();
        videoSub.clear();
        audioSub.clear();
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
        if (uid != creator)
            removeUserFromRoom(uid);
        else {
            ToastHelper.showToast(rootView, R.string.lwe_error_room_force_leave);
            new Handler(msg -> {
                rootView.leaveRoom();
                return true;
            }).sendEmptyMessageDelayed(0, 3000);
        }
    }

    @Override
    public void onUserAudioStart(long uid) {
        NERtcEx.getInstance().subscribeRemoteAudioStream(uid, true);
        audioSub.put(uid, true);
    }

    @Override
    public void onUserAudioStop(long uid) {
        onUserAudioMute(uid, true);
    }

    private NERtcRemoteVideoStreamType getVideoQuality() {
        return NERtcRemoteVideoStreamType.values()[Preferences.getVideoQuality()];
    }

    @Override
    public void onUserVideoStart(long uid, int maxProfile) {
        NERtcEx.getInstance().subscribeRemoteVideoStream(uid, getVideoQuality(), true);
        videoSub.put(uid, true);
    }

    @Override
    public void onUserVideoStop(long uid) {
        onUserVideoMute(uid, true);
    }

    @Override
    public void onDisconnect(int reason) {
        Log.e(getClass().getSimpleName(), "disconnected from video room: reason " + reason);
        rootView.onDisconnect();
    }

    @Override
    public void onUserAudioMute(long uid, boolean muted) {
        View userView = getLayoutUsers().findViewById(viewIds.get(uid));
        ImageButton buttonAudioUser = userView.findViewById(R.id.buttonAudioUser);
        boolean subbed = audioSub.get(uid);
        if (subbed && muted) {
            //already subbed, but user muted, we unsub him
            buttonAudioUser.callOnClick();
        } else if (!subbed && !muted) {
            //not subbed, user unmuted, we sub him
            buttonAudioUser.callOnClick();
        }
    }

    @Override
    public void onUserVideoMute(long uid, boolean muted) {
        View userView = getLayoutUsers().findViewById(viewIds.get(uid));
        ImageButton buttonVideoUser = userView.findViewById(R.id.buttonVideoUser);
        boolean subbed = videoSub.get(uid);
        if (subbed && muted) {
            //already subbed, but user muted, we unsub him
            buttonVideoUser.callOnClick();
        } else if (!subbed && !muted) {
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

    public void setRootView(RoomActivity rootView) {
        this.rootView = rootView;
    }

    public void setCreator(long creator) {
        this.creator = creator;
    }
}
