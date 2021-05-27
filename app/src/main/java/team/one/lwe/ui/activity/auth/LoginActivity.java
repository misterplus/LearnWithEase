package team.one.lwe.ui.activity.auth;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nimlib.sdk.auth.LoginInfo;

import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.config.Preferences;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.activity.MainActivity;
import team.one.lwe.ui.callback.RegularCallback;
import team.one.lwe.util.APIUtils;
import team.one.lwe.util.UserUtils;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class LoginActivity extends LWEUI {

    private EditText editTextUsername, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (ActivityCompat.checkSelfPermission(this, "android.permission.CAMERA") == PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(view1 -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            if (!UserUtils.isUsernameValid(username) || !UserUtils.isPasswordValid(password)) {
                ToastHelper.showToast(this, R.string.lwe_error_login_format);
            } else {
                DialogMaker.showProgressDialog(this, getString(R.string.lwe_progress_login), false);
                doConvert(username, password);
            }
        });
        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(view1 -> startActivityForResult(new Intent(getBaseContext(), RegisterActivity.class), 0));
        editTextPassword.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                buttonLogin.callOnClick();
            }
            return false;
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == 1) {
                ToastHelper.showToast(this, R.string.lwe_success_register);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults[0] == PERMISSION_DENIED) {
                    ToastHelper.showToast(this, R.string.lwe_error_perm);
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
                }
                break;
            }
            default:
                break;
        }
    }

    private void doConvert(String username, String password) {
        new NetworkThread(editTextUsername) {
            @Override
            public ASResponse doRequest() {
                return APIUtils.convert(username, password);
            }

            @Override
            public void onSuccess(ASResponse asp) {
                doLogin(UserUtils.getLoginInfo(asp.getInfo()));
            }

            @Override
            public void onFailed(int code, String desc) {
                DialogMaker.dismissProgressDialog();
                super.onFailed(code, desc);
            }

            @Override
            public void onException(Exception e) {
                DialogMaker.dismissProgressDialog();
                super.onException(e);
            }
        }.start();
    }

    private void doLogin(LoginInfo info) {
        NimUIKit.login(info, new RegularCallback<LoginInfo>(getBaseContext()) {
            @Override
            public void onSuccess(LoginInfo info) {
                Log.i(this.getClass().getSimpleName(), "login success");
                DialogMaker.dismissProgressDialog();
                startActivity(new Intent(getBaseContext(), MainActivity.class));
                Preferences.saveUserAccount(getApplicationContext(), info.getAccount());
                Preferences.saveUserToken(getApplicationContext(), info.getToken());
            }

            @Override
            public void onFailed(int code) {
                DialogMaker.dismissProgressDialog();
                if (code == 302)
                    ToastHelper.showToast(getBaseContext(), R.string.lwe_error_login_info);
                super.onFailed(code);
            }

            @Override
            public void onException(Throwable e) {
                DialogMaker.dismissProgressDialog();
                super.onException(e);
            }
        });
    }
}