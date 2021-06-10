package team.one.lwe.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.nos.model.NosThumbParam;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

import team.one.lwe.R;
import team.one.lwe.bean.StudyRoomInfo;
import team.one.lwe.ui.callback.RegularCallback;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    private final List<StudyRoomInfo> rooms;

    public RoomAdapter(List<StudyRoomInfo> rooms) {
        this.rooms = rooms;
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
        NIMClient.getService(ChatRoomService.class).fetchRoomInfo(roomId).setCallback(new RegularCallback<ChatRoomInfo>(context) {
            @Override
            public void onSuccess(ChatRoomInfo room) {
                holder.textRoomName.setText(room.getName());
                String url = (String) room.getExtension().get("coverUrl");
                NosThumbParam nosThumbParam = new NosThumbParam();
                nosThumbParam.height = 90;
                nosThumbParam.width = 120;
                File cover = new File(context.getExternalCacheDir() + "/cover", String.format("%s.png", url));
                NIMClient.getService(NosService.class).download(url, nosThumbParam, cover.getAbsolutePath()).setCallback(new RegularCallback<Void>(context) {
                    @Override
                    public void onSuccess(Void param) {
                        holder.imageCover.setBackground(Drawable.createFromPath(cover.getAbsolutePath()));
                    }

                    @Override
                    public void onFailed(int code) {
                        //TODO: on failed downloading room cover
                        super.onFailed(code);
                    }
                });
            }

            @Override
            public void onFailed(int code) {
                //TODO: on failed fetching room info
                super.onFailed(code);
            }
        });
        //fetch user avatars
        NIMClient.getService(ChatRoomService.class).fetchRoomMembers(roomId, MemberQueryType.ONLINE_NORMAL, 0, 4).setCallback(new RegularCallback<List<ChatRoomMember>>(context) {
            @Override
            public void onSuccess(List<ChatRoomMember> list) {
                for (int i = 0; i < list.size(); i++) {
                    holder.imageAvatars[i].loadBuddyAvatar(list.get(i).getAccount());
                }
            }

            @Override
            public void onFailed(int code) {
                //TODO: on failed fetching avatars
                super.onFailed(code);
            }
        });

        int timeStudy = info.getTimeStudy();
        int timeRest = info.getTimeRest();
        int contentStudy = info.getContentStudy();
        int gender = info.getGender();
        String province = info.getProvince();
        String city = info.getCity();
        String area = info.getArea();
        String school = info.getSchool();


    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textRoomName;
        private final HeadImageView[] imageAvatars = new HeadImageView[4];
        private final ImageView imageCover;

        public ViewHolder(View view) {
            super(view);
            textRoomName = view.findViewById(R.id.textRoomName);
            imageAvatars[0] = view.findViewById(R.id.imageAvatar1);
            imageAvatars[1] = view.findViewById(R.id.imageAvatar2);
            imageAvatars[2] = view.findViewById(R.id.imageAvatar3);
            imageAvatars[3] = view.findViewById(R.id.imageAvatar4);
            imageCover = view.findViewById(R.id.imageCover);
        }
    }
}
