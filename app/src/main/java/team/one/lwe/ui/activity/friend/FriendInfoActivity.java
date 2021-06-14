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
import team.one.lwe.util.UserUtils;

public class FriendInfoActivity extends LWEUI {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_friend_info, true);
        setToolBar(R.id.toolbar, options);

        String accid = getIntent().getStringExtra("accid");
        NimUserInfo info = NIMClient.getService(UserService.class).getUserInfo(accid);
        HeadImageView imageAvatar = findViewById(R.id.imageAvatar);
        TextView textName = findViewById(R.id.textName);
        TextView textUserName = findViewById(R.id.textUserName);
        TextView textUserInfo = findViewById(R.id.textUserInfo);
        TextView textSignature = findViewById(R.id.textSignature);
        TextView textCity = findViewById(R.id.textCity);
        RelativeLayout rowSchool = findViewById(R.id.rowSchool);
        TextView textSchool = findViewById(R.id.textSchool);
        Button buttonChat = findViewById(R.id.buttonChat);
        Button buttonAddFriend = findViewById(R.id.buttonAddFriend);

        imageAvatar.loadBuddyAvatar(accid);
        textName.setText(info.getName());
        textUserName.setText(info.getAccount());
        UserInfo ex = new Gson().fromJson(info.getExtension(), UserInfo.class);
        textUserInfo.setText(String.format("%s %s岁 | %s %s", getResources().getStringArray(R.array.lwe_gender)[info.getGenderEnum().getValue()], ex.getAge(), getResources().getStringArray(R.array.lwe_spinner_edu)[ex.getBak()], UserUtils.getGradeValues(getResources(), ex.getBak())[ex.getGrade()]));
        textSignature.setText(info.getSignature());
        textCity.setText(String.format("%s %s %s", ex.getProvince(), ex.getCity(), ex.getArea()));
        if (ex.getBak() > 3) {
            rowSchool.setVisibility(View.VISIBLE);
            textSchool.setText(ex.getSchool());
            textSchool.setSelected(true);
        }
        if (NIMClient.getService(FriendService.class).isMyFriend(accid)) {
            buttonChat.setVisibility(View.VISIBLE);
            buttonAddFriend.setVisibility(View.GONE);
        } else {
            buttonChat.setVisibility(View.GONE);
            buttonAddFriend.setVisibility(View.VISIBLE);
        }
        textSignature.setSelected(true);
        textCity.setSelected(true);
        if (NIMClient.getService(FriendService.class).isInBlackList(accid)) {
            RelativeLayout buttonsFriendInfo = findViewById(R.id.buttonsFriendInfo);
            buttonsFriendInfo.setVisibility(View.GONE);
        }

        buttonChat.setOnClickListener(view -> NimUIKit.startP2PSession(view.getContext(), accid));

        buttonAddFriend.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), AddVerifyActivity.class);
            intent.putExtra("account", accid);
            startActivity(intent);
        });
    }
}
