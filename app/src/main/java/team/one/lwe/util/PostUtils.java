package team.one.lwe.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.NonNull;
import team.one.lwe.bean.ASResponse;

public class PostUtils {

    private static final boolean DEV_ENV = false;
    private static final String API_DOMAIN_NAME = DEV_ENV ? "http://10.0.2.2:8080" : "https://lwe.misterplus.plus:8443";

    // 10.0.2.2 is for localhost in emulator
    // real server deployed
    public static ASResponse doPostEncoded(@NonNull String url, int timeout, @NonNull String name, @NonNull Object value, @NonNull Object... parameters) {
        return doPostForm("application/x-www-form-urlencoded;charset=utf-8", url, timeout, name, value, parameters);
    }

    public static ASResponse doPostJson(@NonNull String url, int timeout, @NonNull String body) {
        return doPostBody("application/json;charset=utf-8", url, timeout, body);
    }

    private static ASResponse doPostForm(String content, @NonNull String url, int timeout, @NonNull String name, @NonNull Object value, @NonNull Object... parameters) {
        HttpResponse resp = HttpRequest.post(API_DOMAIN_NAME + url)
                .header("Content-Type", content)
                .form(name, value, parameters)
                .timeout(timeout)
                .execute();
        return new ASResponse(resp.body());
    }

    private static ASResponse doPostBody(String content, @NonNull String url, int timeout, @NonNull String body) {
        HttpResponse resp = HttpRequest.post(API_DOMAIN_NAME + url)
                .header("Content-Type", content)
                .body(body)
                .timeout(timeout)
                .execute();
        return new ASResponse(resp.body());
    }
}
