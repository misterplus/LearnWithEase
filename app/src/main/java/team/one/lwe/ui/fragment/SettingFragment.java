package team.one.lwe.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;

import org.jetbrains.annotations.NotNull;

import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.wedget.LWEToolBarOptions;
import team.one.lwe.util.APIUtils;
import team.one.lwe.util.TextUtils;

public class SettingFragment extends Fragment {
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_setting, true);
        BottomNavigationView navibar = getActivity().findViewById(R.id.navibar);
        navibar.setVisibility(View.GONE);
        ((UI) getActivity()).setToolBar(getActivity().findViewById(R.id.toolbar), R.id.toolbar, options);

        EditText editTextPassword = view.findViewById(R.id.editTextPassword);
        EditText editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        ImageButton buttonUpdatePassword = view.findViewById(R.id.buttonUpdatePassword);
        ImageButton buttonConfirmPassword = view.findViewById(R.id.buttonConfirmPassword);
        RelativeLayout passwordLayout = view.findViewById(R.id.passwordLayout);
        RelativeLayout confirmPasswordLayout = view.findViewById(R.id.confirmPasswordLayout);

        buttonUpdatePassword.setOnClickListener(view -> {
            passwordLayout.setVisibility(View.VISIBLE);
            confirmPasswordLayout.setVisibility(View.VISIBLE);
        });

        buttonConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = editTextPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();
                if (!isPasswordValid(password)) {
                    ToastHelper.showToast(view.getContext(), R.string.lwe_error_password);
                } else if (!password.equals(confirmPassword)) {
                    ToastHelper.showToast(view.getContext(), R.string.lwe_error_confirm_password);
                } else if (!NetworkUtil.isNetAvailable(getActivity())) {
                    ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
                } else {
                    doUpdate(NimUIKit.getAccount(), password);
                }
            }
        });

        return view;
    }

    private static boolean isPasswordValid(@NotNull String password) {
        return TextUtils.isLegalPassword(password) && TextUtils.getPasswordComplexity(password) > 1 && password.length() >= 6 && password.length() <= 16;
    }

    private void doUpdate(String username, String password) {
        new NetworkThread(view) {
            @Override
            public ASResponse doRequest() {
                return APIUtils.update(username, password);
            }

            @Override
            public void onSuccess(ASResponse asp) {
                ToastHelper.showToast(view.getContext(), R.string.lwe_success_update);
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
    }
}
