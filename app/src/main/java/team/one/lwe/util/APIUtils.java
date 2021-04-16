package team.one.lwe.util;

import com.netease.nimlib.sdk.auth.LoginInfo;

import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import lombok.NonNull;
import team.one.lwe.bean.ASResponse;

public class APIUtils {

    public static LoginInfo convert(@NonNull String username, @NonNull String password) {
        HttpResponse resp = PostUtils.getBasicPost("/user/convert")
                .form(
                        "username", username,
                        "password", password
                )
                .timeout(10000)
                .execute();
        // TODO: add status code check
        switch (resp.getStatus()) {
            case 200: {
                ASResponse asp = new ASResponse(new JSONObject(resp.body()));
                if (asp.isSuccess()) {
                    return new LoginInfo(asp.getInfo().getStr("accid"), asp.getInfo().getStr("token"));
                }
                else {

                }
            }
            default: {
                return null;
            }
        }
    }
}
