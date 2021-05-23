package team.one.lwe.ui.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import team.one.lwe.R;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.wedget.LWEToolBarOptions;

public class UserSettingActivity extends LWEUI {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_user);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_user_setting, true);
        setToolBar(R.id.toolbar, options);

        ImageButton buttonUpdatePassword = findViewById(R.id.buttonUpdatePassword);

        buttonUpdatePassword.setOnClickListener(view1 -> startActivity(new Intent(this, UpdatePasswordActivity.class)));
    }
}
