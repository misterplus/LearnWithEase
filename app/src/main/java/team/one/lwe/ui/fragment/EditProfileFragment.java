package team.one.lwe.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citypickerview.CityPickerView;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;


import java.util.HashMap;
import java.util.Map;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.bean.Preference;
import team.one.lwe.bean.User;
import team.one.lwe.bean.UserInfo;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.wedget.LWEToolBarOptions;
import team.one.lwe.util.APIUtils;
import team.one.lwe.util.NavigationUtils;
import team.one.lwe.util.TextUtils;

public class EditProfileFragment extends Fragment {

    private final CityPickerView cPicker = new CityPickerView();
    private final String[] cPickerNames = new String[3];
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO: finish mine fragment
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_edit_profile, true);
        BottomNavigationView navibar = getActivity().findViewById(R.id.navibar);
        navibar.setVisibility(View.GONE);
        ((UI) getActivity()).setToolBar(getActivity().findViewById(R.id.toolbar), R.id.toolbar, options);
        CityConfig cityConfig = new CityConfig.Builder()
                .confirTextColor("#0887FD")
                .build();
        cPicker.setConfig(cityConfig);
        cPicker.init(this.getContext());
        EditText editNickname = view.findViewById(R.id.nickname);
        EditText textPersonalSignature = view.findViewById(R.id.PersonalSignature);
        EditText editTextAge = view.findViewById(R.id.editTextAge);
        ImageButton nicknameArrow = view.findViewById(R.id.nicknameArrow);
        ImageButton signatureArrow = view.findViewById(R.id.signatureArrow);
        ImageButton ageArrow = view.findViewById(R.id.ageArrow);

        String account = NimUIKit.getAccount();
        NimUserInfo user = NIMClient.getService(UserService.class).getUserInfo(account);
        String json = user.getExtension();
        Map userExtension = JSON.parseObject(json, Map.class);
        int gender = user.getGenderEnum().getValue();
        editNickname.setText(user.getName());
        editNickname.setFocusable(true);
        editNickname.setInputType(InputType.TYPE_NULL);
        textPersonalSignature.setText(user.getSignature());
        textPersonalSignature.setFocusable(true);
        textPersonalSignature.setInputType(InputType.TYPE_NULL);
        editTextAge.setText(userExtension.get("age").toString());
        editTextAge.setFocusable(true);
        editTextAge.setInputType(InputType.TYPE_NULL);

        nicknameArrow.setOnClickListener(v -> {
            editNickname.requestFocus();
            editNickname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
            editNickname.setTextColor(Color.BLUE);
        });

        signatureArrow.setOnClickListener(v -> {
            textPersonalSignature.requestFocus();
            textPersonalSignature.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
            textPersonalSignature.setTextColor(Color.BLUE);
        });

        ageArrow.setOnClickListener(v -> {
            editTextAge.requestFocus();
            editTextAge.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
            editTextAge.setTextColor(Color.BLUE);
        });

        editNickname.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String nickname = editNickname.getText().toString();
                Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
                fields.put(UserInfoFieldEnum.Name, nickname);
                NIMClient.getService(UserService.class).updateUserInfo(fields);
                editNickname.setInputType(InputType.TYPE_NULL);
                editNickname.setTextColor(Color.BLACK);
            }
        });

        textPersonalSignature.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String signature = textPersonalSignature.getText().toString();
                Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
                fields.put(UserInfoFieldEnum.SIGNATURE, signature);
                NIMClient.getService(UserService.class).updateUserInfo(fields);
            }
        });

        editTextAge.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                String newage = editTextAge.getText().toString();
                userExtension.replace("age", newage);
                JSONObject jsonObject = new JSONObject(userExtension);
                String extension = jsonObject.toString();
                Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
                fields.put(UserInfoFieldEnum.EXTEND, extension);
                NIMClient.getService(UserService.class).updateUserInfo(fields);
            }
        });

        RadioGroup groupGender = view.findViewById(R.id.groupGender);
        RadioButton femaleButton = view.findViewById(R.id.radioFemale);
        RadioButton maleButton = view.findViewById(R.id.radioMale);
        RadioButton unknownButton = view.findViewById(R.id.radioUnknown);

        try {
            switch (gender) {
                case 2: {
                    femaleButton.setChecked(true);
                    break;
                }
                case 1: {
                    maleButton.setChecked(true);
                    break;
                }
                default: {
                    unknownButton.setChecked(true);
                }
            }
        } catch (Exception e) {
            DialogMaker.dismissProgressDialog();
        }

        groupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            int gender;

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (femaleButton.getId() == checkedId) {
                    gender = 2;
                } else if (maleButton.getId() == checkedId) {
                    gender = 1;
                } else if (unknownButton.getId() == checkedId) {
                    gender = 0;
                }
                Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
                fields.put(UserInfoFieldEnum.GENDER, gender);
                NIMClient.getService(UserService.class).updateUserInfo(fields);
            }
        });

        TextView textCityPicker = view.findViewById(R.id.textCityPicker);
        TextView textCity = view.findViewById(R.id.textCity);
        TextView textSchool = view.findViewById(R.id.textSchool);
        TextView textSchoolPicker = view.findViewById(R.id.textSchoolPicker);
        ImageButton buttonCity = view.findViewById(R.id.buttonCity);
        Spinner spinnerEdu = view.findViewById(R.id.spinnerEdu);
        SearchableSpinner spinnerSchool = view.findViewById(R.id.spinnerSchool);
        Spinner spinnerGrade = view.findViewById(R.id.spinnerGrade);
        RelativeLayout rowSchool = view.findViewById(R.id.rowSchool);

        String[] eduValues = getResources().getStringArray(R.array.lwe_spinner_edu);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.lwe_spinner_item, eduValues);
        adapter.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerEdu.setAdapter(adapter);

        String[] schoolValues = getResources().getStringArray(R.array.lwe_spinner_school);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getContext(), R.layout.lwe_spinner_item_invisible, schoolValues);
        adapter2.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerSchool.setAdapter(adapter2);
        spinnerSchool.setTitle(getString(R.string.lwe_placeholder_school));
        spinnerSchool.setPositiveButton(getString(R.string.lwe_hint_confirm));
        spinnerSchool.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String school = (String) spinnerSchool.getSelectedItem();
                textSchool.setText(school);
                if (school.length() >= 16) {
                    textSchoolPicker.setVisibility(View.INVISIBLE);
                    textSchool.setTextScaleX((float) (16.0 / school.length()));
                } else {
                    textSchoolPicker.setVisibility(View.VISIBLE);
                    textSchool.setTextScaleX((float) 1.0);
                }
                userExtension.replace("school", school);
                JSONObject jsonObject = new JSONObject(userExtension);
                String extension = jsonObject.toString();
                Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
                fields.put(UserInfoFieldEnum.EXTEND, extension);
                NIMClient.getService(UserService.class).updateUserInfo(fields);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                textSchoolPicker.setVisibility(View.VISIBLE);
                textSchool.setText(getString(R.string.lwe_placeholder_school));
            }
        });

        String[] gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_0);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(getContext(), R.layout.lwe_spinner_item, gradeValues);
        adapter3.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerGrade.setAdapter(adapter3);

        spinnerEdu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] values;
                switch (i) {
                    case 0: {
                        values = getResources().getStringArray(R.array.lwe_spinner_grade_0);
                        break;
                    }
                    case 1: {
                        values = getResources().getStringArray(R.array.lwe_spinner_grade_1);
                        break;
                    }
                    case 2: {
                        values = getResources().getStringArray(R.array.lwe_spinner_grade_2);
                        break;
                    }
                    case 3: {
                        values = getResources().getStringArray(R.array.lwe_spinner_grade_3);
                        break;
                    }
                    case 4: {
                        values = getResources().getStringArray(R.array.lwe_spinner_grade_4);
                        break;
                    }
                    case 5: {
                        values = getResources().getStringArray(R.array.lwe_spinner_grade_5);
                        break;
                    }
                    case 6: {
                        values = getResources().getStringArray(R.array.lwe_spinner_grade_6);
                        break;
                    }
                    case 7: {
                        values = getResources().getStringArray(R.array.lwe_spinner_grade_7);
                        break;
                    }
                    default: {
                        values = getResources().getStringArray(R.array.lwe_spinner_grade_0);
                    }
                }

                userExtension.replace("bak", eduValues[i]);
                JSONObject jsonObject = new JSONObject(userExtension);
                String extension = jsonObject.toString();
                Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
                fields.put(UserInfoFieldEnum.EXTEND, extension);

                NIMClient.getService(UserService.class).updateUserInfo(fields);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), R.layout.lwe_spinner_item, values);
                adapter.setDropDownViewResource(R.layout.lwe_spinner_item);
                spinnerGrade.setAdapter(adapter);

                if (i > 3) {
                    rowSchool.setVisibility(View.VISIBLE);
                } else {
                    rowSchool.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                String[] gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_0);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.lwe_spinner_item, gradeValues);
                adapter.setDropDownViewResource(R.layout.lwe_spinner_item);
                spinnerGrade.setAdapter(adapter);
            }
        });

        spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                userExtension.replace("grade", gradeValues[i]);
                JSONObject jsonObject = new JSONObject(userExtension);
                String extension = jsonObject.toString();
                Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
                fields.put(UserInfoFieldEnum.EXTEND, extension);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                userExtension.replace("grade", gradeValues[0]);
                JSONObject jsonObject = new JSONObject(userExtension);
                String extension = jsonObject.toString();
                Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
                fields.put(UserInfoFieldEnum.EXTEND, extension);
            }
        });

        cPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                //set ids
                cPickerNames[0] = province.getName();
                cPickerNames[1] = city.getName();
                cPickerNames[2] = district.getName();
                //set views
                String text;
                if (province.getName().equals(city.getName()))
                    text = String.format(getString(R.string.lwe_placeholder_cityformat2), province.getName(), district.getName());
                else
                    text = String.format(getString(R.string.lwe_placeholder_cityformat3), province.getName(), city.getName(), district.getName());
                //scales horizontally
                if (text.length() >= 16) {
                    textCityPicker.setVisibility(View.INVISIBLE);
                    textCity.setTextScaleX((float) (16.0 / text.length()));
                } else {
                    textCityPicker.setVisibility(View.VISIBLE);
                    textCity.setTextScaleX((float) 1.0);
                }
                textCity.setText(text);

                userExtension.replace("province", cPickerNames[0]);
                userExtension.replace("city", cPickerNames[1]);
                userExtension.replace("area", cPickerNames[2]);
                JSONObject jsonObject = new JSONObject(userExtension);
                String extension = jsonObject.toString();
                Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
                fields.put(UserInfoFieldEnum.EXTEND, extension);
                NIMClient.getService(UserService.class).updateUserInfo(fields);
            }
        });
        buttonCity.setOnClickListener(view -> cPicker.showCityPicker());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().findViewById(R.id.navibar).setVisibility(View.VISIBLE);
    }
}
