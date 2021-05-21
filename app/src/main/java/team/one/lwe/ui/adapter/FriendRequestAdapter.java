package team.one.lwe.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.SystemMessageService;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.List;

import lombok.AllArgsConstructor;
import team.one.lwe.R;
import team.one.lwe.ui.callback.VoidSuccessCallback;

@AllArgsConstructor
public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    private final List<SystemMessage> requests;

    @NonNull
    @Override
    public FriendRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestAdapter.ViewHolder holder, int position) {
        SystemMessage requestMsg = requests.get(position);
        AddFriendNotify request = (AddFriendNotify) requestMsg.getAttachObject();
        String account = request.getAccount();
        NimUserInfo info = NIMClient.getService(UserService.class).getUserInfo(account);
        holder.imageAvatar.loadBuddyAvatar(account);
        holder.textName.setText(String.format("%s(%s)", info.getName(), account));
        holder.textReason.setText(request.getMsg());
        holder.buttonAccept.setOnClickListener(v -> {
            NIMClient.getService(FriendService.class).ackAddFriendRequest(request.getAccount(), true).setCallback(new VoidSuccessCallback(holder.view.getContext()));
            NIMClient.getService(SystemMessageService.class).setSystemMessageRead(requestMsg.getMessageId());
            holder.textAccept.setVisibility(View.VISIBLE);
            holder.buttonAccept.setVisibility(View.GONE);
            holder.buttonDecline.setVisibility(View.GONE);
        });
        holder.buttonDecline.setOnClickListener(v -> {
            NIMClient.getService(FriendService.class).ackAddFriendRequest(request.getAccount(), false).setCallback(new VoidSuccessCallback(holder.view.getContext()));
            NIMClient.getService(SystemMessageService.class).setSystemMessageRead(requestMsg.getMessageId());
            holder.textDecline.setVisibility(View.VISIBLE);
            holder.buttonAccept.setVisibility(View.GONE);
            holder.buttonDecline.setVisibility(View.GONE);
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final View view;
        private final HeadImageView imageAvatar;
        private final TextView textName, textReason, textAccept, textDecline;
        private final Button buttonAccept, buttonDecline;


        public ViewHolder(View view) {
            super(view);
            this.view = view;
            imageAvatar = view.findViewById(R.id.imageAvatar);
            textName = view.findViewById(R.id.textName);
            textReason = view.findViewById(R.id.textReason);
            textAccept = view.findViewById(R.id.textAccept);
            textDecline = view.findViewById(R.id.textDecline);
            buttonAccept = view.findViewById(R.id.buttonAccept);
            buttonDecline = view.findViewById(R.id.buttonDecline);
        }
    }
}
