package team.one.lwe.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import team.one.lwe.R;
import team.one.lwe.ui.activity.mine.EditPrefActivity;
import team.one.lwe.ui.activity.mine.EditProfileActivity;
import team.one.lwe.ui.activity.mine.SettingActivity;

public class MineFragment extends Fragment {

    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mine, container, false);
        TextView textUsername = view.findViewById(R.id.textUsername);
        TextView textPersonalSignature = view.findViewById(R.id.textPersonalSignature);
        ImageButton buttonEditProfile = view.findViewById(R.id.buttonEditProfile);
        ImageButton buttonPreference = view.findViewById(R.id.buttonPreference);
        ImageButton buttonSetting = view.findViewById(R.id.buttonSetting);
        buttonEditProfile.setOnClickListener(view1 -> startActivity(new Intent(getContext(), EditProfileActivity.class)));
        buttonPreference.setOnClickListener(view1 -> startActivity(new Intent(getContext(), EditPrefActivity.class)));
        buttonSetting.setOnClickListener(view1 -> startActivity(new Intent(getContext(), SettingActivity.class)));
        String account = NimUIKit.getAccount();
        HeadImageView imageAvatar = view.findViewById(R.id.imageAvatar);
        NimUserInfo user = NIMClient.getService(UserService.class).getUserInfo(account);
        String signature = user.getSignature();
        imageAvatar.loadBuddyAvatar(account);
        textUsername.setText(account);
        textPersonalSignature.setText(signature);
        return view;
    }
}
