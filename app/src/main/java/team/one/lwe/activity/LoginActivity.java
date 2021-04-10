package team.one.lwe.activity;

import android.app.Activity;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import team.one.lwe.config.Preferences;

public class LoginActivity extends Activity {



    public void doLogin(String account, String token) {
        LoginInfo info = new LoginInfo(account, token);
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
