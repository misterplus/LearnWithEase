package team.one.lwe.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import java.util.ArrayList;
import java.util.List;

import team.one.lwe.R;
import team.one.lwe.ui.wedget.LWEToolBarOptions;
import team.one.lwe.util.NavigationUtils;

public class FriendFragment extends Fragment {
    private View view;

    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().findViewById(R.id.buttonsFriend).setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO: finish friend fragment
        view = inflater.inflate(R.layout.fragment_friend, container, false);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_friend, false);
        ((UI)getActivity()).setToolBar(R.id.toolbar, options);
        RelativeLayout buttonsFriend = getActivity().findViewById(R.id.buttonsFriend);
        buttonsFriend.setVisibility(View.VISIBLE);
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
        new TabLayoutMediator(tabFriend, pagerFriend, (tab, position) -> tab.setText(getResources().getStringArray(R.array.lwe_pager_friend)[position]));
        ImageButton buttonFriendAdd = buttonsFriend.findViewById(R.id.buttonFriendAdd);
        buttonFriendAdd.setOnClickListener(v -> NavigationUtils.navigateTo(this, new AddFriendFragment(), true));
        return view;
    }
}
