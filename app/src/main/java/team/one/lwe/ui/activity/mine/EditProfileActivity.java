package team.one.lwe.ui.activity.mine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citypickerview.CityPickerView;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.ui.popupmenu.NIMPopupMenu;
import com.netease.nim.uikit.common.ui.popupmenu.PopupMenuItem;
import com.netease.nim.uikit.common.util.C;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import team.one.lwe.R;
import team.one.lwe.bean.UserInfo;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.activity.MainActivity;
import team.one.lwe.ui.callback.RegularCallback;
import team.one.lwe.ui.callback.UpdateCallback;
import team.one.lwe.ui.listener.TextLockListener;
import team.one.lwe.ui.wedget.LWEToolBarOptions;
import team.one.lwe.util.UserUtils;

public class EditProfileActivity extends LWEUI {

    private final CityPickerView cPicker = new CityPickerView();
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_edit_profile, true);
        setToolBar(R.id.toolbar, options);
        CityConfig cityConfig = new CityConfig.Builder()
                .confirTextColor("#0887FD")
                .build();
        cPicker.setConfig(cityConfig);
        cPicker.init(this);
        EditText editTextName = findViewById(R.id.editTextName);
        EditText editTextSignature = findViewById(R.id.editTextSignature);
        EditText editTextAge = findViewById(R.id.editTextAge);
        ImageButton buttonName = findViewById(R.id.buttonName);
        ImageButton buttonSignature = findViewById(R.id.buttonSignature);
        ImageButton buttonAge = findViewById(R.id.buttonAge);
        ImageButton buttonCity = findViewById(R.id.buttonCity);
        ImageButton buttonAvatar = findViewById(R.id.buttonAvatar);
        RadioGroup groupGender = findViewById(R.id.groupGender);
        RadioButton radioFemale = findViewById(R.id.radioFemale);
        RadioButton radioMale = findViewById(R.id.radioMale);
        RadioButton radioUnknown = findViewById(R.id.radioUnknown);
        TextView textCity = findViewById(R.id.textCity);
        TextView textSchool = findViewById(R.id.textSchool);
        HeadImageView imageAvatar = findViewById(R.id.imageAvatar);
        Spinner spinnerEdu = findViewById(R.id.spinnerEdu);
        Spinner spinnerGrade = findViewById(R.id.spinnerGrade);
        SearchableSpinner spinnerSchool = findViewById(R.id.spinnerSchool);
        RelativeLayout rowSchool = findViewById(R.id.rowSchool);

        String account = NimUIKit.getAccount();
        NimUserInfo user = NIMClient.getService(UserService.class).getUserInfo(account);
        UserInfo userExtension = new Gson().fromJson(user.getExtension(), UserInfo.class);
        int gender = user.getGenderEnum().getValue();
        editTextName.setText(user.getName());
        editTextSignature.setText(user.getSignature());
        editTextAge.setText(String.valueOf(userExtension.getAge()));
        imageAvatar.loadBuddyAvatar(account);

        editTextName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                loseFocus(editTextName);
                String name = editTextName.getText().toString();
                if (UserUtils.isNameInvalid(name))
                    ToastHelper.showToast(this, R.string.lwe_error_name);
                else if (!NetworkUtil.isNetAvailable(getBaseContext()))
                    ToastHelper.showToast(this, R.string.lwe_error_nonetwork);
                else
                    UserUtils.updateUserNickName(name).setCallback(new UpdateCallback(this, "name"));
            }
        });

        editTextSignature.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                loseFocus(editTextSignature);
                String signature = editTextSignature.getText().toString();
                if (UserUtils.isSignatureInvalid(signature))
                    ToastHelper.showToast(this, R.string.lwe_error_signature);
                else if (!NetworkUtil.isNetAvailable(this))
                    ToastHelper.showToast(this, R.string.lwe_error_nonetwork);
                else {
                    if (signature.isEmpty()) {
                        editTextSignature.setText(getString(R.string.lwe_text_empty_signature));
                        UserUtils.updateUserSignature(getString(R.string.lwe_text_empty_signature)).setCallback(new UpdateCallback(this, "signature"));
                    } else
                        UserUtils.updateUserSignature(signature).setCallback(new UpdateCallback(this, "signature"));
                    UserUtils.updateUserSignature(MainActivity.getFragmentList().get(2).getView().findViewById(R.id.textPersonalSignature), signature);
                }
            }
        });

        editTextAge.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                loseFocus(editTextAge);
                int age = Integer.parseInt(editTextAge.getText().toString());
                if (UserUtils.isAgeInvalid(age)) {
                    ToastHelper.showToast(this, R.string.lwe_error_age);
                } else if (!NetworkUtil.isNetAvailable(this)) {
                    ToastHelper.showToast(this, R.string.lwe_error_nonetwork);
                } else {
                    userExtension.setAge(age);
                    UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback(this, "age"));
                }
            }
        });

        switch (gender) {
            case 2: {
                radioFemale.setChecked(true);
                break;
            }
            case 1: {
                radioMale.setChecked(true);
                break;
            }
            default: {
                radioUnknown.setChecked(true);
            }
        }

        groupGender.setOnCheckedChangeListener((group, checkedId) -> {
            if (!NetworkUtil.isNetAvailable(this)) {
                ToastHelper.showToast(this, R.string.lwe_error_nonetwork);
                return;
            }
            switch (checkedId) {
                case R.id.radioFemale: {
                    UserUtils.updateUserGender(2).setCallback(new UpdateCallback(this, "gender"));
                    break;
                }
                case R.id.radioMale: {
                    UserUtils.updateUserGender(1).setCallback(new UpdateCallback(this, "gender"));
                    break;
                }
                default: {
                    UserUtils.updateUserGender(0).setCallback(new UpdateCallback(this, "gender"));
                }
            }
        });

        String text;
        if (userExtension.getProvince().equals(userExtension.getCity()))
            text = String.format(getString(R.string.lwe_placeholder_cityformat2), userExtension.getProvince(), userExtension.getArea());
        else
            text = String.format(getString(R.string.lwe_placeholder_cityformat3), userExtension.getProvince(), userExtension.getCity(), userExtension.getArea());
        if (text.length() >= 16) {
            textCity.setTextScaleX((float) (16.0 / text.length()));
        } else {
            textCity.setTextScaleX((float) 1.0);
        }
        textCity.setText(text);
        textCity.setSelected(true);

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
                if (school.length() >= 16) {
                    textSchool.setTextScaleX((float) (16.0 / school.length()));
                } else {
                    textSchool.setTextScaleX((float) 1.0);
                }
                textSchool.setSelected(true);
                if (!NetworkUtil.isNetAvailable(getBaseContext())) {
                    ToastHelper.showToast(getBaseContext(), R.string.lwe_error_nonetwork);
                } else {
                    userExtension.setSchool(school);
                    UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback(getBaseContext(), "school"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                textSchool.setText(getString(R.string.lwe_placeholder_school));
                textSchool.setSelected(true);
            }
        });

        String[] gradeValues = UserUtils.getGradeValues(getResources(), userExtension.getBak());
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, R.layout.lwe_spinner_item, gradeValues);
        adapter3.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerGrade.setAdapter(adapter3);

        spinnerEdu.setSelection(userExtension.getBak(), false);
        spinnerGrade.setSelection(userExtension.getGrade(), false);
        if (userExtension.getBak() > 3) {
            rowSchool.setVisibility(View.VISIBLE);
            for (int i = 0; i < schoolValues.length; i++)
                if (schoolValues[i].equals(userExtension.getSchool())) {
                    spinnerSchool.setSelection(i, false);
                    break;
                }
        }

        spinnerEdu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!NetworkUtil.isNetAvailable(getBaseContext())) {
                    ToastHelper.showToast(getBaseContext(), R.string.lwe_error_nonetwork);
                } else {
                    userExtension.setBak(i);
                    if (i > 3) {
                        rowSchool.setVisibility(View.VISIBLE);
                    } else {
                        rowSchool.setVisibility(View.GONE);
                        userExtension.setSchool("");
                    }
                    UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback(getBaseContext(), "education"));
                }

                String[] values = UserUtils.getGradeValues(getResources(), i);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), R.layout.lwe_spinner_item, values);
                adapter.setDropDownViewResource(R.layout.lwe_spinner_item);
                spinnerGrade.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                String[] gradeValues = UserUtils.getGradeValues(getResources(), userExtension.getBak());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), R.layout.lwe_spinner_item, gradeValues);
                adapter.setDropDownViewResource(R.layout.lwe_spinner_item);
                spinnerGrade.setAdapter(adapter);
            }
        });

        spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!NetworkUtil.isNetAvailable(getBaseContext())) {
                    ToastHelper.showToast(getBaseContext(), R.string.lwe_error_nonetwork);
                } else {
                    userExtension.setGrade(i);
                    UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback(getBaseContext(), "grade"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        cPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                String text;
                if (province.getName().equals(city.getName()))
                    text = String.format(getString(R.string.lwe_placeholder_cityformat2), province.getName(), district.getName());
                else
                    text = String.format(getString(R.string.lwe_placeholder_cityformat3), province.getName(), city.getName(), district.getName());
                if (text.length() >= 16) {
                    textCity.setTextScaleX((float) (16.0 / text.length()));
                } else {
                    textCity.setTextScaleX((float) 1.0);
                }
                textCity.setText(text);
                textCity.setSelected(true);

                if (!NetworkUtil.isNetAvailable(getBaseContext())) {
                    ToastHelper.showToast(getBaseContext(), R.string.lwe_error_nonetwork);
                } else {
                    userExtension.setProvince(province.getName());
                    userExtension.setCity(city.getName());
                    userExtension.setArea(district.getName());
                    UserUtils.updateUserExtension(userExtension).setCallback(new UpdateCallback(getBaseContext(), "city"));
                }
            }
        });

        buttonName.setOnClickListener(new TextLockListener(editTextName));
        buttonSignature.setOnClickListener(new TextLockListener(editTextSignature));
        buttonAge.setOnClickListener(new TextLockListener(editTextAge));
        buttonCity.setOnClickListener(view -> cPicker.showCityPicker());
        List<PopupMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new PopupMenuItem(0, "拍照"));
        menuItems.add(new PopupMenuItem(1, "从相册选择"));
        NIMPopupMenu menu = new NIMPopupMenu(this, menuItems, item -> {
            switch (item.getTag()) {
                case 0: {
                    File caption = new File(this.getExternalCacheDir(), "caption_avatar.jpg");
                    try {
                        if (caption.exists())
                            caption.delete();
                        caption.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (Build.VERSION.SDK_INT >= 24)
                        uri = FileProvider.getUriForFile(this, "team.one.lwe.ipc.provider.file", caption);
                    else
                        uri = Uri.fromFile(caption);
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, 0);
                    break;
                }
                case 1: {
                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }
            }
        });
        buttonAvatar.setOnClickListener(v -> menu.show(findViewById(R.id.buttonAvatar)));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 0: {
                    File cropped = new File(this.getExternalCacheDir(), "avatar_cropped.png");
                    try {
                        if (cropped.exists())
                            cropped.delete();
                        cropped.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Uri uriCropped = Uri.fromFile(cropped);
                    UCrop.of(uri, uriCropped)
                            .withAspectRatio(1, 1)
                            .withMaxResultSize(400, 400)
                            .start(this);
                    break;
                }
                case 1: {
                    uri = data.getData();
                    File cropped = new File(this.getExternalCacheDir(), "avatar_cropped.png");
                    try {
                        if (cropped.exists())
                            cropped.delete();
                        cropped.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Uri uriCropped = Uri.fromFile(cropped);
                    UCrop.of(uri, uriCropped)
                            .withAspectRatio(1, 1)
                            .withMaxResultSize(400, 400)
                            .start(this);
                    break;
                }
                case UCrop.REQUEST_CROP: {
                    Uri uriResult = UCrop.getOutput(data);
                    File result = new File(uriResult.getPath());
                    NIMClient.getService(NosService.class).upload(result, C.MimeType.MIME_PNG).setCallback(new RegularCallback<String>(this) {
                        @Override
                        public void onSuccess(String url) {
                            UserUtils.updateUserAvatar(url).setCallback(new UpdateCallback(getBaseContext(), "avatar"));
                            HeadImageView imageAvatar = findViewById(R.id.imageAvatar);
                            HeadImageView imageAvatar2 = MainActivity.getFragmentList().get(2).getView().findViewById(R.id.imageAvatar);
                            imageAvatar.loadAvatar(url);
                            imageAvatar2.loadAvatar(url);
                        }
                    });
                }
                case UCrop.RESULT_ERROR: {
                    try {
                        throw UCrop.getError(data);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }
        }
    }

    private void loseFocus(EditText editText) {
        editText.setFocusable(false);
        editText.setTextColor(Color.BLACK);
        editText.clearFocus();
    }
}
