package team.one.lwe.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citypickerview.CityPickerView;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.bean.User;
import team.one.lwe.bean.UserInfo;
import team.one.lwe.enums.Background;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.wedget.LWEToolBarOptions;
import team.one.lwe.util.APIUtils;

public class RegisterFragment extends Fragment {

    private final CityPickerView cPicker = new CityPickerView();
    private final String[] cPickerNames = new String[3];
    private View view;

    private void onBackPressed() {
        getActivity().onBackPressed();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO: finish register fragment
        view = inflater.inflate(R.layout.fragment_register, container, false);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_register);
        ((UI) getActivity()).setToolBar(view, R.id.toolbar, options);
        CityConfig cityConfig = new CityConfig.Builder()
                .confirTextColor("#0887FD")
                .build();
        cPicker.setConfig(cityConfig);
        cPicker.init(this.getContext());
        TextView textCityPicker = view.findViewById(R.id.textCityPicker);
        TextView textCity = view.findViewById(R.id.textCity);
        TextView textSchool = view.findViewById(R.id.textSchool);
        TextView textSchoolPicker = view.findViewById(R.id.textSchoolPicker);
        EditText editTextUsername = view.findViewById(R.id.editTextUsername);
        EditText editTextPassword = view.findViewById(R.id.editTextPassword);
        EditText editTextName = view.findViewById(R.id.editTextName);
        EditText editTextAge = view.findViewById(R.id.editTextAge);
        RadioGroup groupGender = view.findViewById(R.id.groupGender);
        ImageButton buttonCity = view.findViewById(R.id.buttonCity);
        Spinner spinnerEdu = view.findViewById(R.id.spinnerEdu);
        SearchableSpinner spinnerSchool = view.findViewById(R.id.spinnerSchool);
        Spinner spinnerGrade = view.findViewById(R.id.spinnerGrade);
        RelativeLayout rowSchool = view.findViewById(R.id.rowSchool);
        Button buttonRegister = view.findViewById(R.id.buttonRegister);

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
            }
        });
        buttonCity.setOnClickListener(view -> cPicker.showCityPicker());

        buttonRegister.setOnClickListener(view -> {
            DialogMaker.showProgressDialog(getContext(), getString(R.string.lwe_progress_register));
            try {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                String name = editTextName.getText().toString();
                int gender = groupGender.getCheckedRadioButtonId();
                int age = Integer.parseInt(editTextAge.getText().toString());
                int bak = spinnerEdu.getSelectedItemPosition();
                int grade = spinnerGrade.getSelectedItemPosition();
                String school = bak > 3 ? (String) spinnerSchool.getSelectedItem() : "";
                User user = new User(username, password, name, gender, new UserInfo(age, grade, Background.values()[bak], cPickerNames[0], cPickerNames[1], cPickerNames[2], school));
                new NetworkThread(view) {
                    @Override
                    public ASResponse doRequest() {
                        return APIUtils.register(user);
                    }

                    @Override
                    public void onSuccess(ASResponse asp) {
                        ToastHelper.showToast(getContext(), getString(R.string.lwe_success_register));
                        onBackPressed();
                    }

                    @Override
                    public void onFailed(int code, String desc) {
                        DialogMaker.dismissProgressDialog();
                        switch (code) {
                            case 408: {
                                ToastHelper.showToast(view.getContext(), R.string.lwe_error_timeout);
                                break;
                            }
                            case 415: {
                                ToastHelper.showToast(view.getContext(), R.string.lwe_error_confail);
                                break;
                            }
                            default: {
                                ToastHelper.showToast(view.getContext(), R.string.lwe_error_unknown);
                            }
                        }
                    }

                    @Override
                    public void onException(Exception e) {
                        DialogMaker.dismissProgressDialog();
                        super.onException(e);
                    }

                }.start();
            } catch (NumberFormatException e) {
                DialogMaker.dismissProgressDialog();
                ToastHelper.showToast(getContext(), getString(R.string.lwe_error_age_format));
            }
        });
        return view;
    }
}
