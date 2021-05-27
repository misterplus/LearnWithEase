package team.one.lwe.network;

import android.util.Log;
import android.view.View;

import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.http.HttpException;
import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;

public abstract class NetworkThread extends Thread {

    private final View view;

    public NetworkThread(View view) {
        this.view = view;
    }

    public abstract ASResponse doRequest();

    public abstract void onSuccess(ASResponse asp);

    public void onFailed(int code, String desc) {
        switch (code) {
            case 408: {
                ToastHelper.showToast(view.getContext(), R.string.lwe_error_timeout);
                return;
            }
            case 415: {
                ToastHelper.showToast(view.getContext(), R.string.lwe_error_confail);
                return;
            }
            default: {
                ToastHelper.showToast(view.getContext(), R.string.lwe_error_unknown);
            }
        }
    }

    public void onException(Exception e) {
        Log.e(view.getTransitionName(), Log.getStackTraceString(e));
    }

    @Override
    public void run() {
        try {
            if (!NetworkUtil.isNetAvailable(view.getContext())) {
                view.post(() -> {
                    DialogMaker.dismissProgressDialog();
                    ToastHelper.showToast(view.getContext(), R.string.lwe_error_nonetwork);
                });
                return;
            }
            ASResponse asp = doRequest();
            // All methods other than request run on ui thread
            view.post(() -> {
                if (asp.isSuccess()) {
                    onSuccess(asp);
                } else {
                    onFailed(asp.getCode(), asp.getDesc());
                }
            });
        } catch (IORuntimeException e) {
            view.post(() -> {
                if (e.causeInstanceOf(ConnectException.class) && e.getMessage().contains("Failed to connect to")) { //connection refused
                    onFailed(415, "connection failed");
                } else if (e.causeInstanceOf(SocketTimeoutException.class)) {
                    onFailed(408, "connection timeout");
                } else {
                    onException(e);
                }
            });
        } catch (HttpException e) {
            view.post(() -> {
                if ("Connection reset".equals(e.getMessage())) { //connection reset
                    onFailed(415, "connection failed");
                } else {
                    onException(e);
                }
            });
        } catch (Exception e) {
            view.post(() -> onException(e));
        }
    }
}