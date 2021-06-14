package team.one.lwe.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.netease.nimlib.sdk.friend.FriendService;

import java.io.Serializable;
import java.util.List;

import team.one.lwe.LWEConstants;
import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.bean.EnterRoomData;
import team.one.lwe.bean.StudyRoomInfo;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.activity.room.CreateRoomActivity;
import team.one.lwe.ui.activity.room.RoomActivity;
import team.one.lwe.ui.adapter.RoomAdapter;
import team.one.lwe.ui.callback.LWENERtcCallback;
import team.one.lwe.ui.callback.RegularCallback;
import team.one.lwe.util.APIUtils;
import team.one.lwe.util.ImgUtils;

public class HomeFragment extends Fragment {

    //TODO: refresh recs

    private String roomId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ImageButton buttonRoomNew = view.findViewById(R.id.buttonRoomNew);
        EditText editTextSearchRoom = view.findViewById(R.id.editTextSearchRoom);
        RecyclerView listRoom = view.findViewById(R.id.listRoom);
        RelativeLayout searchedResult = view.findViewById(R.id.searchedResult);
        ImageView imageCover = searchedResult.findViewById(R.id.imageCover);
        ImageButton buttonClose = searchedResult.findViewById(R.id.buttonClose);
        TextView textRoomName = searchedResult.findViewById(R.id.textRoomName);
        LinearLayout noResult = view.findViewById(R.id.noResult);
        HeadImageView[] imageAvatars = new HeadImageView[4];
        imageAvatars[0] = searchedResult.findViewById(R.id.imageAvatar1);
        imageAvatars[1] = searchedResult.findViewById(R.id.imageAvatar2);
        imageAvatars[2] = searchedResult.findViewById(R.id.imageAvatar3);
        imageAvatars[3] = searchedResult.findViewById(R.id.imageAvatar4);
        GridLayoutManager grid = new GridLayoutManager(getContext(), 2);
        grid.setOrientation(LinearLayoutManager.VERTICAL);
        listRoom.setLayoutManager(grid);
        new NetworkThread(view) {
            @Override
            public ASResponse doRequest() {
                return APIUtils.fetchRecs();
            }

            @Override
            public void onSuccess(ASResponse asp) {
                List<StudyRoomInfo> recs = new Gson().fromJson(asp.getRecs().toString(), new TypeToken<List<StudyRoomInfo>>() {
                }.getType());
                RoomAdapter adapter = new RoomAdapter(recs);
                listRoom.setAdapter(adapter);
            }
        }.start();

        editTextSearchRoom.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                DialogMaker.showProgressDialog(getContext(), getString(R.string.lwe_progress_search));
                roomId = editTextSearchRoom.getText().toString();
                EnterChatRoomData enterChatRoomData = new EnterChatRoomData(roomId);
                NIMClient.getService(ChatRoomService.class).enterChatRoom(enterChatRoomData).setCallback(new RegularCallback<EnterChatRoomResultData>(getContext()) {
                    @Override
                    public void onSuccess(EnterChatRoomResultData param) {
                        NIMClient.getService(ChatRoomService.class).fetchRoomInfo(roomId).setCallback(new RegularCallback<ChatRoomInfo>(getContext()) {

                            @Override
                            public void onSuccess(ChatRoomInfo info) {
                                NIMClient.getService(ChatRoomService.class).fetchRoomMembers(roomId, MemberQueryType.ONLINE_NORMAL, 0, 4).setCallback(new RegularCallback<List<ChatRoomMember>>(context) {
                                    @Override
                                    public void onSuccess(List<ChatRoomMember> list) {
                                        DialogMaker.dismissProgressDialog();
                                        for (int i = 0; i < list.size(); i++) {
                                            imageAvatars[i].loadBuddyAvatar(list.get(i).getAccount());
                                        }
                                        NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
                                    }

                                    @Override
                                    public void onFailed(int code) {
                                        DialogMaker.dismissProgressDialog();
                                        ToastHelper.showToast(context, R.string.lwe_error_avatar);
                                    }
                                });

                                ImgUtils.loadRoomCover(getContext(), imageCover, (String) info.getExtension().get("coverUrl"));
                                textRoomName.setText(info.getName());
                                searchedResult.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFailed(int code) {
                                DialogMaker.dismissProgressDialog();
                                ToastHelper.showToast(context, R.string.lwe_error_fetch_room_info);
                                super.onFailed(code);
                            }
                        });
                    }

                    @Override
                    public void onFailed(int code) {
                        if (code == 404) {
                            noResult.setVisibility(View.VISIBLE);
                            searchedResult.setVisibility(View.GONE);
                            new Handler(msg -> {
                                noResult.setVisibility(View.GONE);
                                return true;
                            }).sendEmptyMessageDelayed(0, 5000);
                            return;
                        }
                        super.onFailed(code);
                    }
                });


                return true;
            }
            return false;
        });
        searchedResult.setOnClickListener(v -> new NetworkThread(view) {
            @Override
            public ASResponse doRequest() {
                return APIUtils.getRoomToken(roomId);
            }

            @Override
            public void onSuccess(ASResponse asp) {
                EnterRoomData enterRoomData = new EnterRoomData(roomId, asp.getToken(), asp.getInfo().getLong("uid"));
                enterRoom(enterRoomData, false);
            }

            @Override
            public void onFailed(int code, String desc) {
                ToastHelper.showToast(getContext(), R.string.lwe_error_join_room);
            }
        }.start());
        buttonClose.setOnClickListener(view1 -> {
            searchedResult.setVisibility(View.GONE);
            noResult.setVisibility(View.GONE);
        });
        //menu is probably deprecated here, since matchmaking is not gonna get implemented lol
//        List<PopupMenuItem> menuItems = new ArrayList<>();
//        menuItems.add(new PopupMenuItem(0, "创建房间"));
//        menuItems.add(new PopupMenuItem(1, "加入房间"));
//        NIMPopupMenu menu = new NIMPopupMenu(getContext(), menuItems, item -> {
//            switch (item.getTag()) {
//                case 0: {
//                    startActivityForResult(new Intent(getContext(), CreateRoomActivity.class), 0);
//                    break;
//                }
//                case 1: {
//                    String roomId = "";
//                    new NetworkThread(view) {
//                        @Override
//                        public ASResponse doRequest() {
//                            return APIUtils.getRoomToken(roomId);
//                        }
//
//                        @Override
//                        public void onSuccess(ASResponse asp) {
//                            EnterRoomData enterRoomData = new EnterRoomData(roomId, asp.getToken(), asp.getInfo().getLong("uid"));
//                            enterRoom(enterRoomData, false);
//                        }
//                    }.start();
//                }
//            }
//        });

        //button is only for creating rooms now
        buttonRoomNew.setOnClickListener(v -> startActivityForResult(new Intent(getContext(), CreateRoomActivity.class), 0));

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 1) { //create room and success
            EnterRoomData enterRoomData = new Gson().fromJson(data.getStringExtra("enterRoomData"), EnterRoomData.class);
            enterRoom(enterRoomData, true);
        }
    }

    private void enterRoom(EnterRoomData enterRoomData, boolean creator) {
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
                if (!creator && (Boolean) data.getRoomInfo().getExtension().get("friendsOnly")) {
                    boolean isFriend = NIMClient.getService(FriendService.class).isMyFriend(data.getRoomInfo().getCreator());
                    if (!isFriend) {
                        NIMClient.getService(ChatRoomService.class).exitChatRoom(enterChatRoomData.getRoomId());
                        ToastHelper.showToast(getContext(), R.string.lwe_error_room_not_friend);
                        return;
                    }
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
                intent.putExtra("creator", creator);
                startActivity(intent);
            }

            @Override
            public void onFailed(int code) {
                if (code == 404) {
                    ToastHelper.showToast(getContext(), R.string.lwe_error_room_invalid);
                    return;
                }
                ToastHelper.showToast(getContext(), R.string.lwe_error_join_room);
            }
        });
    }
}
