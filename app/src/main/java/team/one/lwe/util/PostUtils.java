package team.one.lwe.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.NonNull;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.config.Preferences;

public class PostUtils {

    private static final boolean DEV_ENV = false;
    private static final String API_DOMAIN_NAME = DEV_ENV ? "http://10.0.2.2:8080" : "https://lwe.misterplus.plus:8443";

    // 10.0.2.2 is for localhost in emulator
    // real server deployed

    private static void withAuth(HttpRequest request) {
        request.header("accid", Preferences.getUserAccount())
                .header("token", Preferences.getUserToken());
    }

    public static ASResponse doPostEncoded(boolean withAuth, @NonNull String url, int timeout, @NonNull String name, @NonNull Object value, @NonNull Object... parameters) {
        return doPostForm(withAuth, "application/x-www-form-urlencoded;charset=utf-8", url, timeout, name, value, parameters);
    }

    public static ASResponse doPostEncoded(boolean withAuth, @NonNull String url, int timeout) {
        return doPostForm(withAuth, "application/x-www-form-urlencoded;charset=utf-8", url, timeout);
    }

    private static ASResponse doPostForm(boolean withAuth, String content, String url, int timeout) {
        HttpRequest req = HttpRequest.post(API_DOMAIN_NAME + url)
                .header("Content-Type", content)
                .timeout(timeout);
        if (withAuth)
            withAuth(req);
        HttpResponse resp = req.execute();
        return new ASResponse(resp.body());
    }

    public static ASResponse doPostJson(boolean withAuth, @NonNull String url, int timeout, @NonNull String body) {
        return doPostBody(withAuth, "application/json;charset=utf-8", url, timeout, body);
    }

    private static ASResponse doPostForm(boolean withAuth, String content, @NonNull String url, int timeout, @NonNull String name, @NonNull Object value, @NonNull Object... parameters) {
        HttpRequest req = HttpRequest.post(API_DOMAIN_NAME + url)
                .header("Content-Type", content)
                .form(name, value, parameters)
                .timeout(timeout);
        if (withAuth)
            withAuth(req);
        HttpResponse resp = req.execute();
        return new ASResponse(resp.body());
    }

    private static ASResponse doPostBody(boolean withAuth, String content, @NonNull String url, int timeout, @NonNull String body) {
        HttpRequest req = HttpRequest.post(API_DOMAIN_NAME + url)
                .header("Content-Type", content)
                .body(body)
                .timeout(timeout);
        if (withAuth)
            withAuth(req);
        HttpResponse resp = req.execute();
        return new ASResponse(resp.body());
    }
}
