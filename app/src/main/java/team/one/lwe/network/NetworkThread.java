package team.one.lwe.network;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import cn.hutool.core.io.IORuntimeException;
import team.one.lwe.bean.ASResponse;

public abstract class NetworkThread extends Thread {

    public abstract ASResponse doRequest();
    public abstract void onSuccess(ASResponse asp);
    public abstract void onFailed(int code, String desc);
    public abstract void onConnectionFailed(ConnectException e);
    public abstract void onTimeout(SocketTimeoutException e);

    @Override
    public void run() {
        try {
            ASResponse asp = doRequest();
            if (asp.isSuccess()) {
                onSuccess(asp);
            }
            else {
                onFailed(asp.getCode(), asp.getDesc());
            }
        } catch (IORuntimeException e) {
            if (e.causeInstanceOf(ConnectException.class)) {
                onConnectionFailed((ConnectException) e.getCause());
            }
            else if (e.causeInstanceOf(SocketTimeoutException.class)) {
                onTimeout((SocketTimeoutException) e.getCause());
            }
        }
    }
}
