package team.one.lwe.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.NonNull;
import team.one.lwe.bean.ASResponse;

public class PostUtils {

    private static final boolean DEV_ENV = true;
    private static final String API_DOMAIN_NAME = DEV_ENV ? "http://10.0.2.2:8080" : "https://lwe.misterplus.plus";
    // 10.0.2.2 is for localhost in emulator
    // real server not deployed yet
    public static ASResponse doPost(@NonNull String url, int timeout, @NonNull String name, @NonNull Object value, @NonNull Object... parameters) {
        HttpResponse resp = HttpRequest.post(API_DOMAIN_NAME + url)
                .header("Content-Type", "application/json;charset=utf-8")
                .form(name, value, parameters)
                .timeout(timeout)
                .execute();
        return new ASResponse(resp.body());
    }
}
