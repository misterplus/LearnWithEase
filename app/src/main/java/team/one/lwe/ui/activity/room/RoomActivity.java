package team.one.lwe.ui.activity.room;

import android.os.Bundle;

import com.google.gson.Gson;
import com.netease.nim.uikit.business.chatroom.fragment.ChatRoomMessageFragment;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;

import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.wedget.LWEToolBarOptions;

public class RoomActivity extends LWEUI {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        ASResponse asp = new Gson().fromJson(getIntent().getStringExtra("asp"), ASResponse.class);
        ChatRoomInfo room = asp.getChatroom();
        LWEToolBarOptions options = new LWEToolBarOptions(String.format("%s(%s)", room.getName(), room.getRoomId()), true);
        setToolBar(R.id.toolbar, options);
        if (savedInstanceState == null) {
            ChatRoomMessageFragment chatroom = new ChatRoomMessageFragment();
            String roomId = room.getRoomId();
            chatroom.init(roomId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.chatroom, chatroom)
                    .commit();
        }
    }
}
