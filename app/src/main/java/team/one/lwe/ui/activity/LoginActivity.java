package team.one.lwe.ui.activity;

import android.os.Bundle;

import com.netease.nim.uikit.common.activity.UI;

import team.one.lwe.R;
import team.one.lwe.ui.fragment.LoginFragment;

public class LoginActivity extends UI {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
    }
}