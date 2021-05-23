package team.one.lwe.ui.callback;

import android.content.Context;
import android.util.Log;

import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nimlib.sdk.RequestCallback;

import lombok.AllArgsConstructor;
import team.one.lwe.R;

@AllArgsConstructor
public abstract class RegularCallback<T> implements RequestCallback<T> {

    protected final Context context;

    public abstract void onSuccess(T param);

    @Override
    public void onFailed(int code) {
        switch (code) {
            case 408: {
                ToastHelper.showToast(context, R.string.lwe_error_timeout);
                return;
            }
            case 415: {
                ToastHelper.showToast(context, R.string.lwe_error_connection);
                return;
            }
            case 416: {
                ToastHelper.showToast(context, R.string.lwe_error_frequently);
                return;
            }
            case 500: {
                ToastHelper.showToast(context, R.string.lwe_error_confail);
                return;
            }
            default: {
                ToastHelper.showToast(context, R.string.lwe_error_unknown);
            }
        }
    }

    @Override
    public void onException(Throwable e) {
        Log.e(context.getPackageName(), Log.getStackTraceString(e));
    }
}
