package team.one.lwe.ui.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import team.one.lwe.R;
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
