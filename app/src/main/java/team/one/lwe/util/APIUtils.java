package team.one.lwe.util;

import com.google.gson.Gson;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import cn.hutool.core.io.IORuntimeException;
import lombok.NonNull;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.bean.RoomBasic;
import team.one.lwe.bean.User;
import team.one.lwe.bean.UserInfo;
import team.one.lwe.config.Preferences;

public class APIUtils {

    public static ASResponse convert(@NonNull String username, @NonNull String password) throws IORuntimeException {
        return PostUtils.doPostEncoded(false,
                "/user/convert", 5000,
                "username", username,
                "password", password);
    }

    public static ASResponse register(@NonNull User user) throws IORuntimeException {
        return PostUtils.doPostJson(false,
                "/user/register", 5000,
                new Gson().toJson(user));
    }

    public static ASResponse update(@NonNull String username, @NonNull String oldPassword, @NonNull String newPassword) throws IORuntimeException {
        return PostUtils.doPostEncoded(false,
                "/user/update", 5000,
                "username", username,
                "oldPassword", oldPassword,
                "newPassword", newPassword);
    }

    public static ASResponse getRoomToken(@NonNull String roomId) throws IORuntimeException {
        return PostUtils.doPostEncoded(true,
                "/room/getToken", 5000,
                "roomId", roomId);
    }

    public static ASResponse createRoom(@NonNull RoomBasic room) throws IORuntimeException {
        return PostUtils.doPostJson(true,
                "/room/create", 5000,
                new Gson().toJson(room));
    }

    public static ASResponse getAccid(long uid) throws IORuntimeException {
        return PostUtils.doPostEncoded(false,
                "/user/getAccid", 5000,
                "uid", uid);
    }

    public static ASResponse getUid(@NonNull String accid) throws IORuntimeException {
        return PostUtils.doPostEncoded(false,
                "/user/getUid", 5000,
                "accid", accid);
    }
    public static ASResponse fetchRecs() throws IORuntimeException {
        User user = new User();
        NimUserInfo info = NIMClient.getService(UserService.class).getUserInfo(Preferences.getUserAccount());
        user.setEx(new Gson().fromJson(info.getExtension(), UserInfo.class));
        user.setGender(info.getGenderEnum().getValue());
        return PostUtils.doPostJson(true,
                "/room/fetch", 5000,
                new Gson().toJson(user));
    }
}
