package team.one.lwe.ui.callback;

import android.content.Context;

import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.List;

import team.one.lwe.R;

public abstract class MissingInfoCallback extends RegularCallback<List<NimUserInfo>> {

    public MissingInfoCallback(Context context) {
        super(context);
    }

    @Override
    public void onFailed(int code) {
        ToastHelper.showToast(context, R.string.lwe_error_fetch_info);
    }
}
