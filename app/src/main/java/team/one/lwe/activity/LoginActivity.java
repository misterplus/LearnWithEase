package team.one.lwe.activity;

import android.os.Bundle;
import android.util.Log;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.activity.UI;

import team.one.lwe.LWECache;
import team.one.lwe.R;
import team.one.lwe.fragment.LoginFragment;

public class LoginActivity extends UI {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null && LWECache.noCache()) {
            Log.i(this.getClass().getSimpleName(), "haven't logged in, go to login");
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        } else {
            Log.i(this.getClass().getSimpleName(), "already logged in, go to main page");
            NimUIKit.startP2PSession(this, "plus_dev");
        }
    }
}