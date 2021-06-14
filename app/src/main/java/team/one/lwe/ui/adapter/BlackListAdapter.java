package team.one.lwe.ui.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.List;

import lombok.AllArgsConstructor;
import team.one.lwe.R;
import team.one.lwe.ui.activity.friend.FriendInfoActivity;
import team.one.lwe.ui.callback.RegularCallback;

@AllArgsConstructor
public class BlackListAdapter extends RecyclerView.Adapter<BlackListAdapter.ViewHolder> {

    private final List<String> blackList;

    @NonNull
    @Override
    public BlackListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_black_list, parent, false);
        return new BlackListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlackListAdapter.ViewHolder holder, int position) {
        String account = blackList.get(position);
        NimUserInfo info = NIMClient.getService(UserService.class).getUserInfo(account);
        holder.imageAvatar.loadBuddyAvatar(account);
        holder.textName.setText(info.getName());
        holder.textUserName.setText(account);
        holder.view.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), FriendInfoActivity.class);
            intent.putExtra("accid", account);
            view.getContext().startActivity(intent);
        });

        holder.buttonRemove.setOnClickListener(v -> NIMClient.getService(FriendService.class).removeFromBlackList(account).setCallback(new RegularCallback<Void>(holder.view.getContext()) {
            @Override
            public void onSuccess(Void param) {
                holder.buttonRevert.setVisibility(View.VISIBLE);
                holder.buttonRemove.setVisibility(View.GONE);
            }

            @Override
            public void onFailed(int code) {
                ToastHelper.showToast(context, R.string.lwe_error_black_list_remove_fail);
            }
        }));

        holder.buttonRevert.setOnClickListener(view -> NIMClient.getService(FriendService.class).addToBlackList(account).setCallback(new RegularCallback<Void>(holder.view.getContext()) {
            @Override
            public void onSuccess(Void param) {
                holder.buttonRevert.setVisibility(View.GONE);
                holder.buttonRemove.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailed(int code) {
                ToastHelper.showToast(context, R.string.lwe_error_black_list_add_fail);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return blackList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View view;
        private final HeadImageView imageAvatar;
        private final TextView textName, textUserName;
        private final Button buttonRemove, buttonRevert;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            imageAvatar = view.findViewById(R.id.imageAvatar);
            textName = view.findViewById(R.id.textName);
            textUserName = view.findViewById(R.id.textUserName);
            buttonRevert = view.findViewById(R.id.buttonRevert);
            buttonRemove = view.findViewById(R.id.buttonRemove);
        }
    }
}
