package team.one.lwe.fragment;

import android.os.Bundle;

import team.one.lwe.util.APIUtils;
import team.one.lwe.util.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.jetbrains.annotations.NotNull;

import team.one.lwe.R;
import team.one.lwe.config.Preferences;

public class LoginFragment extends Fragment {

    private View view;
    private EditText editTextUsername, editTextPassword;
    private ImageButton buttonLogin;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        editTextUsername = view.findViewById(R.id.editTextUsername);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(view1 -> {
            if (!isUsernameValid(editTextUsername.getText().toString()) || !isPasswordValid(editTextPassword.getText().toString())) {
                // username or password invalid
            } else {
                new Thread(() -> {
                    LoginInfo info = APIUtils.convert(editTextUsername.getText().toString(), editTextPassword.getText().toString());
                    doLogin(info);
                }).start();
            }
        });
        return view;
    }

    private static boolean isUsernameValid(@NotNull String username) {
        return TextUtils.isLegalInfo(username) && username.length() >= 6 && username.length() <= 16;
    }

    private static boolean isPasswordValid(@NotNull String password) {
        return TextUtils.isLegalInfo(password) && password.length() >= 8 && password.length() <= 16;
    }

    public void doLogin(LoginInfo info) {
        RequestCallback<LoginInfo> callback =
                new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo info) {
                        Preferences.saveUserAccount(info.getAccount());
                        Preferences.saveUserToken(info.getToken());
                    }

                    @Override
                    public void onFailed(int code) {
                        if (code == 302) {
                            // fails
                        } else {
                            //
                        }
                    }

                    @Override
                    public void onException(Throwable exception) {
                        // your code
                    }
                };

        //执行手动登录
        NIMClient.getService(AuthService.class).login(info).setCallback(callback);
    }
}
