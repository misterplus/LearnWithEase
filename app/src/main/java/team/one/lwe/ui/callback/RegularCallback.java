package team.one.lwe.ui.callback;

import android.util.Log;
import android.view.View;

import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nimlib.sdk.RequestCallback;

import lombok.AllArgsConstructor;
import team.one.lwe.R;

@AllArgsConstructor
public abstract class RegularCallback<T> implements RequestCallback<T> {

    protected final View view;

    public abstract void onSuccess(T param);

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
