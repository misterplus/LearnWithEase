package team.one.lwe.ui.activity.friend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.SystemMessageService;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import team.one.lwe.R;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.adapter.FriendRequestAdapter;
import team.one.lwe.ui.callback.FetchFriendRequestCallback;
import team.one.lwe.ui.callback.MissingInfoCallback;
import team.one.lwe.ui.callback.RegularCallback;
import team.one.lwe.util.SystemMsgUtils;

import static com.netease.nimlib.sdk.friend.model.AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST;

public class AddFriendActivity extends LWEUI {

    private String searchedAccount;
    private RecyclerView listRequest;
    private LinearLayoutManager mRecyclerViewLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((R.layout.activity_friend_add));
        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> {
            AddFriendActivity.this.finish();
        });
        EditText editTextSearchUsername = findViewById(R.id.editTextSearchUsername);
        Button buttonAdd = findViewById(R.id.buttonAdd);
        TextView textAdd = findViewById(R.id.textAdd);
        LinearLayout searchResult = findViewById(R.id.searchResult);
        LinearLayout noResult = findViewById(R.id.noResult);
        listRequest = findViewById(R.id.listRequest);
        mRecyclerViewLayoutManager = new LinearLayoutManager(getBaseContext());
        final boolean[] isBeingAdded = {false};
        List<SystemMessage> listBeingAdded = new ArrayList<>();

        editTextSearchUsername.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                DialogMaker.showProgressDialog(this, getString(R.string.lwe_progress_search));
                NIMClient.getService(UserService.class).fetchUserInfo(Collections.singletonList(editTextSearchUsername.getText().toString())).setCallback(new MissingInfoCallback(this) {
                    @Override
                    public void onSuccess(List<NimUserInfo> list) {
                        if (!list.isEmpty()) {
                            NimUserInfo info = list.get(0);
                            searchedAccount = info.getAccount();
                            HeadImageView imageAvatar = findViewById(R.id.imageAvatar);
                            TextView textName = findViewById(R.id.textName);
                            TextView textSignature = findViewById(R.id.textSignature);
                            imageAvatar.loadBuddyAvatar(searchedAccount);
                            textName.setText(String.format("%s(%s)", info.getName(), info.getAccount()));
                            textSignature.setText(info.getSignature());
                            boolean isMyFriend = NIMClient.getService(FriendService.class).isMyFriend(info.getAccount()) || info.getAccount().equals(NimUIKit.getAccount());
                            if (isMyFriend) {
                                textAdd.setVisibility(View.VISIBLE);
                                buttonAdd.setVisibility(View.GONE);
                            } else {
                                textAdd.setVisibility(View.GONE);
                                buttonAdd.setVisibility(View.VISIBLE);
                            }
                            searchResult.setVisibility(View.VISIBLE);
                            noResult.setVisibility(View.GONE);
                        } else {
                            searchResult.setVisibility(View.GONE);
                            noResult.setVisibility(View.VISIBLE);
                        }
                    }
                });
                NIMClient.getService(SystemMessageService.class).querySystemMessageUnread().setCallback(new FetchFriendRequestCallback(this) {
                    @Override
                    public void onSuccess(List<SystemMessage> msg) {
                        for (SystemMessage m : msg) {
                            if (m.getAttachObject() instanceof AddFriendNotify && ((AddFriendNotify) m.getAttachObject()).getEvent() == RECV_ADD_FRIEND_VERIFY_REQUEST && ((AddFriendNotify) m.getAttachObject()).getAccount().equals(searchedAccount)) {
                                isBeingAdded[0] = true;
                                listBeingAdded.add(m);
                            }
                        }
                    }
                });
                DialogMaker.dismissProgressDialog();
                return true;
            }
            return false;
        });
        buttonAdd.setOnClickListener(v -> {
            if (isBeingAdded[0]) {
                NIMClient.getService(FriendService.class).addFriend(new AddFriendData(searchedAccount, VerifyType.DIRECT_ADD, "")).setCallback(new RegularCallback<Void>(this) {
                    @Override
                    public void onSuccess(Void param) {
                        textAdd.setVisibility(View.VISIBLE);
                        buttonAdd.setVisibility(View.GONE);
                    }
                });
                for (SystemMessage m : listBeingAdded) {
                    NIMClient.getService(SystemMessageService.class).setSystemMessageRead(m.getMessageId());
                }
            } else {
                Intent intent = new Intent(this, AddVerifyActivity.class);
                intent.putExtra("account", searchedAccount);
                startActivityForResult(intent, 0);
            }
        });
        NIMClient.getService(SystemMessageService.class).querySystemMessageUnread().setCallback(new FetchFriendRequestCallback(this) {
            @Override
            public void onSuccess(List<SystemMessage> msg) {
                List<SystemMessage> requests = new ArrayList<>();
                List<String> accounts = new ArrayList<>();
                for (SystemMessage m : msg) {
                    Object o = m.getAttachObject();
                    if (o instanceof AddFriendNotify) {
                        AddFriendNotify afn = (AddFriendNotify) o;
                        if (afn.getEvent() == RECV_ADD_FRIEND_VERIFY_REQUEST) {
                            requests.add(m);
                            accounts.add(afn.getAccount());
                        }
                    }
                }
                SystemMsgUtils.deduplicateFriendRequests(requests);
                NIMClient.getService(UserService.class).fetchUserInfo(accounts).setCallback(new MissingInfoCallback(getBaseContext()) {
                    @Override
                    public void onSuccess(List<NimUserInfo> infoList) {
                        FriendRequestAdapter adapter = new FriendRequestAdapter(requests);
                        listRequest.setLayoutManager(mRecyclerViewLayoutManager);
                        listRequest.setAdapter(adapter);
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            finish();
        } else if (requestCode == 1 && resultCode == 2) {
            int first = mRecyclerViewLayoutManager.findFirstVisibleItemPosition();
            int position = Integer.parseInt(data.getStringExtra("position"));
            String request = data.getStringExtra("request");
            View view = listRequest.getChildAt(position - first);
            if (listRequest.getChildViewHolder(view) instanceof FriendRequestAdapter.ViewHolder) {
                //修改数据
                Button buttonAccept = view.findViewById(R.id.buttonAccept);
                Button buttonDecline = view.findViewById(R.id.buttonDecline);
                TextView textAccept = view.findViewById(R.id.textAccept);
                TextView textDecline = view.findViewById(R.id.textDecline);
                buttonAccept.setVisibility(View.GONE);
                buttonDecline.setVisibility(View.GONE);
                if (request.equals("accept")) {
                    textAccept.setVisibility(View.VISIBLE);
                } else if (request.equals("decline")) {
                    textDecline.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}