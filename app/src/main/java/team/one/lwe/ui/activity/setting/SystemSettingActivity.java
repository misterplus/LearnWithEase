package team.one.lwe.ui.activity.setting;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.switchmaterial.SwitchMaterial;

import team.one.lwe.R;
import team.one.lwe.config.Preferences;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.wedget.LWEToolBarOptions;

public class SystemSettingActivity extends LWEUI {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_system);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_system_setting, true);
        setToolBar(R.id.toolbar, options);

        RadioGroup radioDefaultImageQuality = findViewById(R.id.radioDefaultImageQuality);
        RadioButton radioImageQualityHigh = findViewById(R.id.radioImageQualityHigh);
        RadioButton radioImageQualityMiddle = findViewById(R.id.radioImageQualityMiddle);
        RadioButton radioImageQualityLow = findViewById(R.id.radioImageQualityLow);
        RadioGroup radioDefaultVideoQuality = findViewById(R.id.radioDefaultVideoQuality);
        RadioButton radioVideoQualityHigh = findViewById(R.id.radioVideoQualityHigh);
        RadioButton radioVideoQualityLow = findViewById(R.id.radioVideoQualityLow);
        SwitchMaterial switchMessageRemending = findViewById(R.id.switchMessageRemending);
        SwitchMaterial switchGravityInduction = findViewById(R.id.switchGravityInduction);
        ImageButton buttonPermissionSetting = findViewById(R.id.buttonPermissionSetting);
        ImageButton buttonClearCache = findViewById(R.id.buttonClearCache);
        ImageButton buttonUserAgreement = findViewById(R.id.buttonUserAgreement);

        int imageQuality;
        int videoQuality = Preferences.getVideoQuality();

        if (videoQuality == 1)
            radioVideoQualityHigh.setChecked(true);
        else
            radioVideoQualityLow.setChecked(true);

        radioDefaultVideoQuality.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioVideoQualityLow: {
                    Preferences.setVideoQuality(1);
                    break;
                }
                default: {
                    Preferences.setVideoQuality(0);
                }
            }
        });
    }
}
