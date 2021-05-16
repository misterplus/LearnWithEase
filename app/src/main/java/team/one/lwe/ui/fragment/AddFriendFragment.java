package team.one.lwe.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.netease.nim.uikit.common.activity.UI;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

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
        EditText editTextSearchUsername = view.findViewById(R.id.editTextSearchUsername);
        EditText editText
        editTextSearchUsername.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                NimUserInfo searchedInfo = NIMClient.getService(UserService.class).getUserInfo(editTextSearchUsername.getText().toString());

            }
            return false;
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().findViewById(R.id.app_bar_layout).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.navibar).setVisibility(View.VISIBLE);
    }
}
