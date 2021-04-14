package team.one.lwe.util;

import cn.hutool.http.HttpRequest;
import lombok.NonNull;

public class PostUtils {

    private static final String API_DOMAIN_NAME = "http://localhost:8080";

    public static HttpRequest getBasicPost(@NonNull String url) {
        return HttpRequest.post(API_DOMAIN_NAME + url)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
    }
}
