package team.one.lwe.ui.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;

import org.jetbrains.annotations.NotNull;

import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.wedget.LWEToolBarOptions;
import team.one.lwe.util.APIUtils;
import team.one.lwe.util.TextUtils;

public class SettingActivity extends UI {

    private static boolean isPasswordValid(@NotNull String password) {
        return TextUtils.isLegalPassword(password) && TextUtils.getPasswordComplexity(password) > 1 && password.length() >= 6 && password.length() <= 16;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_setting, true);
        setToolBar(R.id.toolbar, options);

        EditText editTextPassword = findViewById(R.id.editTextPassword);
        EditText editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        ImageButton buttonUpdatePassword = findViewById(R.id.buttonUpdatePassword);
        ImageButton buttonConfirmPassword = findViewById(R.id.buttonConfirmPassword);
        RelativeLayout passwordLayout = findViewById(R.id.passwordLayout);
        RelativeLayout confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);

        buttonUpdatePassword.setOnClickListener(view -> {
            passwordLayout.setVisibility(View.VISIBLE);
            confirmPasswordLayout.setVisibility(View.VISIBLE);
        });

        buttonConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = editTextPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();
                if (!isPasswordValid(password)) {
                    ToastHelper.showToast(view.getContext(), R.string.lwe_error_password);
                } else if (!password.equals(confirmPassword)) {
                    ToastHelper.showToast(view.getContext(), R.string.lwe_error_confirm_password);
                } else if (!NetworkUtil.isNetAvailable(getBaseContext())) {
                    ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
                } else {
                    doUpdate(NimUIKit.getAccount(), password);
                }
            }
        });
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
                DialogMaker.dismissProgressDialog();
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
                DialogMaker.dismissProgressDialog();
            }
        }.start();
    }
}
