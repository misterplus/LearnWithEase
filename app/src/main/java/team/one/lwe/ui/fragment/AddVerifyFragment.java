package team.one.lwe.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import team.one.lwe.R;
import team.one.lwe.bean.UserInfo;
import team.one.lwe.ui.callback.RegularCallback;
import team.one.lwe.ui.wedget.LWEToolBarOptions;
import team.one.lwe.util.NavigationUtils;
import team.one.lwe.util.UserUtils;

public class AddVerifyFragment extends Fragment {
    private final String account;
    private View view;

    private void onNavigateUpClicked() {
        getActivity().onBackPressed();
    }

    public AddVerifyFragment(String account) {
        this.account = account;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend_add_verify, container, false);
        getActivity().findViewById(R.id.app_bar_layout).setVisibility(View.VISIBLE);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_friend_add, true);
        ((UI) getActivity()).setToolBar(R.id.toolbar, options);

        RoundedImageView imageAvatar = view.findViewById(R.id.imageAvatar);
        TextView textName = view.findViewById(R.id.textName);
        TextView textInfo = view.findViewById(R.id.textInfo);
        Button buttonAdd = view.findViewById(R.id.buttonAdd);
        EditText editTextReason = view.findViewById(R.id.editTextReason);
        NimUserInfo info = NIMClient.getService(UserService.class).getUserInfo(account);
        UserUtils.setAvatar(imageAvatar, account, info.getAvatar());
        textName.setText(String.format("%s(%s)", info.getName(), info.getAccount()));
        UserInfo ex = new Gson().fromJson(info.getExtension(), UserInfo.class);
        textInfo.setText(String.format("%s %s岁 %s", getResources().getStringArray(R.array.lwe_gender)[info.getGenderEnum().getValue()], ex.getAge(), ex.getProvince()));
        buttonAdd.setOnClickListener(v -> {
            String reason = editTextReason.getText().toString();
            if (reason.isEmpty() || reason.length() > 60)
                ToastHelper.showToast(getContext(), getString(R.string.lwe_error_reason));
            else {
                NIMClient.getService(FriendService.class).addFriend(new AddFriendData(account, VerifyType.VERIFY_REQUEST, reason)).setCallback(new RegularCallback<Void>(view) {
                    @Override
                    public void onSuccess(Void param) {
                        ToastHelper.showToast(getContext(), getString(R.string.lwe_success_add));
                    }
                });
                //TODO: go back to Friend Fragment
                onNavigateUpClicked();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().findViewById(R.id.app_bar_layout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.navibar).setVisibility(View.GONE);
    }
}
