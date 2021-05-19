package team.one.lwe.ui.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import team.one.lwe.R;
import team.one.lwe.bean.Preference;
import team.one.lwe.bean.UserInfo;
import team.one.lwe.ui.callback.UpdateCallback;
import team.one.lwe.ui.wedget.LWEToolBarOptions;
import team.one.lwe.util.UserUtils;

public class EditPrefActivity extends UI {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pref);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_edit_prefernce, true);
        setToolBar(R.id.toolbar, options);

        Spinner spinnerTimeStudy = findViewById(R.id.spinnerTimeStudy);
        Spinner spinnerTimeRest = findViewById(R.id.spinnerTimeRest);
        Spinner spinnerContentStudy = findViewById(R.id.spinnerContentStudy);
        SwitchMaterial switchSameCity = findViewById(R.id.switchSameCity);
        SwitchMaterial switchSameSchool = findViewById(R.id.switchSameSchool);
        SwitchMaterial switchSameGender = findViewById(R.id.switchSameGender);
        SwitchMaterial switchNewRoomFirst = findViewById(R.id.switchNewRoomFirst);

        String[] timeValues = getResources().getStringArray(R.array.lwe_spinner_time_study);
        ArrayAdapter<String> a = new ArrayAdapter<>(this, R.layout.lwe_spinner_item, timeValues);
        a.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerTimeStudy.setAdapter(a);

        String[] restValues = getResources().getStringArray(R.array.lwe_spinner_time_rest);
        ArrayAdapter<String> a2 = new ArrayAdapter<>(this, R.layout.lwe_spinner_item, restValues);
        a2.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerTimeRest.setAdapter(a2);

        String[] contentValues = getResources().getStringArray(R.array.lwe_spinner_content_study);
        ArrayAdapter<String> a3 = new ArrayAdapter<>(this, R.layout.lwe_spinner_item, contentValues);
        a3.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerContentStudy.setAdapter(a3);

        String account = NimUIKit.getAccount();
        NimUserInfo user = NIMClient.getService(UserService.class).getUserInfo(account);
        UserInfo userExtension = new Gson().fromJson(user.getExtension(), UserInfo.class);
        Preference pref = userExtension.getPref();
        spinnerTimeStudy.setSelection(pref.getTimeStudy(), false);
        spinnerTimeRest.setSelection(pref.getTimeRest(), false);
        spinnerContentStudy.setSelection(pref.getContentStudy(), false);
        switchSameCity.setChecked(pref.isSameCity());
        switchSameSchool.setChecked(pref.isSameSchool());
        switchSameGender.setChecked(pref.isSameGender());
        switchNewRoomFirst.setChecked(pref.isNewRoomFirst());

        spinnerTimeStudy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!NetworkUtil.isNetAvailable(getBaseContext())) {
                    ToastHelper.showToast(getBaseContext(), R.string.lwe_error_nonetwork);
                } else {
                    pref.setTimeStudy(i);
                    userExtension.setPref(pref);
                    UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback(getBaseContext(), "study time"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerTimeRest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!NetworkUtil.isNetAvailable(getBaseContext())) {
                    ToastHelper.showToast(getBaseContext(), R.string.lwe_error_nonetwork);
                } else {
                    pref.setTimeRest(i);
                    userExtension.setPref(pref);
                    UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback(getBaseContext(), "rest time"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerContentStudy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!NetworkUtil.isNetAvailable(getBaseContext())) {
                    ToastHelper.showToast(getBaseContext(), R.string.lwe_error_nonetwork);
                } else {
                    pref.setContentStudy(i);
                    userExtension.setPref(pref);
                    UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback(getBaseContext(), "study content"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        switchSameCity.setOnClickListener(view -> {
            if (!NetworkUtil.isNetAvailable(this)) {
                ToastHelper.showToast(getBaseContext(), R.string.lwe_error_nonetwork);
            } else {
                pref.setSameCity(!pref.isSameCity());
                userExtension.setPref(pref);
                UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback(getBaseContext(), "same city"));
            }
        });

        switchSameSchool.setOnClickListener(view -> {
            if (!NetworkUtil.isNetAvailable(this)) {
                ToastHelper.showToast(getBaseContext(), R.string.lwe_error_nonetwork);
            } else {
                pref.setSameSchool(!pref.isSameSchool());
                userExtension.setPref(pref);
                UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback(getBaseContext(), "same school"));
            }
        });

        switchSameGender.setOnClickListener(view -> {
            if (!NetworkUtil.isNetAvailable(this)) {
                ToastHelper.showToast(getBaseContext(), R.string.lwe_error_nonetwork);
            } else {
                pref.setSameGender(!pref.isSameGender());
                userExtension.setPref(pref);
                UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback(getBaseContext(), "same gender"));
            }
        });

        switchNewRoomFirst.setOnClickListener(view -> {
            if (!NetworkUtil.isNetAvailable(this)) {
                ToastHelper.showToast(getBaseContext(), R.string.lwe_error_nonetwork);
            } else {
                pref.setNewRoomFirst(!pref.isNewRoomFirst());
                userExtension.setPref(pref);
                UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback(getBaseContext(), "new room first"));
            }
        });
    }
}