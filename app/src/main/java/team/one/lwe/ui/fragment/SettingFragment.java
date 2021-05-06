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
import android.widget.Button;
import team.one.lwe.util.NavigationUtils;


public class SettingFragment extends Fragment {

    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO: finish register fragment
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_setting);
        ((UI) getActivity()).setToolBar(view, R.id.toolbar, options);

        Button buttonRegister = view.findViewById(R.id.buttonPermissionSetting);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                NavigationUtils.navigateTo(SettingFragment.this,new PermissionsettingFragment(), true);
            }
        });
        return view;
    }
}
