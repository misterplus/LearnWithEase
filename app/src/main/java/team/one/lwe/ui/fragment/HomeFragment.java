package team.one.lwe.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.netease.nim.uikit.common.ui.popupmenu.NIMPopupMenu;
import com.netease.nim.uikit.common.ui.popupmenu.PopupMenuItem;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;

import java.util.ArrayList;
import java.util.List;

import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.bean.EnterRoomData;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.activity.room.CreateRoomActivity;
import team.one.lwe.ui.activity.room.RoomActivity;
import team.one.lwe.ui.callback.RegularCallback;
import team.one.lwe.util.APIUtils;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ImageButton buttonRoomNew = view.findViewById(R.id.buttonRoomNew);
        EditText testInput = view.findViewById(R.id.testInput);

        List<PopupMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new PopupMenuItem(0, "创建房间"));
        menuItems.add(new PopupMenuItem(1, "加入房间"));

        NIMPopupMenu menu = new NIMPopupMenu(getContext(), menuItems, item -> {
            switch (item.getTag()) {
                case 0: {
                    startActivityForResult(new Intent(getContext(), CreateRoomActivity.class), 0);
                    break;
                }
                case 1: {
                    String roomId = testInput.getText().toString();
                    //TODO: rework this
                    new NetworkThread(view) {
                        @Override
                        public ASResponse doRequest() {
                            return APIUtils.getRoomToken(roomId);
                        }

                        @Override
                        public void onSuccess(ASResponse asp) {
                            Intent intent = new Intent(getContext(), RoomActivity.class);
                            EnterRoomData enterRoomData = new EnterRoomData(roomId, asp.getToken(), asp.getInfo().getLong("uid"));
                            intent.putExtra("enterRoomData", new Gson().toJson(enterRoomData));
                            startActivity(intent);
                        }
                    }.start();
                }
            }
        });
        buttonRoomNew.setOnClickListener(v -> menu.show(buttonRoomNew));
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 1) { //create room and success
            Intent intent = new Intent(getContext(), RoomActivity.class);
            intent.putExtra("enterRoomData", data.getStringExtra("enterRoomData"));
            startActivity(intent);
        }
    }
}
