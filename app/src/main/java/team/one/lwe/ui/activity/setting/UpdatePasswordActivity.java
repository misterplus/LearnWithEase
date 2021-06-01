package team.one.lwe.ui.activity.setting;

import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ToastHelper;

import org.jetbrains.annotations.NotNull;

import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.config.Preferences;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.wedget.LWEToolBarOptions;
import team.one.lwe.util.APIUtils;
import team.one.lwe.util.TextUtils;
import team.one.lwe.util.UserUtils;

public class UpdatePasswordActivity extends LWEUI {

    private static boolean isPasswordValid(@NotNull String password) {
        return TextUtils.isLegalPassword(password) && TextUtils.getPasswordComplexity(password) > 1 && password.length() >= 6 && password.length() <= 16;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_update_password, true);
        setToolBar(R.id.toolbar, options);

        EditText editTextOldPassword = findViewById(R.id.editTextOldPassword);
        EditText editTextNewPassword = findViewById(R.id.editTextNewPassword);
        EditText editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        Button buttonConfirmPassword = findViewById(R.id.buttonConfirmPassword);

        buttonConfirmPassword.setOnClickListener(view -> {
            String oldPassword = editTextOldPassword.getText().toString();
            String newPassword = editTextNewPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();
            if (!isPasswordValid(newPassword)) {
                ToastHelper.showToast(getBaseContext(), R.string.lwe_error_password);
            } else if (!newPassword.equals(confirmPassword)) {
                ToastHelper.showToast(getBaseContext(), R.string.lwe_error_confirm_password);
            } else {
                doUpdate(NimUIKit.getAccount(), oldPassword, newPassword);
            }
        });

        editTextConfirmPassword.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                buttonConfirmPassword.callOnClick();
            }
            return false;
        });
    }

    private void doUpdate(String username, String oldPassword, String newPassword) {
        new NetworkThread(findViewById(R.id.toolbar)) {
            @Override
            public ASResponse doRequest() {
                return APIUtils.update(username, oldPassword, newPassword);
            }

            @Override
            public void onSuccess(ASResponse asp) {
                Preferences.saveUserToken(UserUtils.getLoginInfo(asp.getInfo()).getToken());
                ToastHelper.showToast(getBaseContext(), R.string.lwe_success_update);
                finish();
            }

            @Override
            public void onFailed(int code, String desc) {
                if (code == 302) {
                    ToastHelper.showToast(getBaseContext(), R.string.lwe_error_old_password);
                    return;
                }
                super.onFailed(code, desc);
            }
        }.start();
    }
}
