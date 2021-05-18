package team.one.lwe.ui.activity;

import android.os.Bundle;

import com.netease.nim.uikit.common.activity.UI;

import team.one.lwe.R;
import team.one.lwe.ui.fragment.FriendFragment;

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


        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new FriendFragment())
                    .commit();
        }
    }
}
