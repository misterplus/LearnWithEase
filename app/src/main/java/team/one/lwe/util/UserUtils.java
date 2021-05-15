package team.one.lwe.util;

import com.google.gson.Gson;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;

import java.util.HashMap;
import java.util.Map;

import team.one.lwe.bean.UserInfo;

public class UserUtils {

    public static InvocationFuture<Void> updateUserNickName(String nickname) {
        Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
        fields.put(UserInfoFieldEnum.Name, nickname);
        return NIMClient.getService(UserService.class).updateUserInfo(fields);
    }

    public static InvocationFuture<Void> updateUserSignature(String signature) {
        Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
        fields.put(UserInfoFieldEnum.SIGNATURE, signature);
        return NIMClient.getService(UserService.class).updateUserInfo(fields);
    }

    public static InvocationFuture<Void> updateUserGender(int gender) {
        Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
        fields.put(UserInfoFieldEnum.GENDER, gender);
        return NIMClient.getService(UserService.class).updateUserInfo(fields);
    }

    public static InvocationFuture<Void> updateUserExtension(UserInfo userExtension) {
        String extension = new Gson().toJson(userExtension);
        Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
        fields.put(UserInfoFieldEnum.EXTEND, extension);
        return NIMClient.getService(UserService.class).updateUserInfo(fields);
    }
}
