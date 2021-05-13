package team.one.lwe.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citypickerview.CityPickerView;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;


import java.util.HashMap;
import java.util.Map;

import team.one.lwe.R;
import team.one.lwe.bean.UserInfo;
import team.one.lwe.ui.wedget.LWEToolBarOptions;

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
        UserInfo userExtension = new Gson().fromJson(user.getExtension(), UserInfo.class);
        int gender = user.getGenderEnum().getValue();
        editNickname.setText(user.getName());
        textPersonalSignature.setText(user.getSignature());
        editTextAge.setText(String.valueOf(userExtension.getAge()));

        nicknameArrow.setOnClickListener(v -> {
            editNickname.setFocusable(true);
            editNickname.setFocusableInTouchMode(true);
            editNickname.requestFocus();
            editNickname.findFocus();
            editNickname.setTextColor(Color.GRAY);
        });

        signatureArrow.setOnClickListener(v -> {
            textPersonalSignature.setFocusable(true);
            textPersonalSignature.setFocusableInTouchMode(true);
            textPersonalSignature.requestFocus();
            textPersonalSignature.findFocus();
            textPersonalSignature.setTextColor(Color.GRAY);
        });

        ageArrow.setOnClickListener(v -> {
            editTextAge.setFocusable(true);
            editTextAge.setFocusableInTouchMode(true);
            editTextAge.requestFocus();
            editTextAge.findFocus();
            editTextAge.setTextColor(Color.GRAY);
        });

        editNickname.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String nickname = editNickname.getText().toString();
                Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
                fields.put(UserInfoFieldEnum.Name, nickname);
                NIMClient.getService(UserService.class).updateUserInfo(fields);
                editNickname.setFocusable(false);
                editNickname.setTextColor(Color.BLACK);
                editNickname.clearFocus();
            }
        });

        textPersonalSignature.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String signature = textPersonalSignature.getText().toString();
                Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
                fields.put(UserInfoFieldEnum.SIGNATURE, signature);
                NIMClient.getService(UserService.class).updateUserInfo(fields);
                textPersonalSignature.setFocusable(false);
                textPersonalSignature.setTextColor(Color.BLACK);
                textPersonalSignature.clearFocus();
            }
        });

        editTextAge.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                int newage = Integer.parseInt(editTextAge.getText().toString());
                userExtension.setAge(newage);
                String extension = new Gson().toJson(userExtension);
                Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
                fields.put(UserInfoFieldEnum.EXTEND, extension);
                NIMClient.getService(UserService.class).updateUserInfo(fields);
                editTextAge.setFocusable(false);
                editTextAge.setTextColor(Color.BLACK);
                editTextAge.clearFocus();
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

        String text;
        if (userExtension.getProvince().equals(userExtension.getCity()))
            text = String.format(getString(R.string.lwe_placeholder_cityformat2), userExtension.getProvince(), userExtension.getArea());
        else
            text = String.format(getString(R.string.lwe_placeholder_cityformat3), userExtension.getProvince(), userExtension.getCity(), userExtension.getArea());
        if (text.length() >= 16) {
            textCityPicker.setVisibility(View.INVISIBLE);
            textCity.setTextScaleX((float) (16.0 / text.length()));
        } else {
            textCityPicker.setVisibility(View.VISIBLE);
            textCity.setTextScaleX((float) 1.0);
        }
        textCity.setText(text);

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
                userExtension.setSchool(school);
                String extension = new Gson().toJson(userExtension);
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

        String[] gradeValues;
        switch (userExtension.getBak()) {
            case 0: {
                gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_0);
                break;
            }
            case 1: {
                gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_1);
                break;
            }
            case 2: {
                gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_2);
                break;
            }
            case 3: {
                gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_3);
                break;
            }
            case 4: {
                gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_4);
                break;
            }
            case 5: {
                gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_5);
                break;
            }
            case 6: {
                gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_6);
                break;
            }
            case 7: {
                gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_7);
                break;
            }
            default: {
                gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_0);
            }
        }
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(getContext(), R.layout.lwe_spinner_item, gradeValues);
        adapter3.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerGrade.setAdapter(adapter3);

        spinnerEdu.setSelection(userExtension.getBak(), true);
        spinnerGrade.setSelection(userExtension.getGrade(), true);
        if (userExtension.getBak() > 3) {
            rowSchool.setVisibility(View.VISIBLE);
            for (int i = 0; i < schoolValues.length; i++)
                if (schoolValues[i].equals(userExtension.getSchool())) {
                    spinnerSchool.setSelection(i, true);
                    break;
                }
        }

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

                userExtension.setBak(i);
                String extension = new Gson().toJson(userExtension);
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
                    userExtension.setSchool("");
                    extension = new Gson().toJson(userExtension);
                    fields = new HashMap<>(1);
                    fields.put(UserInfoFieldEnum.EXTEND, extension);
                    NIMClient.getService(UserService.class).updateUserInfo(fields);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                String[] gradeValues;
                switch (userExtension.getBak()) {
                    case 0: {
                        gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_0);
                        break;
                    }
                    case 1: {
                        gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_1);
                        break;
                    }
                    case 2: {
                        gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_2);
                        break;
                    }
                    case 3: {
                        gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_3);
                        break;
                    }
                    case 4: {
                        gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_4);
                        break;
                    }
                    case 5: {
                        gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_5);
                        break;
                    }
                    case 6: {
                        gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_6);
                        break;
                    }
                    case 7: {
                        gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_7);
                        break;
                    }
                    default: {
                        gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_0);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.lwe_spinner_item, gradeValues);
                adapter.setDropDownViewResource(R.layout.lwe_spinner_item);
                spinnerGrade.setAdapter(adapter);
            }
        });

        spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                userExtension.setGrade(i);
                String extension = new Gson().toJson(userExtension);
                Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
                fields.put(UserInfoFieldEnum.EXTEND, extension);
                NIMClient.getService(UserService.class).updateUserInfo(fields);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                cPickerNames[0] = province.getName();
                cPickerNames[1] = city.getName();
                cPickerNames[2] = district.getName();
                String text;
                if (province.getName().equals(city.getName()))
                    text = String.format(getString(R.string.lwe_placeholder_cityformat2), province.getName(), district.getName());
                else
                    text = String.format(getString(R.string.lwe_placeholder_cityformat3), province.getName(), city.getName(), district.getName());
                if (text.length() >= 16) {
                    textCityPicker.setVisibility(View.INVISIBLE);
                    textCity.setTextScaleX((float) (16.0 / text.length()));
                } else {
                    textCityPicker.setVisibility(View.VISIBLE);
                    textCity.setTextScaleX((float) 1.0);
                }
                textCity.setText(text);

                userExtension.setProvince(cPickerNames[0]);
                userExtension.setCity(cPickerNames[1]);
                userExtension.setArea(cPickerNames[2]);
                String extension = new Gson().toJson(userExtension);
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
