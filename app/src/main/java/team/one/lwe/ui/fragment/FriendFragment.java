package team.one.lwe.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.netease.nim.uikit.business.contact.ContactsFragment;
import com.netease.nim.uikit.business.recent.RecentContactsFragment;
import com.netease.nim.uikit.common.activity.UI;

import java.util.ArrayList;
import java.util.List;

import team.one.lwe.R;
import team.one.lwe.ui.wedget.LWEToolBarOptions;

public class FriendFragment extends Fragment {
    private View view;

    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO: finish friend fragment
        view = inflater.inflate(R.layout.fragment_friend, container, false);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_friend, false);
        ((UI)getActivity()).setToolBar(R.id.toolbar, options);
        TabLayout tabFriend = view.findViewById(R.id.tabFriend);
        ViewPager2 pagerFriend = view.findViewById(R.id.pagerFriend);
        fragmentList.add(new RecentContactsFragment());
        fragmentList.add(new ContactsFragment());
        pagerFriend.setAdapter(new FragmentStateAdapter(this) {
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
        new TabLayoutMediator(tabFriend, pagerFriend, (tab, position) -> tab.setText(getResources().getStringArray(R.array.lwe_pager_friend)[position])).attach();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.lwe_menu_friend,menu);
    }
}
