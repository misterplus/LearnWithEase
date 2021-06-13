package team.one.lwe.ui.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.List;

import lombok.AllArgsConstructor;
import team.one.lwe.R;
import team.one.lwe.bean.UserInfo;
import team.one.lwe.ui.activity.friend.FriendInfoActivity;
import team.one.lwe.ui.callback.RegularCallback;
import team.one.lwe.util.UserUtils;

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
        UserInfo ex = new Gson().fromJson(info.getExtension(), UserInfo.class);
        holder.imageAvatar.loadBuddyAvatar(account);
        holder.textName.setText(String.format("%s(%s)", info.getName(), account));
        holder.textInfo.setText(String.format("%s %s岁 | %s %s", holder.view.getResources().getStringArray(R.array.lwe_gender)[info.getGenderEnum().getValue()], ex.getAge(), holder.view.getResources().getStringArray(R.array.lwe_spinner_edu)[ex.getBak()], UserUtils.getGradeValues(holder.view.getResources(), ex.getBak())[ex.getGrade()]));
        holder.view.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), FriendInfoActivity.class);
            intent.putExtra("accid", account);
            intent.putExtra("blackList", "blackList");
            view.getContext().startActivity(intent);
        });

        holder.buttonRemove.setOnClickListener(v -> {
            NIMClient.getService(FriendService.class).removeFromBlackList(account).setCallback(new RegularCallback<Void>(holder.view.getContext()) {
                @Override
                public void onSuccess(Void param) {
                    holder.textResult.setVisibility(View.VISIBLE);
                    holder.buttonRemove.setVisibility(View.GONE);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return blackList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View view;
        private final HeadImageView imageAvatar;
        private final TextView textName, textInfo, textResult;
        private final Button buttonRemove;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            imageAvatar = view.findViewById(R.id.imageAvatar);
            textName = view.findViewById(R.id.textName);
            textInfo = view.findViewById(R.id.textInfo);
            textResult = view.findViewById(R.id.textResult);
            buttonRemove = view.findViewById(R.id.buttonRemove);
        }
    }
}
