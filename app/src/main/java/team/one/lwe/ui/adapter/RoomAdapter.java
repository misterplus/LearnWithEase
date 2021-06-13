package team.one.lwe.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import team.one.lwe.LWEConstants;
import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.bean.EnterRoomData;
import team.one.lwe.bean.Preference;
import team.one.lwe.bean.StudyRoomInfo;
import team.one.lwe.bean.UserInfo;
import team.one.lwe.config.Preferences;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.activity.room.RoomActivity;
import team.one.lwe.ui.callback.LWENERtcCallback;
import team.one.lwe.ui.callback.RegularCallback;
import team.one.lwe.util.APIUtils;
import team.one.lwe.util.ImgUtils;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    private final List<StudyRoomInfo> rooms;
    private final NimUserInfo user;

    public RoomAdapter(List<StudyRoomInfo> rooms) {
        this.rooms = rooms;
        this.user = NIMClient.getService(UserService.class).getUserInfo(Preferences.getUserAccount());
    }

    @Override
    public @NotNull ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        StudyRoomInfo info = rooms.get(position);
        String roomId = info.getRoomId();
        Context context = holder.imageCover.getContext();

        //fetch room name and cover
        boolean[] markers = new boolean[2];
        EnterChatRoomData enterChatRoomData = new EnterChatRoomData(roomId);
        NIMClient.getService(ChatRoomService.class).enterChatRoom(enterChatRoomData).setCallback(new RegularCallback(context) {
            @Override
            public void onSuccess(Object param) {
                NIMClient.getService(ChatRoomService.class).fetchRoomInfo(roomId).setCallback(new RegularCallback<ChatRoomInfo>(context) {
                    @Override
                    public void onSuccess(ChatRoomInfo room) {
                        markers[0] = true;
                        if (markers[1])
                            NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
                        holder.textRoomName.setText(room.getName());
                        String url = (String) room.getExtension().get("coverUrl");
                        ImgUtils.loadRoomCover(context, holder.imageCover, url);
                    }

                    @Override
                    public void onFailed(int code) {
                        ToastHelper.showToast(context, R.string.lwe_error_fetch_room_info);
                    }
                });
                //fetch user avatars
                NIMClient.getService(ChatRoomService.class).fetchRoomMembers(roomId, MemberQueryType.ONLINE_NORMAL, 0, 4).setCallback(new RegularCallback<List<ChatRoomMember>>(context) {
                    @Override
                    public void onSuccess(List<ChatRoomMember> list) {
                        markers[1] = true;
                        if (markers[0])
                            NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
                        for (int i = 0; i < list.size(); i++) {
                            holder.imageAvatars[i].loadBuddyAvatar(list.get(i).getAccount());
                        }
                    }

                    @Override
                    public void onFailed(int code) {
                        ToastHelper.showToast(context, R.string.lwe_error_avatar);
                    }
                });
            }
        });


        List<String> tags = new ArrayList<>();
        int timeStudy = info.getTimeStudy();
        int timeRest = info.getTimeRest();
        int contentStudy = info.getContentStudy();
        int gender = info.getGender();
        String province = info.getProvince();
        String city = info.getCity();
        String area = info.getArea();
        String school = info.getSchool();
        UserInfo userInfo = new Gson().fromJson(user.getExtension(), UserInfo.class);
        Preference pref = userInfo.getPref();
        Resources res = context.getResources();
        if (pref.getTimeStudy() == timeStudy)
            tags.add("自习" + res.getStringArray(R.array.lwe_spinner_time_study)[timeStudy]);
        if (pref.getTimeRest() == timeRest) {
            tags.add("休息" + res.getStringArray(R.array.lwe_spinner_time_rest)[timeRest]);
        }
        String content = res.getStringArray(R.array.lwe_spinner_content_study)[contentStudy];
        content = content.equals("全部") ? "全科" : content;
        tags.add(content);
        if (gender != 0 && pref.isSameGender() && gender == user.getGenderEnum().getValue())
            tags.add(String.format("同为%s生", res.getStringArray(R.array.lwe_gender)[gender]));
        if (pref.isSameCity()) {
            if (province.length() <= 5 && province.equals(userInfo.getProvince()))
                tags.add(province);
            if (city.length() <= 5 && city.equals(userInfo.getCity()))
                tags.add(city);
            if (area.length() <= 5 && area.equals(userInfo.getArea()))
                tags.add(area);
        } else {
            if (province.length() <= 5)
                tags.add(province);
            if (city.length() <= 5)
                tags.add(city);
            if (area.length() <= 5)
                tags.add(area);
        }
        if (!school.equals("") && pref.isSameSchool() && school.equals(userInfo.getSchool()))
            tags.add("同校");

        //randomize and sift for 2 tags
        Collections.shuffle(tags);
        List<String> selected = tags.subList(0, 2);

        for (String text : selected) {
            View tag = LayoutInflater.from(context).inflate(R.layout.lwe_layout_tag, holder.layoutTags, false);
            TextView textTag = tag.findViewById(R.id.textTag);
            textTag.setText(text);
            holder.layoutTags.addView(tag);
        }

        holder.layoutRoom.setOnClickListener(v -> new NetworkThread(holder.layoutRoom) {
            @Override
            public ASResponse doRequest() {
                return APIUtils.getRoomToken(roomId);
            }

            @Override
            public void onSuccess(ASResponse asp) {
                EnterRoomData enterRoomData = new EnterRoomData(roomId, asp.getToken(), asp.getInfo().getLong("uid"));
                enterRoom(context, enterRoomData);
            }

            @Override
            public void onFailed(int code, String desc) {
                ToastHelper.showToast(context, R.string.lwe_error_join_room);
            }
        }.start());

    }

    private void enterRoom(Context context, EnterRoomData enterRoomData) {
        String roomid = enterRoomData.getRoomid();
        EnterChatRoomData enterChatRoomData = new EnterChatRoomData(roomid);
        NIMClient.getService(ChatRoomService.class).enterChatRoom(enterChatRoomData).setCallback(new RegularCallback<EnterChatRoomResultData>(context) {
            @Override
            public void onSuccess(EnterChatRoomResultData data) {
                int maxUsers = (Integer) data.getRoomInfo().getExtension().get("maxUsers");
                if (data.getRoomInfo().getOnlineUserCount() > maxUsers) {
                    NIMClient.getService(ChatRoomService.class).exitChatRoom(enterChatRoomData.getRoomId());
                    ToastHelper.showToast(context, R.string.lwe_error_room_full);
                    return;
                }
                try {
                    NERtcEx.getInstance().init(context.getApplicationContext(), LWEConstants.APP_KEY, LWENERtcCallback.getInstance(), null);
                } catch (Exception e) {
                    Log.e(RoomAdapter.this.getClass().getSimpleName(), Log.getStackTraceString(e));
                    NIMClient.getService(ChatRoomService.class).exitChatRoom(enterChatRoomData.getRoomId());
                    ToastHelper.showToast(context, R.string.lwe_error_init_nertc);
                    return;
                }
                NERtcEx.getInstance().joinChannel(enterRoomData.getToken(), enterRoomData.getRoomid(), enterRoomData.getUid());
                Intent intent = new Intent(context, RoomActivity.class);
                intent.putExtra("data", (Serializable) data);
                intent.putExtra("creator", false);
                context.startActivity(intent);
            }

            @Override
            public void onFailed(int code) {
                if (code == 404) {
                    ToastHelper.showToast(context, R.string.lwe_error_room_invalid);
                    return;
                }
                ToastHelper.showToast(context, R.string.lwe_error_join_room);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textRoomName;
        private final HeadImageView[] imageAvatars = new HeadImageView[4];
        private final ImageView imageCover;
        private final LinearLayout layoutTags;
        private final RelativeLayout layoutRoom;

        public ViewHolder(View view) {
            super(view);
            textRoomName = view.findViewById(R.id.textRoomName);
            imageAvatars[0] = view.findViewById(R.id.imageAvatar1);
            imageAvatars[1] = view.findViewById(R.id.imageAvatar2);
            imageAvatars[2] = view.findViewById(R.id.imageAvatar3);
            imageAvatars[3] = view.findViewById(R.id.imageAvatar4);
            imageCover = view.findViewById(R.id.imageCover);
            layoutTags = view.findViewById(R.id.layoutTags);
            layoutRoom = view.findViewById(R.id.layoutRoom);
        }
    }
}
