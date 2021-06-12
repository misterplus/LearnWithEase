package team.one.lwe.ui.activity.auth;

import android.os.Bundle;
import android.view.View;
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

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citypickerview.CityPickerView;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import team.one.lwe.LWEConstants;
import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.bean.Preference;
import team.one.lwe.bean.User;
import team.one.lwe.bean.UserInfo;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.wedget.LWEToolBarOptions;
import team.one.lwe.util.APIUtils;
import team.one.lwe.util.TextUtils;
import team.one.lwe.util.UserUtils;

public class RegisterActivity extends LWEUI {

    private final CityPickerView cPicker = new CityPickerView();
    private final String[] cPickerNames = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_register, true);
        setToolBar(R.id.toolbar, options);
        CityConfig cityConfig = new CityConfig.Builder()
                .confirTextColor("#0887FD")
                .build();
        cPicker.setConfig(cityConfig);
        cPicker.init(this);
        TextView textCity = findViewById(R.id.textCity);
        TextView textSchool = findViewById(R.id.textSchool);
        EditText editTextUsername = findViewById(R.id.editTextUsername);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        EditText editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        EditText editTextName = findViewById(R.id.editTextName);
        EditText editTextAge = findViewById(R.id.editTextAge);
        RadioGroup groupGender = findViewById(R.id.groupGender);
        ImageButton buttonCity = findViewById(R.id.buttonCity);
        Spinner spinnerEdu = findViewById(R.id.spinnerEdu);
        SearchableSpinner spinnerSchool = findViewById(R.id.spinnerSchool);
        Spinner spinnerGrade = findViewById(R.id.spinnerGrade);
        RelativeLayout rowSchool = findViewById(R.id.rowSchool);
        Button buttonRegister = findViewById(R.id.buttonRegister);
        Spinner spinnerTimeStudy = findViewById(R.id.spinnerTimeStudy);
        Spinner spinnerTimeRest = findViewById(R.id.spinnerTimeRest);
        Spinner spinnerContentStudy = findViewById(R.id.spinnerContentStudy);
        SwitchMaterial switchSameCity = findViewById(R.id.switchSameCity);
        SwitchMaterial switchSameSchool = findViewById(R.id.switchSameSchool);
        SwitchMaterial switchSameGender = findViewById(R.id.switchSameGender);
        SwitchMaterial switchNewRoomFirst = findViewById(R.id.switchNewRoomFirst);

        String[] eduValues = getResources().getStringArray(R.array.lwe_spinner_edu);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.lwe_spinner_item, eduValues);
        adapter.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerEdu.setAdapter(adapter);


        String[] schoolValues = getResources().getStringArray(R.array.lwe_spinner_school);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.lwe_spinner_item_invisible, schoolValues);
        adapter2.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerSchool.setAdapter(adapter2);
        spinnerSchool.setTitle(getString(R.string.lwe_placeholder_school));
        spinnerSchool.setPositiveButton(getString(R.string.lwe_hint_confirm));
        spinnerSchool.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String school = (String) spinnerSchool.getSelectedItem();
                textSchool.setText(school);
                textSchool.setSelected(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                textSchool.setText(getString(R.string.lwe_placeholder_school));
            }
        });

        String[] gradeValues = getResources().getStringArray(R.array.lwe_spinner_grade_0);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, R.layout.lwe_spinner_item, gradeValues);
        adapter3.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerGrade.setAdapter(adapter3);

        spinnerEdu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] values = UserUtils.getGradeValues(getResources(), i);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), R.layout.lwe_spinner_item, values);
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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), R.layout.lwe_spinner_item, gradeValues);
                adapter.setDropDownViewResource(R.layout.lwe_spinner_item);
                spinnerGrade.setAdapter(adapter);
            }
        });


        String[] timeValues = getResources().getStringArray(R.array.lwe_spinner_time_study);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(this, R.layout.lwe_spinner_item, timeValues);
        adapter4.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerTimeStudy.setAdapter(adapter4);

        String[] restValues = getResources().getStringArray(R.array.lwe_spinner_time_rest);
        ArrayAdapter<String> adapter5 = new ArrayAdapter<>(this, R.layout.lwe_spinner_item, restValues);
        adapter5.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerTimeRest.setAdapter(adapter5);

        String[] contentValues = getResources().getStringArray(R.array.lwe_spinner_content_study);
        ArrayAdapter<String> adapter6 = new ArrayAdapter<>(this, R.layout.lwe_spinner_item, contentValues);
        adapter6.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerContentStudy.setAdapter(adapter6);

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
                textCity.setText(text);
                textCity.setSelected(true);
            }
        });
        buttonCity.setOnClickListener(view -> cPicker.showCityPicker());

        buttonRegister.setOnClickListener(view1 -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();
            String name = editTextName.getText().toString();
            int gender;
            int age = Integer.parseInt(editTextAge.getText().toString());
            int bak = spinnerEdu.getSelectedItemPosition();
            int grade = spinnerGrade.getSelectedItemPosition();
            if (!UserUtils.isUsernameValid(username)) {
                ToastHelper.showToast(this, R.string.lwe_error_login_format_username);
            } else if (!UserUtils.isPasswordValid(password)) {
                ToastHelper.showToast(this, R.string.lwe_error_login_format_password);
            } else if (!password.equals(confirmPassword)) {
                ToastHelper.showToast(this, R.string.lwe_error_confirm_password);
            } else if (UserUtils.isNameInvalid(name)) {
                ToastHelper.showToast(this, R.string.lwe_error_name);
            } else if (UserUtils.isAgeInvalid(age)) {
                ToastHelper.showToast(this, R.string.lwe_error_age_format);
            } else if (TextUtils.isEmpty(cPickerNames[0]) || TextUtils.isEmpty(cPickerNames[1]) || TextUtils.isEmpty(cPickerNames[2])) {
                ToastHelper.showToast(this, R.string.lwe_error_city);
            } else {
                DialogMaker.showProgressDialog(this, getString(R.string.lwe_progress_register), false);
                try {
                    switch (((RadioButton) findViewById(groupGender.getCheckedRadioButtonId())).getText().toString()) {
                        case "女": {
                            gender = 2;
                            break;
                        }
                        case "男": {
                            gender = 1;
                            break;
                        }
                        default: {
                            gender = 0;
                        }
                    }
                    String school = bak > 3 ? (String) spinnerSchool.getSelectedItem() : "";
                    Preference pref = new Preference(spinnerTimeStudy.getSelectedItemPosition(), spinnerTimeRest.getSelectedItemPosition(), spinnerContentStudy.getSelectedItemPosition(), switchSameCity.isChecked(), switchSameSchool.isChecked(), switchSameGender.isChecked(), switchNewRoomFirst.isChecked());
                    User user = new User(username, password, name, gender, new UserInfo(age, grade, bak, cPickerNames[0], cPickerNames[1], cPickerNames[2], school, pref), LWEConstants.DEFAULT_AVATAR, getString(R.string.lwe_text_empty_signature));
                    new NetworkThread(editTextName) {
                        @Override
                        public ASResponse doRequest() {
                            return APIUtils.register(user);
                        }

                        @Override
                        public void onSuccess(ASResponse asp) {
                            DialogMaker.dismissProgressDialog();
                            setResult(1);
                            finish();
                        }

                        @Override
                        public void onFailed(int code, String desc) {
                            DialogMaker.dismissProgressDialog();
                            if (code == 414) {
                                if (desc.equals("already register"))
                                    ToastHelper.showToast(getBaseContext(), R.string.lwe_error_register);
                                else
                                    ToastHelper.showToast(getBaseContext(), R.string.lwe_error_unknown);
                                return;
                            }
                            super.onFailed(code, desc);
                        }

                        @Override
                        public void onException(Exception e) {
                            DialogMaker.dismissProgressDialog();
                            super.onException(e);
                        }

                    }.start();
                } catch (NumberFormatException e) {
                    DialogMaker.dismissProgressDialog();
                    ToastHelper.showToast(this, R.string.lwe_error_age_format);
                }
            }
        });
    }
}
