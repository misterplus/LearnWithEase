package team.one.lwe.ui.activity.room;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.NERtcOption;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.chatroom.fragment.ChatRoomMessageFragment;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;

import team.one.lwe.LWEConstants;
import team.one.lwe.R;
import team.one.lwe.bean.EnterRoomData;
import team.one.lwe.config.Preferences;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.callback.LWENERtcCallback;
import team.one.lwe.ui.callback.RegularCallback;
import team.one.lwe.ui.wedget.LWEToolBarOptions;

public class RoomActivity extends LWEUI {

    private ChatRoomMessageFragment messageFragment;
    private RelativeLayout layoutVideo;
    private boolean enterVideo = false, enterChat = false;
    private boolean videoMuted = false, audioMuted = false;

    //TODO: left room confirmation

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Intent intent = getIntent();
        EnterRoomData enterRoomData = new Gson().fromJson(intent.getStringExtra("enterRoomData"), EnterRoomData.class);
        String roomid = enterRoomData.getRoomid();
        EnterChatRoomData enterChatRoomData = new EnterChatRoomData(roomid);
        layoutVideo = findViewById(R.id.layoutVideo);
        ImageButton buttonVideoSelf = findViewById(R.id.buttonVideoSelf);
        ImageButton buttonAudioSelf = findViewById(R.id.buttonAudioSelf);
        HeadImageView imageAvatarSelf = findViewById(R.id.imageAvatarSelf);
        //TODO: local video & audio muting
        //TODO: rework icon, muted icons should be red
        buttonVideoSelf.setOnClickListener(view -> {
            if (videoMuted) {
                //unmute, change icon
                NERtcEx.getInstance().muteLocalVideoStream(false);
                buttonVideoSelf.setBackgroundResource(R.drawable.lwe_icon_video);
                ToastHelper.showToast(this, R.string.lwe_text_video_unmute);
            }
            else {
                //mute, change icon
                NERtcEx.getInstance().muteLocalVideoStream(true);
                buttonVideoSelf.setBackgroundResource(R.drawable.lwe_icon_video_disabled);
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
            }
            else {
                //mute, change icon
                NERtcEx.getInstance().muteLocalAudioStream(true);
                buttonAudioSelf.setBackgroundResource(R.drawable.lwe_icon_mic_muted);
                ToastHelper.showToast(this, R.string.lwe_text_audio_mute);
            }
            audioMuted = !audioMuted;
        });
        imageAvatarSelf.loadBuddyAvatar(Preferences.getUserAccount());
        if (!enterChat)
            enterChatRoom(enterChatRoomData);
        if (!enterVideo)
            enterVideoRoom(enterRoomData);
    }

    private void enterChatRoom(EnterChatRoomData enterChatRoomData) {
        NIMClient.getService(ChatRoomService.class).enterChatRoom(enterChatRoomData).setCallback(new RegularCallback<EnterChatRoomResultData>(this) {
            @Override
            public void onSuccess(EnterChatRoomResultData data) {
                //TODO: maxUsers check
                int maxUsers = (Integer) data.getRoomInfo().getExtension().get("maxUsers");
                if (data.getRoomInfo().getOnlineUserCount() > maxUsers) {
                    NIMClient.getService(ChatRoomService.class).exitChatRoom(enterChatRoomData.getRoomId());
                    //TODO: room full notify
                    finish();
                    return;
                }
                NimUIKit.enterChatRoomSuccess(data, false);
                ChatRoomInfo info = data.getRoomInfo();
                String roomId = data.getRoomId();
                LWEToolBarOptions options = new LWEToolBarOptions(String.format("%s(%s)", info.getName(), roomId), true);
                setToolBar(R.id.toolbar, options);
                initMessageFragment(roomId);
                enterChat = true;
            }

            @Override
            public void onFailed(int code) {
                //TODO: failed to enter room
                super.onFailed(code);
            }
        });
    }

    private void enterVideoRoom(EnterRoomData enterRoomData) {
        try {
            NERtcEx.getInstance().init(getApplicationContext(), LWEConstants.APP_KEY, new LWENERtcCallback(this), null);
        } catch (Exception e) {
            //TODO: fail to init sdk, abort
            e.printStackTrace();
        }
        NERtcEx.getInstance().joinChannel(enterRoomData.getToken(), enterRoomData.getRoomid(), enterRoomData.getUid());

        NERtcEx.getInstance().enableLocalVideo(true);
        NERtcEx.getInstance().enableLocalAudio(true);
        NERtcVideoView videoSelf = findViewById(R.id.videoSelf);
        NERtcEx.getInstance().setupLocalVideoCanvas(videoSelf);
        enterVideo = true;
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
