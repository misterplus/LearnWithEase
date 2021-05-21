package team.one.lwe.util;

import com.google.gson.Gson;

import cn.hutool.core.io.IORuntimeException;
import lombok.NonNull;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.bean.User;

public class APIUtils {

    public static ASResponse convert(@NonNull String username, @NonNull String password) throws IORuntimeException {
        return PostUtils.doPostEncoded(
                "/user/convert", 5000,
                "username", username,
                "password", password);
    }

    public static ASResponse register(@NonNull User user) throws IORuntimeException {
        return PostUtils.doPostJson(
                "/user/register", 5000,
                new Gson().toJson(user));
    }

    public static ASResponse update(@NonNull String username, @NonNull String oldPassword, @NonNull String newPassword) throws IORuntimeException {
        return PostUtils.doPostEncoded(
                "/user/update", 5000,
                "username", username,
                "oldPassword", oldPassword,
                "newPassword", newPassword);
    }
}
