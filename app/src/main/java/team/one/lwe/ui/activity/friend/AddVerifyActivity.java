package team.one.lwe.ui.activity.friend;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import team.one.lwe.R;
import team.one.lwe.bean.UserInfo;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.callback.RegularCallback;
import team.one.lwe.ui.wedget.LWEToolBarOptions;

public class AddVerifyActivity extends LWEUI {
    private String account;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_add_verify);
        account = getIntent().getStringExtra("account");
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_friend_add, true);
        setToolBar(R.id.toolbar, options);

        HeadImageView imageAvatar = findViewById(R.id.imageAvatar);
        TextView textName = findViewById(R.id.textName);
        TextView textInfo = findViewById(R.id.textInfo);
        Button buttonAdd = findViewById(R.id.buttonAdd);
        EditText editTextReason = findViewById(R.id.editTextReason);
        NimUserInfo info = NIMClient.getService(UserService.class).getUserInfo(account);
        imageAvatar.loadBuddyAvatar(account);
        textName.setText(String.format("%s(%s)", info.getName(), info.getAccount()));
        UserInfo ex = new Gson().fromJson(info.getExtension(), UserInfo.class);
        textInfo.setText(String.format("%s %s岁 %s", getResources().getStringArray(R.array.lwe_gender)[info.getGenderEnum().getValue()], ex.getAge(), ex.getProvince()));
        buttonAdd.setOnClickListener(v -> {
            String reason = editTextReason.getText().toString();
            if (reason.isEmpty() || reason.length() > 60)
                ToastHelper.showToast(this, R.string.lwe_error_reason);
            else {
                NIMClient.getService(FriendService.class).addFriend(new AddFriendData(account, VerifyType.VERIFY_REQUEST, reason)).setCallback(new RegularCallback<Void>(this) {
                    @Override
                    public void onSuccess(Void param) {
                        ToastHelper.showToast(getBaseContext(), R.string.lwe_success_add);
                        setResult(1);
                        finish();
                    }
                });
            }
        });
    }
}
