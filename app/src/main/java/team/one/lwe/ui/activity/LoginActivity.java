package team.one.lwe.ui.activity;

import android.Manifest;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.netease.nim.uikit.common.activity.UI;

import team.one.lwe.R;
import team.one.lwe.ui.fragment.LoginFragment;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class LoginActivity extends UI {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //TODO: handle failed requests
        if (ActivityCompat.checkSelfPermission(this, "android.permission.CAMERA") == PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
    }
}