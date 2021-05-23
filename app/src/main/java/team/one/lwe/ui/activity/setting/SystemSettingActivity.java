package team.one.lwe.ui.activity.setting;

import android.os.Bundle;

import team.one.lwe.R;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.wedget.LWEToolBarOptions;

public class SystemSettingActivity extends LWEUI {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_system);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_system_setting, true);
        setToolBar(R.id.toolbar, options);
    }
}
