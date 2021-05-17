package team.one.lwe.ui.callback;

import android.util.Log;
import android.view.View;

import com.netease.nim.uikit.common.ToastHelper;

import team.one.lwe.R;

public class VoidSuccessCallback extends RegularCallback<Void> {

    public VoidSuccessCallback(View view) {
        super(view);
    }

    @Override
    public void onSuccess(Void param) {
        //callback that doesn't have a success response
    }

    @Override
    public void onFailed(int code) {
        switch (code) {
            case 408: {
                ToastHelper.showToast(view.getContext(), R.string.lwe_error_timeout);
                break;
            }
            case 415: {
                ToastHelper.showToast(view.getContext(), R.string.lwe_error_confail);
                break;
            }
            default: {
                ToastHelper.showToast(view.getContext(), R.string.lwe_error_unknown);
            }
        }
    }

    @Override
    public void onException(Throwable e) {
        Log.e(view.getTransitionName(), Log.getStackTraceString(e));
    }
}
