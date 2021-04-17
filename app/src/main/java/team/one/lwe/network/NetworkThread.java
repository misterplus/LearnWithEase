package team.one.lwe.network;

import android.view.View;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import cn.hutool.core.io.IORuntimeException;
import team.one.lwe.bean.ASResponse;

public abstract class NetworkThread extends Thread {

    private final View view;

    public NetworkThread(View view) {
        this.view = view;
    }

    public abstract ASResponse doRequest();

    public abstract void onSuccess(ASResponse asp);

    public abstract void onFailed(int code, String desc);

    public abstract void onException(IORuntimeException e);

    @Override
    public void run() {
        try {
            ASResponse asp = doRequest();
            // All methods other than request run on ui thread
            view.post(() -> {
                if (asp.isSuccess()) {
                    onSuccess(asp);
                }
                else {
                    onFailed(asp.getCode(), asp.getDesc());
                }
            });
        } catch (IORuntimeException e) {
            view.post(() -> {
                if (e.causeInstanceOf(ConnectException.class)) {
                    onFailed(415, "connection failed");
                }
                else if (e.causeInstanceOf(SocketTimeoutException.class)) {
                    onFailed(408, "connection timeout");
                }
                else {
                    onException(e);
                }
            });
        }
    }
}
