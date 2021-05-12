package team.one.lwe.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

import java.util.List;
import java.util.logging.Logger;

import team.one.lwe.R;
import team.one.lwe.util.NavigationUtils;

import static com.netease.nim.uikit.api.NimUIKit.getAccount;

public class MineFragment extends Fragment {

    private View view;
    TextView textUsername, textPersonalSignature;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mine, container, false);
        textUsername = view.findViewById(R.id.textUsername);
        textPersonalSignature = view.findViewById(R.id.textPersonalSignature);
        ImageButton buttonEditProfile = view.findViewById(R.id.buttonEditProfile);
        buttonEditProfile.setOnClickListener(view1 -> NavigationUtils.navigateTo(this, new EditProfileFragment(), false));
        String account = NimUIKit.getAccount();
        NimUserInfo user = NIMClient.getService(UserService.class).getUserInfo(account);
        String signature = user.getSignature();
        textUsername.setText(account);
        textPersonalSignature.setText(signature);
        return view;
    }
}
