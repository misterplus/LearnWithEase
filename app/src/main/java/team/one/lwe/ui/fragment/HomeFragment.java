package team.one.lwe.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.popupmenu.NIMPopupMenu;
import com.netease.nim.uikit.common.ui.popupmenu.PopupMenuItem;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import team.one.lwe.LWEConstants;
import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.bean.EnterRoomData;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.activity.room.CreateRoomActivity;
import team.one.lwe.ui.activity.room.RoomActivity;
import team.one.lwe.ui.callback.LWENERtcCallback;
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
                            EnterRoomData enterRoomData = new EnterRoomData(roomId, asp.getToken(), asp.getInfo().getLong("uid"));
                            enterRoom(enterRoomData);
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
            EnterRoomData enterRoomData = new Gson().fromJson(data.getStringExtra("enterRoomData"), EnterRoomData.class);
            enterRoom(enterRoomData);
        }
    }

    private void enterRoom(EnterRoomData enterRoomData) {
        String roomid = enterRoomData.getRoomid();
        EnterChatRoomData enterChatRoomData = new EnterChatRoomData(roomid);
        NIMClient.getService(ChatRoomService.class).enterChatRoom(enterChatRoomData).setCallback(new RegularCallback<EnterChatRoomResultData>(getContext()) {
            @Override
            public void onSuccess(EnterChatRoomResultData data) {
                int maxUsers = (Integer) data.getRoomInfo().getExtension().get("maxUsers");
                if (data.getRoomInfo().getOnlineUserCount() > maxUsers) {
                    NIMClient.getService(ChatRoomService.class).exitChatRoom(enterChatRoomData.getRoomId());
                    ToastHelper.showToast(getContext(), R.string.lwe_error_room_full);
                    return;
                }
                try {
                    NERtcEx.getInstance().init(getActivity().getApplicationContext(), LWEConstants.APP_KEY, LWENERtcCallback.getInstance(), null);
                } catch (Exception e) {
                    Log.e(HomeFragment.this.getClass().getSimpleName(), Log.getStackTraceString(e));
                    NIMClient.getService(ChatRoomService.class).exitChatRoom(enterChatRoomData.getRoomId());
                    ToastHelper.showToast(getContext(), R.string.lwe_error_init_nertc);
                    return;
                }
                NERtcEx.getInstance().joinChannel(enterRoomData.getToken(), enterRoomData.getRoomid(), enterRoomData.getUid());
                Intent intent = new Intent(getActivity(), RoomActivity.class);
                intent.putExtra("data", (Serializable) data);
                startActivity(intent);
            }

            @Override
            public void onFailed(int code) {
                ToastHelper.showToast(getContext(), R.string.lwe_error_join_room);
            }
        });
    }
}
