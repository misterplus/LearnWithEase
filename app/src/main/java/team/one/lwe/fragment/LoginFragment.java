package team.one.lwe.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;
import org.jetbrains.annotations.NotNull;

import cn.hutool.core.io.IORuntimeException;
import team.one.lwe.R;
import team.one.lwe.util.APIUtils;
import team.one.lwe.util.TextUtils;

public class LoginFragment extends Fragment {

    private View view;
    private EditText editTextUsername, editTextPassword;
    private ImageButton buttonLogin;
    private Button buttonRegister;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        editTextUsername = view.findViewById(R.id.editTextUsername);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(view1 -> {
            if (!isUsernameValid(editTextUsername.getText().toString()) || !isPasswordValid(editTextPassword.getText().toString())) {
                ToastHelper.showToast(view.getContext(), R.string.lwe_error_login);
            } else {
                DialogMaker.showProgressDialog(view.getContext(), inflater.getContext().getString(R.string.lwe_progress_login), false);
                new Thread(() -> {
                    try {
                        LoginInfo info = APIUtils.convert(editTextUsername.getText().toString(), editTextPassword.getText().toString());
                        doLogin(info);
                    } catch (IORuntimeException e) {
                        // TODO: failed to connect (endpoint not available)
                        // TODO: timeout ?
                    }
                }).start();
            }
        });
        buttonRegister = view.findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(view1 -> {
            // TODO: go to register
        });
        return view;
    }

    private static boolean isUsernameValid(@NotNull String username) {
        return TextUtils.isLegalUsername(username) && username.length() >= 6 && username.length() <= 16;
    }

    private static boolean isPasswordValid(@NotNull String password) {
        return TextUtils.isLegalPassword(password) && TextUtils.getPasswordComplexity(password) > 1 && password.length() >= 6 && password.length() <= 16;
    }

    public void doLogin(LoginInfo info) {
        // this method will also init UIKit
        NimUIKit.login(info, new RequestCallback<LoginInfo>() {
            // callback already runs on main thread
            @Override
            public void onSuccess(LoginInfo info) {
                Log.i(this.getClass().getSimpleName(), "login success");
                DialogMaker.dismissProgressDialog();
                // TODO: go to main page
                NimUIKit.startP2PSession(view.getContext(), "plus_dev");
                // ^ this is only a placeholder for now
                // TODO: enable caching for release
                // caching is disabled for debugging purposes
                //Preferences.saveUserAccount(getContext(), info.getAccount());
                //Preferences.saveUserToken(getContext(), info.getToken());
            }

            @Override
            public void onFailed(int code) {
                DialogMaker.dismissProgressDialog(); // dismiss dialog first cause login failed
                switch (code) {
                    case 302: {
                        // TODO: info incorrect

                        break;
                    }
                    case 408: {
                        // TODO: timeout
                        break;
                    }
                    case 415: {
                        // TODO: connection failed
                        break;
                    }
                    case 416: {
                        // TODO: too many retries
                        break;
                    }
                    default: {

                    }
                }
            }

            @Override
            public void onException(Throwable exception) {
                exception.printStackTrace();
            }
        });
    }


}
