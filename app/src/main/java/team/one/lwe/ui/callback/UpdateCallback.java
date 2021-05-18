package team.one.lwe.ui.callback;

import android.util.Log;
import android.view.View;

import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nimlib.sdk.RequestCallback;

import team.one.lwe.R;

public class UpdateCallback<T> implements RequestCallback<T> {

    private final View view;

    public UpdateCallback(View view) {
        this.view = view;
    }

    @Override
    public void onSuccess(T param) {
        Log.i(view.getTransitionName(), "update success");
    }

    @Override
    public void onFailed(int code) {
        switch (code) {
            case 408: {
                ToastHelper.showToast(view.getContext(), R.string.lwe_error_timeout);
                break;
            }
            case 415: {
                ToastHelper.showToast(view.getContext(), R.string.lwe_error_connection);
                break;
            }
            case 416: {
                ToastHelper.showToast(view.getContext(), R.string.lwe_error_frequently);
                break;
            }
            case 500: {
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
