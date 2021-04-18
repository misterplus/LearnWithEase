package team.one.lwe.util;

import cn.hutool.core.io.IORuntimeException;
import lombok.NonNull;
import team.one.lwe.bean.ASResponse;

public class APIUtils {

    public static ASResponse convert(@NonNull String username, @NonNull String password) throws IORuntimeException {
        return PostUtils.doPost(
                "/user/convert", 5000,
                "username", username,
                "password", password);
    }

    public static ASResponse register(@NonNull String username, @NonNull String password) throws IORuntimeException {
        return PostUtils.doPost(
                "/user/register", 5000,
                "username", username,
                "password", password);
    }
}
