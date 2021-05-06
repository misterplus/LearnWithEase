package team.one.lwe.ui.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netease.nim.uikit.common.activity.UI;

import team.one.lwe.R;
import team.one.lwe.ui.wedget.LWEToolBarOptions;

public class PermissionsettingFragment extends Fragment {

    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO: finish register fragment
        view = inflater.inflate(R.layout.fragment_permissionsetting, container, false);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_perm);
        ((UI) getActivity()).setToolBar(view, R.id.toolbar, options);
        return view;
    }
}