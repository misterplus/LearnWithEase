package team.one.lwe.ui.callback;

import android.content.Context;

import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nimlib.sdk.msg.model.SystemMessage;

import java.util.List;

import team.one.lwe.R;

public abstract class FetchFriendRequestCallback extends RegularCallback<List<SystemMessage>> {
    public FetchFriendRequestCallback(Context context) {
        super(context);
    }

    @Override
    public void onFailed(int code) {
        ToastHelper.showToast(context, R.string.lwe_error_fetch_friend_request);
    }
}
