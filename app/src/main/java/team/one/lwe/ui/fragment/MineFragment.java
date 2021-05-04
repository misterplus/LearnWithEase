package team.one.lwe.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.netease.nim.uikit.common.activity.UI;

import team.one.lwe.R;
import team.one.lwe.ui.wedget.LWEToolBarOptions;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;
import org.jetbrains.annotations.NotNull;

import cn.hutool.core.io.IORuntimeException;
import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.util.APIUtils;
import team.one.lwe.util.NavigationUtils;
import team.one.lwe.util.TextUtils;

public class MineFragment extends Fragment {

    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO: finish register fragment
        view = inflater.inflate(R.layout.fragment_mine, container, false);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.mine);
        ((UI) getActivity()).setToolBar(view, R.id.toolbar, options);

        Button buttonRegister = view.findViewById(R.id.buttonSetting);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                NavigationUtils.navigateTo(MineFragment.this,new SettingFragment(), true);
            }
        });
        return view;
    }
}
