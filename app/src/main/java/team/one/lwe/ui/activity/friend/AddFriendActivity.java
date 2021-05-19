package team.one.lwe.ui.activity.friend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
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
import team.one.lwe.ui.adapter.FriendRequestAdapter;
import team.one.lwe.ui.callback.RegularCallback;
import team.one.lwe.util.UserUtils;

import static com.netease.nimlib.sdk.friend.model.AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST;

public class AddFriendActivity extends UI {

    private String searchedAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((R.layout.activity_friend_add));
        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> onBackPressed());
        EditText editTextSearchUsername = findViewById(R.id.editTextSearchUsername);
        Button buttonAdd = findViewById(R.id.buttonAdd);
        TextView textAdd = findViewById(R.id.textAdd);
        LinearLayout searchResult = findViewById(R.id.searchResult);
        LinearLayout noResult = findViewById(R.id.noResult);
        final boolean[] isBeingAdded = {false};
        List<SystemMessage> listBeingAdded = new ArrayList<>();

        editTextSearchUsername.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                DialogMaker.showProgressDialog(this, getString(R.string.lwe_progress_search));
                NIMClient.getService(UserService.class).fetchUserInfo(Collections.singletonList(editTextSearchUsername.getText().toString())).setCallback(new RegularCallback<List<NimUserInfo>>(this) {
                    @Override
                    public void onSuccess(List<NimUserInfo> list) {
                        if (!list.isEmpty()) {
                            NimUserInfo info = list.get(0);
                            searchedAccount = info.getAccount();
                            RoundedImageView imageAvatar = findViewById(R.id.imageAvatar);
                            TextView textName = findViewById(R.id.textName);
                            TextView textSignature = findViewById(R.id.textSignature);
                            UserUtils.setAvatar(imageAvatar, info.getAvatar());
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
                NIMClient.getService(SystemMessageService.class).querySystemMessageUnread().setCallback(new RegularCallback<List<SystemMessage>>(this) {
                    @Override
                    public void onSuccess(List<SystemMessage> msg) {
                        for (SystemMessage m : msg) {
                            if (m.getAttachObject() instanceof AddFriendNotify && ((AddFriendNotify) m.getAttachObject()).getEvent() == RECV_ADD_FRIEND_VERIFY_REQUEST && ((AddFriendNotify) m.getAttachObject()).getAccount().equals(searchedAccount)) {
                                isBeingAdded[0] = true;
                                listBeingAdded.add(m);
                            }
                        }
                    }

                    @Override
                    public void onFailed(int code) {
                        //TODO: handle failed request
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
                startActivity(intent);
            }
        });
        NIMClient.getService(SystemMessageService.class).querySystemMessageUnread().setCallback(new RegularCallback<List<SystemMessage>>(this) {
            @Override
            public void onSuccess(List<SystemMessage> msg) {
                //TODO: deduplicate incoming requests
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
                //SystemMsgUtils.deduplicateFriendRequests(requests);
                NIMClient.getService(UserService.class).fetchUserInfo(accounts).setCallback(new RegularCallback<List<NimUserInfo>>(getBaseContext()) {
                    @Override
                    public void onSuccess(List<NimUserInfo> infoList) {
                        RecyclerView listRequest = findViewById(R.id.listRequest);
                        FriendRequestAdapter adapter = new FriendRequestAdapter(requests);
                        listRequest.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                        listRequest.setAdapter(adapter);
                    }

                    @Override
                    public void onFailed(int code) {
                        //TODO: handle failed request
                    }
                });
            }

            @Override
            public void onFailed(int code) {
                //TODO: handle failed request
            }
        });
    }
}