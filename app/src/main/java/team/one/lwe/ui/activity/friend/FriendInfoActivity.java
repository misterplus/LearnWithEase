package team.one.lwe.ui.activity.friend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import team.one.lwe.R;
import team.one.lwe.bean.UserInfo;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.wedget.LWEToolBarOptions;

public class FriendInfoActivity extends LWEUI {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_friend_info, true);
        setToolBar(R.id.toolbar, options);

        String accid = (String) getIntent().getSerializableExtra("accid");
        NimUserInfo info = NIMClient.getService(UserService.class).getUserInfo(accid);
        HeadImageView imageAvatar = findViewById(R.id.imageAvatar);
        TextView textName = findViewById(R.id.textName);
        TextView textInfo = findViewById(R.id.textInfo);
        TextView textSignature = findViewById(R.id.textSignature);
        TextView textCity = findViewById(R.id.textCity);
        RelativeLayout rowSchool = findViewById(R.id.rowSchool);
        TextView textSchool = findViewById(R.id.textSchool);
        Button buttonChat = findViewById(R.id.buttonChat);
        Button buttonAddFriend = findViewById(R.id.buttonAddFriend);

        imageAvatar.loadBuddyAvatar(accid);
        textName.setText(String.format("%s(%s)", info.getName(), info.getAccount()));
        UserInfo ex = new Gson().fromJson(info.getExtension(), UserInfo.class);
        textInfo.setText(String.format("%s %s岁 %s %s", getResources().getStringArray(R.array.lwe_gender)[info.getGenderEnum().getValue()], ex.getAge(), ex.getBak(), ex.getGrade()));
        textSignature.setText(info.getSignature());
        textCity.setText(String.format("%s %s %s", ex.getProvince(), ex.getCity(), ex.getArea()));
        if (ex.getBak() > 3) {
            rowSchool.setVisibility(View.VISIBLE);
            textSchool.setText(ex.getSchool());
        }
        if (NIMClient.getService(FriendService.class).isMyFriend(accid)) {
            buttonChat.setVisibility(View.VISIBLE);
            buttonAddFriend.setVisibility(View.GONE);
        } else {
            buttonChat.setVisibility(View.GONE);
            buttonAddFriend.setVisibility(View.VISIBLE);
        }

        buttonChat.setOnClickListener(view -> NimUIKit.startP2PSession(view.getContext(), accid));

        buttonAddFriend.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), AddVerifyActivity.class);
            intent.putExtra("account", accid);
            startActivityForResult(intent, -1);
        });
    }
}
