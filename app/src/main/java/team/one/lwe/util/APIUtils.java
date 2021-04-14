package team.one.lwe.util;

import com.netease.nimlib.sdk.auth.LoginInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.http.HttpResponse;
import lombok.NonNull;

public class APIUtils {

    public static LoginInfo convert(@NonNull String username, @NonNull String password) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("username", username);
        paramMap.put("password", password);
        HttpResponse resp = PostUtils.getBasicPost("/user/convert")
                .form(paramMap)
                .timeout(10000)
                .execute();
        try {
            JSONObject json = new JSONObject(resp.body());
            return new LoginInfo(json.get("accid").toString(), json.get("token").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
