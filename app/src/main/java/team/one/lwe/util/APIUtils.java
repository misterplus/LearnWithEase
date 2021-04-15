package team.one.lwe.util;

import com.netease.nimlib.sdk.auth.LoginInfo;

import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import lombok.NonNull;

public class APIUtils {

    public static LoginInfo convert(@NonNull String username, @NonNull String password) {
        HttpResponse resp = PostUtils.getBasicPost("/user/convert")
                .form(
                        "username", username,
                        "password", password
                )
                .timeout(10000)
                .execute();
        JSONObject info = new JSONObject(resp.body()).getJSONObject("info");
        return new LoginInfo(info.getStr("accid"), info.getStr("token"));
    }
}
