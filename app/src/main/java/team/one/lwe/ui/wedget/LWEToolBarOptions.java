package team.one.lwe.ui.wedget;

import com.netease.nim.uikit.common.activity.ToolBarOptions;

import team.one.lwe.R;

public class LWEToolBarOptions extends ToolBarOptions {
    public LWEToolBarOptions(int id,boolean isneednavigate) {
        logoId = R.drawable.lwe_logo_icon_toolbar;
        navigateId = com.netease.nim.uikit.R.drawable.nim_actionbar_dark_back_icon;
        isNeedNavigate = isneednavigate;
        titleId = id;
    }
}
