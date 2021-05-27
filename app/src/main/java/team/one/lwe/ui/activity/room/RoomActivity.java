package team.one.lwe.ui.activity.room;

import android.os.Bundle;

import com.netease.nim.uikit.business.chatroom.fragment.ChatRoomMessageFragment;

import team.one.lwe.R;
import team.one.lwe.ui.activity.LWEUI;

public class RoomActivity extends LWEUI {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        if (savedInstanceState == null) {
            ChatRoomMessageFragment chatroom = new ChatRoomMessageFragment();
            String roomId = getIntent().getStringExtra("roomId");
            chatroom.init(roomId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.chatroom, chatroom)
                    .commit();
        }
    }
}
