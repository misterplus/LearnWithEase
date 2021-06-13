package team.one.lwe.ui.activity.friend;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;

import java.util.List;

import team.one.lwe.R;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.adapter.BlackListAdapter;
import team.one.lwe.ui.wedget.LWEToolBarOptions;

public class ManageBlackListActivity extends LWEUI {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list_manage);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_black_list_manage, true);
        setToolBar(R.id.toolbar, options);

        LinearLayout noResult = findViewById(R.id.noResult);
        RecyclerView listBlackList = findViewById(R.id.listBlackList);
        LinearLayoutManager mRecyclerViewLayoutManager = new LinearLayoutManager(getBaseContext());
        List<String> blackList = NIMClient.getService(FriendService.class).getBlackList();

        if (blackList.size() == 0) {
            noResult.setVisibility(View.VISIBLE);
        } else {
            BlackListAdapter adapter = new BlackListAdapter(blackList);
            listBlackList.setLayoutManager(mRecyclerViewLayoutManager);
            listBlackList.setAdapter(adapter);
        }
    }
}
