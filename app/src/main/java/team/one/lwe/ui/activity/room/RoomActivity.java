package team.one.lwe.ui.activity.room;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.chatroom.fragment.ChatRoomMessageFragment;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;

import team.one.lwe.R;
import team.one.lwe.config.Preferences;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.callback.LWENERtcCallback;
import team.one.lwe.ui.wedget.LWEToolBarOptions;

public class RoomActivity extends LWEUI {

    private ChatRoomMessageFragment messageFragment;
    private RelativeLayout layoutVideo;
    private boolean videoMuted = false, audioMuted = false, creator = false;

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

    public void onJoinChannelFailed() {
        ToastHelper.showToast(this, R.string.lwe_error_join_room);
        finish();
    }

    public void onDisconnect() {
        ToastHelper.showToast(this, R.string.lwe_error_room_disconnect);
        Intent intent = getIntent();
        EnterChatRoomResultData data = (EnterChatRoomResultData) intent.getSerializableExtra("data");
        NIMClient.getService(ChatRoomService.class).exitChatRoom(data.getRoomId());
        NimUIKit.exitedChatRoom(data.getRoomId());
        NERtcEx.getInstance().release();
        RoomActivity.super.onNavigateUpClicked();
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
                Intent intent = getIntent();
                EnterChatRoomResultData data = (EnterChatRoomResultData) intent.getSerializableExtra("data");
                NIMClient.getService(ChatRoomService.class).exitChatRoom(data.getRoomId());
                NimUIKit.exitedChatRoom(data.getRoomId());
                NERtcEx.getInstance().leaveChannel();
                NERtcEx.getInstance().release();
                RoomActivity.super.onNavigateUpClicked();
            }
        }).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        LWENERtcCallback.getInstance().setRootView(this);
        Intent intent = getIntent();
        EnterChatRoomResultData data = (EnterChatRoomResultData) intent.getSerializableExtra("data");
        creator = intent.getBooleanExtra("creator", false);
        layoutVideo = findViewById(R.id.layoutVideo);
        ImageButton buttonVideoSelf = findViewById(R.id.buttonVideoSelf);
        ImageButton buttonAudioSelf = findViewById(R.id.buttonAudioSelf);
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

        NimUIKit.enterChatRoomSuccess(data, false);
        ChatRoomInfo info = data.getRoomInfo();
        String roomId = data.getRoomId();
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
}
