package team.one.lwe.ui.activity.room;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.chatroom.fragment.ChatRoomMessageFragment;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomKickOutEvent;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;

import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.bean.RoomInvite;
import team.one.lwe.config.Preferences;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.callback.LWENERtcCallback;
import team.one.lwe.ui.callback.RegularCallback;
import team.one.lwe.ui.custom.msg.InviteAttachment;
import team.one.lwe.ui.wedget.LWEToolBarOptions;
import team.one.lwe.util.APIUtils;

public class RoomActivity extends LWEUI {

    private ChatRoomMessageFragment messageFragment;
    private RelativeLayout layoutVideo;
    private boolean videoMuted = false, audioMuted = false, videoMutedByPause = false, audioMutedByPause = false;
    private boolean leaving = false;
    private ImageButton buttonVideoSelf, buttonAudioSelf;
    private boolean creator = false;
    private String roomId, name, coverUrl;
    private int maxUsers;
    private Observer<ChatRoomKickOutEvent> kickOutObserver;

    public boolean isCreator() {
        return creator;
    }

    public String getRoomId() {
        return roomId;
    }

    @Override
    protected boolean isHideInput(View v, MotionEvent ev) {
        RelativeLayout tml = findViewById(R.id.textMessageLayout);
        int[] inputLoc = {0, 0};
        tml.getLocationInWindow(inputLoc);
        return ev.getY() < inputLoc[1];
    }

    @Override
    protected void hideSoftInput(IBinder token) {
        if (messageFragment != null)
            messageFragment.shouldCollapseInputPanel();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            ArrayList<String> selects = data.getStringArrayListExtra("RESULT_DATA");
            if (selects == null)
                return;
            RoomInvite invite = new RoomInvite();
            invite.setCoverUrl(coverUrl);
            invite.setName(name);
            invite.setRoomId(roomId);
            InviteAttachment attachment = new InviteAttachment();
            attachment.setInvite(invite);
            IMMessage message;

            for (String name : selects) {
                message = MessageBuilder.createCustomMessage(name, SessionTypeEnum.P2P, attachment.getInvite().getRoomId(), attachment);
                NIMClient.getService(MsgService.class).sendMessage(message, true);
            }
        }
    }

    public void onJoinChannelFailed() {
        ToastHelper.showToast(this, R.string.lwe_error_join_room);
        finish();
    }

    public void onDisconnect() {
        leaving = true;
        ToastHelper.showToast(this, R.string.lwe_error_room_disconnect);
        NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
        NimUIKit.exitedChatRoom(roomId);
        NERtcEx.getInstance().release();
        NIMClient.getService(ChatRoomServiceObserver.class).observeKickOutEvent(kickOutObserver, false);
        RoomActivity.super.onNavigateUpClicked();
    }

    public void leaveRoom() {
        leaving = true;
        NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
        NimUIKit.exitedChatRoom(roomId);
        NERtcEx.getInstance().leaveChannel();
        NERtcEx.getInstance().release();
        NIMClient.getService(ChatRoomServiceObserver.class).observeKickOutEvent(kickOutObserver, false);
        RoomActivity.super.onNavigateUpClicked();
    }


    //TODO: test this from another client
    @Override
    protected void onPause() {
        super.onPause();
        //mute if weren't muted
        if (!leaving) {
            if (!videoMuted) {
                buttonVideoSelf.callOnClick();
                videoMutedByPause = true;
            }
            if (!audioMuted) {
                buttonAudioSelf.callOnClick();
                audioMutedByPause = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //unmute if muted by onPause
        if (!leaving) {
            if (videoMutedByPause) {
                buttonVideoSelf.callOnClick();
                videoMutedByPause = false;
            }
            if (audioMutedByPause) {
                buttonAudioSelf.callOnClick();
                audioMutedByPause = false;
            }
        }
    }

    @Override
    public void onNavigateUpClicked() {
        EasyAlertDialogHelper.createOkCancelDiolag(this, getString(R.string.lwe_title_room_exit), getString(creator ? R.string.lwe_text_room_exit_creator : R.string.lwe_text_room_exit), getString(R.string.lwe_button_confirm), getString(R.string.lwe_button_cancel), false, new EasyAlertDialogHelper.OnDialogActionListener() {
            @Override
            public void doCancelAction() {
                //do nothing
            }

            @Override
            public void doOkAction() {
                leaveRoom();
            }
        }).show();
    }

    private void getCreator(String roomId) {
        NIMClient.getService(ChatRoomService.class).fetchRoomInfo(roomId).setCallback(new RegularCallback<ChatRoomInfo>(this) {
            @Override
            public void onSuccess(ChatRoomInfo info) {
                new NetworkThread(layoutVideo) {
                    @Override
                    public ASResponse doRequest() {
                        return APIUtils.getUid(info.getCreator());
                    }

                    @Override
                    public void onSuccess(ASResponse asp) {
                        LWENERtcCallback.getInstance().setCreator(asp.getInfo().getLong("uid"));
                    }
                }.start();
            }

            @Override
            public void onFailed(int code) {
                new Handler(msg -> {
                    getCreator(roomId);
                    return true;
                }).sendEmptyMessageDelayed(0, 1000);
            }
        });
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        LWENERtcCallback.getInstance().setRootView(this);
        Intent intent = getIntent();
        EnterChatRoomResultData data = (EnterChatRoomResultData) intent.getSerializableExtra("data");
        ChatRoomInfo info = data.getRoomInfo();
        roomId = data.getRoomId();
        name = info.getName();
        coverUrl = (String) info.getExtension().get("coverUrl");
        maxUsers = (Integer) info.getExtension().get("maxUsers");
        getCreator(roomId);
        creator = intent.getBooleanExtra("creator", false);
        layoutVideo = findViewById(R.id.layoutVideo);
        buttonVideoSelf = findViewById(R.id.buttonVideoSelf);
        buttonAudioSelf = findViewById(R.id.buttonAudioSelf);
        HeadImageView imageAvatarSelf = findViewById(R.id.imageAvatarSelf);
        NERtcVideoView videoSelf = findViewById(R.id.videoSelf);
        FrameLayout videoPlaceholderSelf = findViewById(R.id.videoPlaceholderSelf);

        buttonVideoSelf.setOnClickListener(view -> {
            if (videoMuted) {
                //unmute, change icon
                NERtcEx.getInstance().muteLocalVideoStream(false);
                buttonVideoSelf.setBackgroundResource(R.drawable.lwe_icon_video);
                videoSelf.setVisibility(View.VISIBLE);
                videoPlaceholderSelf.setVisibility(View.GONE);
                imageAvatarSelf.setVisibility(View.GONE);
                ToastHelper.showToast(this, R.string.lwe_text_video_unmute);
            } else {
                //mute, change icon
                NERtcEx.getInstance().muteLocalVideoStream(true);
                buttonVideoSelf.setBackgroundResource(R.drawable.lwe_icon_video_disabled);
                videoSelf.setVisibility(View.GONE);
                videoPlaceholderSelf.setVisibility(View.VISIBLE);
                imageAvatarSelf.setVisibility(View.VISIBLE);
                ToastHelper.showToast(this, R.string.lwe_text_video_mute);
            }
            videoMuted = !videoMuted;
        });
        buttonAudioSelf.setOnClickListener(view -> {
            if (audioMuted) {
                //unmute, change icon
                NERtcEx.getInstance().muteLocalAudioStream(false);
                buttonAudioSelf.setBackgroundResource(R.drawable.lwe_icon_mic);
                ToastHelper.showToast(this, R.string.lwe_text_audio_unmute);
            } else {
                //mute, change icon
                NERtcEx.getInstance().muteLocalAudioStream(true);
                buttonAudioSelf.setBackgroundResource(R.drawable.lwe_icon_mic_muted);
                ToastHelper.showToast(this, R.string.lwe_text_audio_mute);
            }
            audioMuted = !audioMuted;
        });
        imageAvatarSelf.loadBuddyAvatar(Preferences.getUserAccount());

        NERtcEx.getInstance().enableLocalVideo(true);
        NERtcEx.getInstance().enableLocalAudio(true);
        NERtcEx.getInstance().setupLocalVideoCanvas(videoSelf);

        kickOutObserver = (Observer<ChatRoomKickOutEvent>) chatRoomKickOutEvent -> {
            ToastHelper.showToast(getBaseContext(), R.string.lwe_text_kick);
            NimUIKit.exitedChatRoom(roomId);
            NERtcEx.getInstance().leaveChannel();
            NERtcEx.getInstance().release();
            NIMClient.getService(ChatRoomServiceObserver.class).observeKickOutEvent(kickOutObserver, false);
            RoomActivity.super.onNavigateUpClicked();
        };
        NIMClient.getService(ChatRoomServiceObserver.class).observeKickOutEvent(kickOutObserver, true);

        NimUIKit.enterChatRoomSuccess(data, false);
        LWEToolBarOptions options = new LWEToolBarOptions(String.format("%s(%s)", info.getName(), roomId), true);
        setToolBar(R.id.toolbar, options);
        initMessageFragment(roomId);
    }

    private void initMessageFragment(String roomId) {
        messageFragment = (ChatRoomMessageFragment) getSupportFragmentManager().findFragmentById(R.id.chat_room_message_fragment);
        if (messageFragment != null) {
            messageFragment.init(roomId);
            messageFragment.setLayoutVideo(layoutVideo);
        } else {
            // 如果Fragment还未Create完成，延迟初始化
            getHandler().postDelayed(() -> {
                initMessageFragment(roomId);
            }, 50);
        }
    }

    public String getName() {
        return name;
    }

    public String getCoverUrl() {
        return coverUrl;
    }
}
