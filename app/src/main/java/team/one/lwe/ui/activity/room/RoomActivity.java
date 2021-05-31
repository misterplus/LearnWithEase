package team.one.lwe.ui.activity.room;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.chatroom.fragment.ChatRoomMessageFragment;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;

import team.one.lwe.R;
import team.one.lwe.bean.EnterRoomData;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.callback.RegularCallback;
import team.one.lwe.ui.wedget.LWEToolBarOptions;

public class RoomActivity extends LWEUI {

    private ChatRoomMessageFragment messageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Intent intent = getIntent();
        EnterRoomData enterRoomData = new Gson().fromJson(intent.getStringExtra("enterRoomData"), EnterRoomData.class);
        String token = enterRoomData.getToken();
        long uid = enterRoomData.getUid();
        String roomid = enterRoomData.getRoomid();
        EnterChatRoomData enterChatRoomData = new EnterChatRoomData(roomid);

        NIMClient.getService(ChatRoomService.class).enterChatRoom(enterChatRoomData).setCallback(new RegularCallback<EnterChatRoomResultData>(this) {
            @Override
            public void onSuccess(EnterChatRoomResultData data) {
                NimUIKit.enterChatRoomSuccess(data, false);
                ChatRoomInfo info = data.getRoomInfo();
                String roomId = data.getRoomId();
                LWEToolBarOptions options = new LWEToolBarOptions(String.format("%s(%s)", info.getName(), roomId), true);
                setToolBar(R.id.toolbar, options);
                initMessageFragment(roomId);
            }
        });
    }

    private void initMessageFragment(String roomId) {
        messageFragment = (ChatRoomMessageFragment) getSupportFragmentManager().findFragmentById(
                R.id.chat_room_message_fragment);
        if (messageFragment != null) {
            messageFragment.init(roomId);
        } else {
            // 如果Fragment还未Create完成，延迟初始化
            getHandler().postDelayed(() -> initMessageFragment(roomId), 50);
        }
    }
}
