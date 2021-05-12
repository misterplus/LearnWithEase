package team.one.lwe.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.netease.nim.uikit.business.recent.RecentContactsFragment;
import com.netease.nim.uikit.common.activity.UI;

import team.one.lwe.R;
import team.one.lwe.ui.fragment.FriendFragment;
import team.one.lwe.ui.fragment.MineFragment;
import team.one.lwe.ui.wedget.LWEToolBarOptions;
import team.one.lwe.util.NavigationUtils;

public class MainActivity extends UI {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        BottomNavigationView navibar = findViewById(R.id.navibar);
//        navibar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.naviFriend: {
//                        getSupportFragmentManager()
//                                .beginTransaction()
//                                .replace(R.id.container, new RecentContactsFragment())
//                                .commit();
//                    }
//                }
//                return false;
//            }
//        });
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_friend, false);
        setToolBar(R.id.toolbar, options);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new FriendFragment())
                    .commit();
        }
    }
}
