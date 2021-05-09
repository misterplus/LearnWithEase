package team.one.lwe.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import team.one.lwe.R;

public class MineFragment extends Fragment {

    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO: finish mine fragment
        view = inflater.inflate(R.layout.fragment_mine, container, false);
        return view;
    }
}
