package team.one.lwe.ui.activity;

import android.os.Bundle;

import com.netease.nim.uikit.common.activity.UI;

import team.one.lwe.crash.ActivityCollector;

public abstract class LWEUI extends UI {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
