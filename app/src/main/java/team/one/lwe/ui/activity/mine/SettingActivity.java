package team.one.lwe.ui.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;

import team.one.lwe.LWECache;
import team.one.lwe.R;
import team.one.lwe.config.Preferences;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.activity.auth.LoginActivity;
import team.one.lwe.ui.wedget.LWEToolBarOptions;

public class SettingActivity extends LWEUI {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_setting, true);
        setToolBar(R.id.toolbar, options);

        Button buttonLogout = findViewById(R.id.buttonLogout);
        ImageButton buttonUpdatePassword = findViewById(R.id.buttonUpdatePassword);

        buttonLogout.setOnClickListener(view -> {
            NIMClient.getService(AuthService.class).logout();
            LWECache.clear();
            Preferences.cleanCache(this);
            Intent intent = new Intent();
            intent.setClass(SettingActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        buttonUpdatePassword.setOnClickListener(view1 -> startActivity(new Intent(this, UpdatePasswordActivity.class)));
    }
}
