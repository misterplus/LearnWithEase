package team.one.lwe.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;

import team.one.lwe.R;

public class RegisterFragment extends Fragment {

    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO: finish register fragment
        view = inflater.inflate(R.layout.fragment_register, container, false);
        ToolBarOptions options = new ToolBarOptions();
        options.logoId = com.netease.nim.uikit.R.drawable.nim_actionbar_nest_dark_logo;
        options.navigateId = com.netease.nim.uikit.R.drawable.nim_actionbar_dark_back_icon;
        options.isNeedNavigate = true;
        options.titleString = "test";
        ((UI) getActivity()).setToolBar(R.id.toolbar, options);
        /*Toolbar bar = view.findViewById(R.id.toolbar);
        bar.setTitle("test");*/
        return view;
    }
}
