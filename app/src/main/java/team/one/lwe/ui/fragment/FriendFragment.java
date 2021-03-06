package team.one.lwe.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.netease.nim.uikit.business.contact.ContactsFragment;
import com.netease.nim.uikit.business.recent.RecentContactsFragment;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.popupmenu.NIMPopupMenu;
import com.netease.nim.uikit.common.ui.popupmenu.PopupMenuItem;

import java.util.ArrayList;
import java.util.List;

import team.one.lwe.R;
import team.one.lwe.ui.activity.friend.AddFriendActivity;
import team.one.lwe.ui.activity.friend.BlackListActivity;
import team.one.lwe.ui.wedget.LWEToolBarOptions;

public class FriendFragment extends Fragment {
    private View view;

    private List<Fragment> fragmentList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend, container, false);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_friend, false);
        ((UI) getActivity()).setToolBar(view, R.id.toolbar, options);
        RelativeLayout buttonsFriend = view.findViewById(R.id.buttonsFriend);
        TabLayout tabFriend = view.findViewById(R.id.tabFriend);
        ViewPager2 pagerFriend = view.findViewById(R.id.pagerFriend);
        fragmentList = new ArrayList<>();
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
        ImageButton buttonFriendAdd = buttonsFriend.findViewById(R.id.buttonFriendAdd);
        List<PopupMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new PopupMenuItem(0, "????????????"));
        menuItems.add(new PopupMenuItem(1, "???????????????"));
        NIMPopupMenu menu = new NIMPopupMenu(getContext(), menuItems, item -> {
            switch (item.getTag()) {
                case 0: {
                    startActivity(new Intent(getContext(), AddFriendActivity.class));
                    break;
                }
                case 1: {
                    startActivity(new Intent(getContext(), BlackListActivity.class));
                }
            }
        });
        buttonFriendAdd.setOnClickListener(v -> menu.show(view.findViewById(R.id.buttonFriendAdd)));

        return view;
    }
}
