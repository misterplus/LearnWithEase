package team.one.lwe.ui.callback;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.netease.nim.uikit.common.ToastHelper;

import team.one.lwe.R;

public class VoidSuccessCallback extends RegularCallback<Void> {

    public VoidSuccessCallback(Context context) {
        super(context);
    }

    @Override
    public void onSuccess(Void param) {
        //callback that doesn't have a success response
    }

    @Override
    public void onFailed(int code) {
        switch (code) {
            case 408: {
                ToastHelper.showToast(context, R.string.lwe_error_timeout);
                break;
            }
            case 415: {
                ToastHelper.showToast(context, R.string.lwe_error_confail);
                break;
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
