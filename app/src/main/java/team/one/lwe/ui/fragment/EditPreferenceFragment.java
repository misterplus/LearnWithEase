package team.one.lwe.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class EditPreferenceFragment extends Fragment {

    private View view;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_preference, container, false);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_edit_prefernce, true);
        BottomNavigationView navibar = getActivity().findViewById(R.id.navibar);
        navibar.setVisibility(View.GONE);
        ((UI) getActivity()).setToolBar(getActivity().findViewById(R.id.toolbar), R.id.toolbar, options);

        Spinner spinnerTimeStudy = view.findViewById(R.id.spinnerTimeStudy);
        Spinner spinnerTimeRest = view.findViewById(R.id.spinnerTimeRest);
        Spinner spinnerContentStudy = view.findViewById(R.id.spinnerContentStudy);
        SwitchMaterial switchSameCity = view.findViewById(R.id.switchSameCity);
        SwitchMaterial switchSameSchool = view.findViewById(R.id.switchSameSchool);
        SwitchMaterial switchSameGender = view.findViewById(R.id.switchSameGender);
        SwitchMaterial switchNewRoomFirst = view.findViewById(R.id.switchNewRoomFirst);

        String[] timeValues = getResources().getStringArray(R.array.lwe_spinner_time_study);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(getContext(), R.layout.lwe_spinner_item, timeValues);
        adapter4.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerTimeStudy.setAdapter(adapter4);

        String[] restValues = getResources().getStringArray(R.array.lwe_spinner_time_rest);
        ArrayAdapter<String> adapter5 = new ArrayAdapter<>(getContext(), R.layout.lwe_spinner_item, restValues);
        adapter5.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerTimeRest.setAdapter(adapter5);

        String[] contentValues = getResources().getStringArray(R.array.lwe_spinner_content_study);
        ArrayAdapter<String> adapter6 = new ArrayAdapter<>(getContext(), R.layout.lwe_spinner_item, contentValues);
        adapter6.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerContentStudy.setAdapter(adapter6);

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
                if (!NetworkUtil.isNetAvailable(getActivity())) {
                    ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
                } else {
                    pref.setTimeStudy(i);
                    userExtension.setPref(pref);
                    UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback<>(view));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerTimeRest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!NetworkUtil.isNetAvailable(getActivity())) {
                    ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
                } else {
                    pref.setTimeRest(i);
                    userExtension.setPref(pref);
                    UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback<>(view));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerContentStudy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!NetworkUtil.isNetAvailable(getActivity())) {
                    ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
                } else {
                    pref.setContentStudy(i);
                    userExtension.setPref(pref);
                    UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback<>(view));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        switchSameCity.setOnClickListener(view -> {
            if (!NetworkUtil.isNetAvailable(getActivity())) {
                ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
            } else {
                pref.setSameCity(!pref.isSameCity());
                userExtension.setPref(pref);
                UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback<>(view));
            }
        });

        switchSameSchool.setOnClickListener(view -> {
            if (!NetworkUtil.isNetAvailable(getActivity())) {
                ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
            } else {
                pref.setSameSchool(!pref.isSameSchool());
                userExtension.setPref(pref);
                UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback<>(view));
            }
        });

        switchSameGender.setOnClickListener(view -> {
            if (!NetworkUtil.isNetAvailable(getActivity())) {
                ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
            } else {
                pref.setSameGender(!pref.isSameGender());
                userExtension.setPref(pref);
                UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback<>(view));
            }
        });

        switchNewRoomFirst.setOnClickListener(view -> {
            if (!NetworkUtil.isNetAvailable(getActivity())) {
                ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
            } else {
                pref.setNewRoomFirst(!pref.isNewRoomFirst());
                userExtension.setPref(pref);
                UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback<>(view));
            }
        });

        return view;
    }
}