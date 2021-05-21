package team.one.lwe.ui.activity.mine;

import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;

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
    private String jiaDeToken;
    private String newPassword;
    private String confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_update_password, true);
        setToolBar(R.id.toolbar, options);

        jiaDeToken = Preferences.getUserAccount(getBaseContext());
        EditText editTextOldPassword = findViewById(R.id.editTextOldPassword);
        EditText editTextNewPassword = findViewById(R.id.editTextNewPassword);
        EditText editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        Button buttonConfirmPassword = findViewById(R.id.buttonConfirmPassword);

        buttonConfirmPassword.setOnClickListener(view -> {
            newPassword = editTextNewPassword.getText().toString();
            confirmPassword = editTextConfirmPassword.getText().toString();
            String oldPassword = editTextOldPassword.getText().toString();
            doConvert(NimUIKit.getAccount(), oldPassword);
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

    private void doConvert(String username, String oldPassword) {
        new NetworkThread(findViewById(R.id.toolbar)) {
            @Override
            public ASResponse doRequest() {
                return APIUtils.convert(username, oldPassword);
            }

            @Override
            public void onSuccess(ASResponse asp) {
                token = asp.getInfo().getStr("token");
                if (!token.equals(Preferences.getUserToken(getBaseContext()))) {
                    ToastHelper.showToast(getBaseContext(), R.string.lwe_error_old_password);
                } else if (!isPasswordValid(newPassword)) {
                    ToastHelper.showToast(getBaseContext(), R.string.lwe_error_password);
                } else if (!newPassword.equals(confirmPassword)) {
                    ToastHelper.showToast(getBaseContext(), R.string.lwe_error_confirm_password);
                } else if (!NetworkUtil.isNetAvailable(getBaseContext())) {
                    ToastHelper.showToast(getBaseContext(), R.string.lwe_error_nonetwork);
                } else {
                    doUpdate(NimUIKit.getAccount(), newPassword);
                }
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
                Preferences.saveUserToken(getBaseContext(), token);
                ToastHelper.showToast(getBaseContext(), R.string.lwe_success_update);
                finish();
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
