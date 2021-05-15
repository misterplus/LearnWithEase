package team.one.lwe.ui.fragment;

import android.annotation.SuppressLint;
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
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import team.one.lwe.R;
import team.one.lwe.bean.UserInfo;
import team.one.lwe.ui.callback.UpdateCallback;
import team.one.lwe.ui.listener.TextLockListener;
import team.one.lwe.ui.wedget.LWEToolBarOptions;
import team.one.lwe.util.UserUtils;

public class EditProfileFragment extends Fragment {

    private final CityPickerView cPicker = new CityPickerView();
    private final String[] cPickerNames = new String[3];
    private View view;

    @SuppressLint("NonConstantResourceId")
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
        EditText editTextNickname = view.findViewById(R.id.nickname);
        EditText editTextPersonalSignature = view.findViewById(R.id.PersonalSignature);
        EditText editTextAge = view.findViewById(R.id.editTextAge);
        ImageButton nicknameArrow = view.findViewById(R.id.nicknameArrow);
        ImageButton signatureArrow = view.findViewById(R.id.signatureArrow);
        ImageButton ageArrow = view.findViewById(R.id.ageArrow);

        String account = NimUIKit.getAccount();
        NimUserInfo user = NIMClient.getService(UserService.class).getUserInfo(account);
        UserInfo userExtension = new Gson().fromJson(user.getExtension(), UserInfo.class);
        int gender = user.getGenderEnum().getValue();
        editTextNickname.setText(user.getName());
        editTextPersonalSignature.setText(user.getSignature());
        editTextAge.setText(String.valueOf(userExtension.getAge()));

        nicknameArrow.setOnClickListener(new TextLockListener(editTextNickname));
        signatureArrow.setOnClickListener(new TextLockListener(editTextPersonalSignature));
        ageArrow.setOnClickListener(new TextLockListener(editTextAge));

        editTextNickname.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String nickname = editTextNickname.getText().toString();
                if (nickname.isEmpty() || nickname.length() > 16) {
                    ToastHelper.showToast(view.getContext(), R.string.lwe_error_name);
                } else if (!NetworkUtil.isNetAvailable(getActivity())) {
                    ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
                } else {
                    UserUtils.updateUserNickName(nickname).setCallback(new UpdateCallback<>(view));
                }
                loseFocus(editTextNickname);
            }
        });

        editTextPersonalSignature.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String signature = editTextPersonalSignature.getText().toString();
                if (signature.length() > 20) {
                    ToastHelper.showToast(view.getContext(), R.string.lwe_error_signature);
                } else if (!NetworkUtil.isNetAvailable(getActivity())) {
                    ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
                } else {
                    UserUtils.updateUserSignature(signature).setCallback(new UpdateCallback<>(view));
                }
                loseFocus(editTextPersonalSignature);
            }
        });

        editTextAge.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                int age = Integer.parseInt(editTextAge.getText().toString());
                if (age < 1 || age > 120) {
                    ToastHelper.showToast(view.getContext(), R.string.lwe_error_age);
                } else if (!NetworkUtil.isNetAvailable(getActivity())) {
                    ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
                } else {
                    userExtension.setAge(age);
                    UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback<>(view));
                }
                loseFocus(editTextAge);
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

        groupGender.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioFemale: {
                    if (!NetworkUtil.isNetAvailable(getActivity())) {
                        ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
                    } else {
                        UserUtils.updateUserGender(2).setCallback(new UpdateCallback<>(view));
                    }
                    break;
                }
                case R.id.radioMale: {
                    if (!NetworkUtil.isNetAvailable(getActivity())) {
                        ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
                    } else {
                        UserUtils.updateUserGender(1).setCallback(new UpdateCallback<>(view));
                    }
                    break;
                }
                default: {
                    if (!NetworkUtil.isNetAvailable(getActivity())) {
                        ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
                    } else {
                        UserUtils.updateUserGender(0).setCallback(new UpdateCallback<>(view));
                    }
                }
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
                if (!NetworkUtil.isNetAvailable(getActivity())) {
                    ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
                } else {
                    userExtension.setSchool(school);
                    UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback<>(view));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                textSchoolPicker.setVisibility(View.VISIBLE);
                textSchool.setText(getString(R.string.lwe_placeholder_school));
            }
        });

        String[] gradeValues;
        gradeValues = getGradeValues(userExtension.getBak());

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
                values = getGradeValues(i);

                if (!NetworkUtil.isNetAvailable(getActivity())) {
                    ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
                } else {
                    userExtension.setBak(i);
                    UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback<>(view));
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), R.layout.lwe_spinner_item, values);
                adapter.setDropDownViewResource(R.layout.lwe_spinner_item);
                spinnerGrade.setAdapter(adapter);

                if (i > 3) {
                    rowSchool.setVisibility(View.VISIBLE);
                } else {
                    rowSchool.setVisibility(View.GONE);
                    userExtension.setSchool("");
                    UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback<>(view));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                String[] gradeValues;
                gradeValues = getGradeValues(userExtension.getBak());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.lwe_spinner_item, gradeValues);
                adapter.setDropDownViewResource(R.layout.lwe_spinner_item);
                spinnerGrade.setAdapter(adapter);
            }
        });

        spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!NetworkUtil.isNetAvailable(getActivity())) {
                    ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
                } else {
                    userExtension.setGrade(i);
                    UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback<>(view));
                }
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

                if (!NetworkUtil.isNetAvailable(getActivity())) {
                    ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
                } else {
                    userExtension.setProvince(cPickerNames[0]);
                    userExtension.setCity(cPickerNames[1]);
                    userExtension.setArea(cPickerNames[2]);
                    UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback<>(view));
                }
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

    private void getFocus(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.findFocus();
        editText.setTextColor(Color.GRAY);
    }

    private void loseFocus(EditText editText) {
        editText.setFocusable(false);
        editText.setTextColor(Color.BLACK);
        editText.clearFocus();
    }

    private String[] getGradeValues(int i) {
        switch (i) {
            case 1: {
                return getResources().getStringArray(R.array.lwe_spinner_grade_1);
            }
            case 2: {
                return getResources().getStringArray(R.array.lwe_spinner_grade_2);
            }
            case 3: {
                return getResources().getStringArray(R.array.lwe_spinner_grade_3);
            }
            case 4: {
                return getResources().getStringArray(R.array.lwe_spinner_grade_4);
            }
            case 5: {
                return getResources().getStringArray(R.array.lwe_spinner_grade_5);
            }
            case 6: {
                return getResources().getStringArray(R.array.lwe_spinner_grade_6);
            }
            case 7: {
                return getResources().getStringArray(R.array.lwe_spinner_grade_7);
            }
            default: {
                return getResources().getStringArray(R.array.lwe_spinner_grade_0);
            }
        }
    }
}
