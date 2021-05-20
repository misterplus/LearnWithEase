package team.one.lwe.ui.activity.mine;

import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.jetbrains.annotations.NotNull;

import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.config.Preferences;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.wedget.LWEToolBarOptions;
import team.one.lwe.util.APIUtils;
import team.one.lwe.util.TextUtils;

public class UpdatePasswordActivity extends LWEUI {
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_update_password, true);
        setToolBar(R.id.toolbar, options);

        EditText editTextOldPassword = findViewById(R.id.editTextOldPassword);
        EditText editTextNewPassword = findViewById(R.id.editTextNewPassword);
        EditText editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        ImageButton buttonConfirmPassword = findViewById(R.id.buttonConfirmPassword);

        buttonConfirmPassword.setOnClickListener(view -> {
            String oldPassword = editTextOldPassword.getText().toString();
            String newPassword = editTextNewPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();
            doConvert(NimUIKit.getAccount(), oldPassword);
            if (token.equals(Preferences.getUserToken(getBaseContext()))) {
                ToastHelper.showToast(view.getContext(), R.string.lwe_error_old_password);
            } else if (!isPasswordValid(newPassword)) {
                ToastHelper.showToast(view.getContext(), R.string.lwe_error_password);
            } else if (!newPassword.equals(confirmPassword)) {
                ToastHelper.showToast(view.getContext(), R.string.lwe_error_confirm_password);
            } else if (!NetworkUtil.isNetAvailable(getBaseContext())) {
                ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
            } else {
                doUpdate(NimUIKit.getAccount(), newPassword);
            }
        });

        editTextConfirmPassword.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                buttonConfirmPassword.callOnClick();
            }
            return false;
        });
    }

    private static boolean isPasswordValid(@NotNull String password) {
        return TextUtils.isLegalPassword(password) && TextUtils.getPasswordComplexity(password) > 1 && password.length() >= 6 && password.length() <= 16;
    }

    private void getToken(String newToken) {
        this.token = newToken;
    }

    private void doConvert(String username, String password) {
        new NetworkThread(findViewById(R.id.toolbar)) {
            @Override
            public ASResponse doRequest() {
                return APIUtils.convert(username, password);
            }

            @Override
            public void onSuccess(ASResponse asp) {
                getToken(asp.getInfo().getStr("token"));
            }

            @Override
            public void onFailed(int code, String desc) {
                switch (code) {
                    case 408: {
                        ToastHelper.showToast(getBaseContext(), R.string.lwe_error_timeout);
                        break;
                    }
                    case 415: {
                        ToastHelper.showToast(getBaseContext(), R.string.lwe_error_confail);
                        break;
                    }
                    default: {
                        ToastHelper.showToast(getBaseContext(), R.string.lwe_error_unknown);
                    }
                }
            }

            @Override
            public void onException(Exception e) {
                super.onException(e);
            }
        }.start();
    }

    private void doUpdate(String username, String password) {
        new NetworkThread(findViewById(R.id.toolbar)) {
            @Override
            public ASResponse doRequest() {
                return APIUtils.update(username, password);
            }

            @Override
            public void onSuccess(ASResponse asp) {
                ToastHelper.showToast(getBaseContext(), R.string.lwe_success_update);
            }

            @Override
            public void onFailed(int code, String desc) {
                switch (code) {
                    case 408: {
                        ToastHelper.showToast(getBaseContext(), R.string.lwe_error_timeout);
                        break;
                    }
                    case 415: {
                        ToastHelper.showToast(getBaseContext(), R.string.lwe_error_confail);
                        break;
                    }
                    default: {
                        ToastHelper.showToast(getBaseContext(), R.string.lwe_error_unknown);
                    }
                }
            }

            @Override
            public void onException(Exception e) {
                super.onException(e);
            }
        }.start();
    }
}
