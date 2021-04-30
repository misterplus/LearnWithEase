package team.one.lwe.ui.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netease.nim.uikit.common.activity.UI;

import team.one.lwe.R;
import team.one.lwe.ui.wedget.LWEToolBarOptions;

public class EditProfileFragment extends Fragment {

    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO: finish register fragment
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.edit_profile);
        ((UI) getActivity()).setToolBar(view, R.id.toolbar, options);


        return view;
    }
}