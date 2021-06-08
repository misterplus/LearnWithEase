package team.one.lwe.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import team.one.lwe.LWECache;
import team.one.lwe.R;
import team.one.lwe.config.Preferences;
import team.one.lwe.ui.activity.auth.LoginActivity;
import team.one.lwe.ui.fragment.FriendFragment;
import team.one.lwe.ui.fragment.HomeFragment;
import team.one.lwe.ui.fragment.MineFragment;

public class MainActivity extends LWEUI {

    private static List<Fragment> fragmentList;
    private final List<Integer> idList = Arrays.asList(R.id.naviHome, R.id.naviFriend, R.id.naviMine);

    public static List<Fragment> getFragmentList() {
        return fragmentList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //登录状态监听
        Observer<StatusCode> observer = (Observer<StatusCode>) statusCode -> {
            if (statusCode == StatusCode.KICKOUT) {
                ToastHelper.showToast(getBaseContext(), "被踢了........");
                EasyAlertDialogHelper.createOkCancelDiolag(this, "提示", "您的帐号已在另一台设备登录，是否重新登录？",
                        "重新登录", "退出登录", false, new EasyAlertDialogHelper.OnDialogActionListener() {
                            @Override
                            public void doCancelAction() {
                                ToastHelper.showToast(getBaseContext(), "退出登录");
                                NIMClient.getService(AuthService.class).logout();
                                LWECache.clear();
                                Preferences.cleanCache();
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void doOkAction() {
                                ToastHelper.showToast(getBaseContext(), "重新登录");
                                NIMClient.getService(AuthService.class).login(new LoginInfo(Preferences.getUserAccount(), Preferences.getUserToken()));
                            }
                        });
            }
        };
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(observer, true);

        fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new FriendFragment());
        fragmentList.add(new MineFragment());

        BottomNavigationView navibar = findViewById(R.id.navibar);
        ViewPager2 pagerMain = findViewById(R.id.pagerMain);
        pagerMain.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getItemCount() {
                return fragmentList.size();
            }
        });
        pagerMain.setOffscreenPageLimit(fragmentList.size());
        pagerMain.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                navibar.setSelectedItemId(idList.get(position));
            }
        });
        pagerMain.setUserInputEnabled(false);
        navibar.setOnNavigationItemSelectedListener(item -> {
            pagerMain.setCurrentItem(idList.indexOf(item.getItemId()));
            return true;
        });
    }
}
