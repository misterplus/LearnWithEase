package team.one.lwe.ui.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nim.uikit.impl.cache.ChatRoomMemberCache;
import com.netease.nim.uikit.impl.cache.FriendDataCache;
import com.netease.nim.uikit.impl.cache.NimUserInfoCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;

import org.jetbrains.annotations.NotNull;

import team.one.lwe.LWECache;
import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.config.Preferences;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.activity.LoginActivity;
import team.one.lwe.ui.wedget.LWEToolBarOptions;
import team.one.lwe.util.APIUtils;
import team.one.lwe.util.TextUtils;

public class SettingActivity extends LWEUI {

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
        ImageButton buttonLogout = findViewById(R.id.buttonLogout);
        ImageButton buttonUpdatePassword = findViewById(R.id.buttonUpdatePassword);
        ImageButton buttonConfirmPassword = findViewById(R.id.buttonConfirmPassword);
        RelativeLayout passwordLayout = findViewById(R.id.passwordLayout);
        RelativeLayout confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);

        buttonLogout.setOnClickListener(view -> {
            NIMClient.getService(AuthService.class).logout();
            LWECache.clear();
            Preferences.cleanCache(this);
            Intent intent = new Intent();
            intent.setClass(SettingActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        buttonUpdatePassword.setOnClickListener(view -> {
            passwordLayout.setVisibility(View.VISIBLE);
            confirmPasswordLayout.setVisibility(View.VISIBLE);
        });

        buttonConfirmPassword.setOnClickListener(view -> {
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
                passwordLayout.setVisibility(View.GONE);
                confirmPasswordLayout.setVisibility(View.GONE);
            }
        });

        editTextConfirmPassword.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                buttonConfirmPassword.callOnClick();
            }
            return false;
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
