 package team.one.lwe.ui.activity.friend;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.netease.nimlib.sdk.msg.SystemMessageService;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.List;

import team.one.lwe.R;
import team.one.lwe.bean.UserInfo;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.callback.RegularCallback;
import team.one.lwe.ui.callback.VoidSuccessCallback;
import team.one.lwe.ui.wedget.LWEToolBarOptions;

public class FriendRequestActivity extends LWEUI {
    private String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);
        account = getIntent().getStringExtra("account");
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_friend_request, true);
        setToolBar(R.id.toolbar, options);

        HeadImageView imageAvatar = findViewById(R.id.imageAvatar);
        TextView textName = findViewById(R.id.textName);
        TextView textInfo = findViewById(R.id.textInfo);
        TextView textReason = findViewById(R.id.textReason);
        Button buttonAccept = findViewById(R.id.buttonAccept);
        Button buttonDecline = findViewById(R.id.buttonDecline);
        NimUserInfo info = NIMClient.getService(UserService.class).getUserInfo(account);
        imageAvatar.loadBuddyAvatar(account);
        textName.setText(String.format("%s(%s)", info.getName(), info.getAccount()));
        UserInfo ex = new Gson().fromJson(info.getExtension(), UserInfo.class);
        textInfo.setText(String.format("%s %s岁 %s", getResources().getStringArray(R.array.lwe_gender)[info.getGenderEnum().getValue()], ex.getAge(), ex.getProvince()));
        textReason.setText(getIntent().getStringExtra("reason"));

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NIMClient.getService(FriendService.class).ackAddFriendRequest(account, true).setCallback(new VoidSuccessCallback(view.getContext()));
                NIMClient.getService(SystemMessageService.class).setSystemMessageRead(Long.parseLong(getIntent().getStringExtra("msgId")));
                finish();
            }
        });
        buttonDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NIMClient.getService(FriendService.class).ackAddFriendRequest(account, false).setCallback(new VoidSuccessCallback(view.getContext()));
                NIMClient.getService(SystemMessageService.class).setSystemMessageRead(Long.parseLong(getIntent().getStringExtra("msgId")));
                finish();
            }
        });
    }
}
