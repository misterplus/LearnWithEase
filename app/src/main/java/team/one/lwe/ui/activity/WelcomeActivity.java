package team.one.lwe.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import team.one.lwe.LWECache;
import team.one.lwe.R;
import team.one.lwe.ui.activity.auth.LoginActivity;

public class WelcomeActivity extends LWEUI {

    private static final boolean DEV_FRONT = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        // redirect to login in 3 seconds
        new Handler(msg -> {
            Intent intent;
            if (LWECache.noCache()) {
                // redirect to main if is developing frontend
                intent = new Intent(this, DEV_FRONT ? MainActivity.class : LoginActivity.class);
            } else {
                intent = new Intent(this, MainActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }).sendEmptyMessageDelayed(0, 3000);
    }
}
