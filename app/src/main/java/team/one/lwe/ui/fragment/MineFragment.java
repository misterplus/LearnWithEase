package team.one.lwe.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.makeramen.roundedimageview.RoundedImageView;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import team.one.lwe.R;
import team.one.lwe.util.NavigationUtils;
import team.one.lwe.util.UserUtils;

public class MineFragment extends Fragment {

    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mine, container, false);
        TextView textUsername = view.findViewById(R.id.textUsername);
        TextView textPersonalSignature = view.findViewById(R.id.textPersonalSignature);
        ImageButton buttonEditProfile = view.findViewById(R.id.buttonEditProfile);
        ImageButton buttonPreference = view.findViewById(R.id.buttonPreference);
        buttonEditProfile.setOnClickListener(view1 -> NavigationUtils.navigateTo(this, new EditProfileFragment(), true));
        buttonPreference.setOnClickListener(view1 -> NavigationUtils.navigateTo(this, new EditPreferenceFragment(), true));
        String account = NimUIKit.getAccount();
        RoundedImageView imageAvatar = view.findViewById(R.id.imageAvatar);
        NimUserInfo user = NIMClient.getService(UserService.class).getUserInfo(account);
        String signature = user.getSignature();

        imageAvatar.setImageURI(UserUtils.getAvatarUri(imageAvatar, account, user.getAvatar()));
        textUsername.setText(account);
        textPersonalSignature.setText(signature);
        return view;
    }
}
