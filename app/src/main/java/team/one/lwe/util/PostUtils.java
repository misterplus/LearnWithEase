package team.one.lwe.util;

import cn.hutool.http.HttpRequest;
import lombok.NonNull;

public class PostUtils {

    private static final String API_DOMAIN_NAME = "http://10.0.2.2:8080"; // 10.0.2.2 is for localhost in emulator

    public static HttpRequest getBasicPost(@NonNull String url) {
        return HttpRequest.post(API_DOMAIN_NAME + url)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
    }
}
