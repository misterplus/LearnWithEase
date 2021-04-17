package team.one.lwe.util;

import com.netease.nimlib.sdk.auth.LoginInfo;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import lombok.NonNull;
import team.one.lwe.bean.ASResponse;

public class APIUtils {

    public static ASResponse convert(@NonNull String username, @NonNull String password) throws IORuntimeException {
        HttpResponse resp = PostUtils.getBasicPost("/user/convert")
                .form(
                        "username", username,
                        "password", password
                )
                .timeout(5000)
                .execute();
        return new ASResponse(new JSONObject(resp.body()));
    }
}
