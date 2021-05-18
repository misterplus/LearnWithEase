package team.one.lwe.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.SystemMessageService;
import com.netease.nimlib.sdk.msg.model.SystemMessage;

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
        //TODO: set avatar
        //holder.imageAvatar.setImageURI("set avatar");
        holder.textName.setText(request.getAccount());
        holder.textReason.setText(request.getMsg());
        holder.buttonAccept.setOnClickListener(v -> {
            NIMClient.getService(FriendService.class).ackAddFriendRequest(request.getAccount(), true).setCallback(new VoidSuccessCallback(holder.view));
            NIMClient.getService(SystemMessageService.class).setSystemMessageRead(requestMsg.getMessageId());
        });
        holder.buttonDecline.setOnClickListener(v -> {
            NIMClient.getService(FriendService.class).ackAddFriendRequest(request.getAccount(), false).setCallback(new VoidSuccessCallback(holder.view));
            NIMClient.getService(SystemMessageService.class).setSystemMessageRead(requestMsg.getMessageId());
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final View view;
        private final RoundedImageView imageAvatar;
        private final TextView textName;
        private final TextView textReason;
        private final Button buttonAccept;
        private final Button buttonDecline;


        public ViewHolder(View view) {
            super(view);
            this.view = view;
            imageAvatar = view.findViewById(R.id.imageAvatar);
            textName = view.findViewById(R.id.textName);
            textReason = view.findViewById(R.id.textReason);
            buttonAccept = view.findViewById(R.id.buttonAccept);
            buttonDecline = view.findViewById(R.id.buttonDecline);
        }
    }
}
