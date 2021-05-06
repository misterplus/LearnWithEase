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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;
import org.jetbrains.annotations.NotNull;

import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.util.APIUtils;
import team.one.lwe.util.NavigationUtils;
import team.one.lwe.util.TextUtils;

public class LoginFragment extends Fragment {

    private View view;
    private EditText editTextUsername, editTextPassword;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        editTextUsername = view.findViewById(R.id.editTextUsername);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        Button buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(view1 -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            if (!isUsernameValid(username) || !isPasswordValid(password)) {
                ToastHelper.showToast(view.getContext(), R.string.lwe_error_login_format);
            } else if (!NetworkUtil.isNetAvailable(getActivity())){
                ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
            } else {
                DialogMaker.showProgressDialog(view.getContext(), inflater.getContext().getString(R.string.lwe_progress_login), false);
                doConvert(username, password);
            }
        });
        Button buttonRegister = view.findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(view1 -> NavigationUtils.navigateTo(this, new RegisterFragment(), true));
        editTextPassword.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                buttonLogin.callOnClick();
            }
            return false;
        });
        return view;
    }

    private static boolean isUsernameValid(@NotNull String username) {
        return TextUtils.isLegalUsername(username) && username.length() >= 6 && username.length() <= 16;
    }

    private static boolean isPasswordValid(@NotNull String password) {
        return TextUtils.isLegalPassword(password) && TextUtils.getPasswordComplexity(password) > 1 && password.length() >= 6 && password.length() <= 16;
    }

    private void doConvert(String username, String password) {
        new NetworkThread(view){
            @Override
            public ASResponse doRequest() {
                return APIUtils.convert(username, password);
            }

            @Override
            public void onSuccess(ASResponse asp) {
                doLogin(new LoginInfo(asp.getInfo().getStr("accid"), asp.getInfo().getStr("token")));
            }

            @Override
            public void onFailed(int code, String desc) {
                DialogMaker.dismissProgressDialog();
                switch (code) {
                    case 408: {
                        ToastHelper.showToast(view.getContext(), R.string.lwe_error_timeout);
                        break;
                    }
                    case 415: {
                        ToastHelper.showToast(view.getContext(), R.string.lwe_error_confail);
                        break;
                    }
                    default: {
                        ToastHelper.showToast(view.getContext(), R.string.lwe_error_unknown);
                    }
                }
            }

            @Override
            public void onException(Exception e) {
                DialogMaker.dismissProgressDialog();
                super.onException(e);
            }
        }.start();
    }

    private void doLogin(LoginInfo info) {
        NimUIKit.login(info, new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo info) {
                Log.i(this.getClass().getSimpleName(), "login success");
                DialogMaker.dismissProgressDialog();
                //TODO: go to main page
                NimUIKit.startP2PSession(view.getContext(), "plus_dev");
                // ^ this is only a placeholder for now
                //RELEASE: enable caching
                // caching is disabled for debugging purposes
                //Preferences.saveUserAccount(getContext(), info.getAccount());
                //Preferences.saveUserToken(getContext(), info.getToken());
            }

            @Override
            public void onFailed(int code) {
                DialogMaker.dismissProgressDialog();
                switch (code) {
                    case 302: {
                        ToastHelper.showToast(view.getContext(), R.string.lwe_error_login_info);
                        break;
                    }
                    case 408: {
                        ToastHelper.showToast(view.getContext(), R.string.lwe_error_timeout);
                        break;
                    }
                    case 415: {
                        ToastHelper.showToast(view.getContext(), R.string.lwe_error_confail);
                        break;
                    }
                    case 416: {
                        ToastHelper.showToast(view.getContext(), R.string.lwe_error_login_retry);
                        break;
                    }
                    default: {
                        ToastHelper.showToast(view.getContext(), R.string.lwe_error_unknown);
                    }
                }
            }

            @Override
            public void onException(Throwable e) {
                DialogMaker.dismissProgressDialog();
                e.printStackTrace();
            }
        });
    }
}
