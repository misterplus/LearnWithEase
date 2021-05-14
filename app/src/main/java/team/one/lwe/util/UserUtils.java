package team.one.lwe.util;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;

import java.util.HashMap;
import java.util.Map;

public class UserUtils {

    public static void updateUserNickName(String nickname) {
        Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
        fields.put(UserInfoFieldEnum.Name, nickname);
        NIMClient.getService(UserService.class).updateUserInfo(fields);
    }

    public static void updateUerSignature(String signature){
        Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
        fields.put(UserInfoFieldEnum.SIGNATURE, signature);
        NIMClient.getService(UserService.class).updateUserInfo(fields);
    }

    public static void updateUerGender(int gender){
        Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
        fields.put(UserInfoFieldEnum.GENDER, gender);
        NIMClient.getService(UserService.class).updateUserInfo(fields);
    }

    public static void updateUerExtension(String extension){
        Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
        fields.put(UserInfoFieldEnum.EXTEND, extension);
        NIMClient.getService(UserService.class).updateUserInfo(fields);
    }
}
