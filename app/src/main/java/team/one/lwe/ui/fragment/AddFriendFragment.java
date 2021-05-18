package team.one.lwe.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
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
import team.one.lwe.util.NavigationUtils;
import team.one.lwe.util.UserUtils;

import static com.netease.nimlib.sdk.friend.model.AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST;

public class AddFriendFragment extends Fragment {

    private View view;
    private String searchedAccount;

    private void onNavigateUpClicked() {
        getActivity().onBackPressed();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend_add, container, false);
        getActivity().findViewById(R.id.app_bar_layout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.navibar).setVisibility(View.GONE);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> onNavigateUpClicked());
        EditText editTextSearchUsername = view.findViewById(R.id.editTextSearchUsername);
        Button buttonAdd = view.findViewById(R.id.buttonAdd);
        TextView textAdd = view.findViewById(R.id.textAdd);
        LinearLayout searchResult = view.findViewById(R.id.searchResult);
        LinearLayout noResult = view.findViewById(R.id.noResult);

        editTextSearchUsername.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                DialogMaker.showProgressDialog(getContext(), getString(R.string.lwe_progress_search));
                NIMClient.getService(UserService.class).fetchUserInfo(Collections.singletonList(editTextSearchUsername.getText().toString())).setCallback(new RegularCallback<List<NimUserInfo>>(view) {
                    @Override
                    public void onSuccess(List<NimUserInfo> list) {
                        if (!list.isEmpty()) {
                            NimUserInfo info = list.get(0);
                            searchedAccount = info.getAccount();
                            RoundedImageView imageAvatar = view.findViewById(R.id.imageAvatar);
                            TextView textName = view.findViewById(R.id.textName);
                            TextView textSignature = view.findViewById(R.id.textSignature);
                            imageAvatar.setImageURI(UserUtils.getAvatarUri(view, searchedAccount, info.getAvatar()));
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
                DialogMaker.dismissProgressDialog();
                return true;
            }
            return false;
        });
        buttonAdd.setOnClickListener(v -> {
            NavigationUtils.navigateTo(this, new AddVerifyFragment(searchedAccount), true);
        });
        //TODO: load incoming requests
        NIMClient.getService(SystemMessageService.class).querySystemMessageUnread().setCallback(new RegularCallback<List<SystemMessage>>(view) {
            @Override
            public void onSuccess(List<SystemMessage> msg) {
                List<SystemMessage> requests = new ArrayList<>();
                List<String> accounts = new ArrayList<>();
                for (SystemMessage m : msg) {
                    if (m.getAttachObject() instanceof AddFriendNotify && ((AddFriendNotify) m.getAttachObject()).getEvent() == RECV_ADD_FRIEND_VERIFY_REQUEST) {
                        requests.add(m);
                        accounts.add(((AddFriendNotify) m.getAttachObject()).getAccount());
                    }
                }
                NIMClient.getService(UserService.class).fetchUserInfo(accounts).setCallback(new RegularCallback<List<NimUserInfo>>(view) {
                    @Override
                    public void onSuccess(List<NimUserInfo> infoList) {
                        RecyclerView listRequest = view.findViewById(R.id.listRequest);
                        FriendRequestAdapter adapter = new FriendRequestAdapter(requests);
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
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().findViewById(R.id.app_bar_layout).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.navibar).setVisibility(View.VISIBLE);
    }
}