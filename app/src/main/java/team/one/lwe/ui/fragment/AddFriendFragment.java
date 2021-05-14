package team.one.lwe.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.netease.nim.uikit.common.activity.UI;

import team.one.lwe.R;

public class AddFriendFragment extends Fragment {

    private View view;

    private void onNavigateUpClicked() {
        getActivity().onBackPressed();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend_add, container, false);
        getActivity().findViewById(R.id.app_bar_layout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.navibar).setVisibility(View.GONE);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> onNavigateUpClicked());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().findViewById(R.id.app_bar_layout).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.navibar).setVisibility(View.VISIBLE);
    }
}
