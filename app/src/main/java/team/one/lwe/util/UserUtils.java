package team.one.lwe.util;

import android.content.res.Resources;

import com.google.gson.Gson;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;

import java.util.HashMap;
import java.util.Map;

import team.one.lwe.R;
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
    
    public static boolean isNameInvalid(String name) {
        return name.isEmpty() || name.length() > 16;
    }

    public static boolean isSignatureInvalid(String signature) {
        return signature.length() > 20;
    }

    public static boolean isAgeInvalid(int age) {
        return age < 1 || age > 120;
    }

    public static String[] getGradeValues(Resources resources, int i) {
        switch (i) {
            case 1: {
                return resources.getStringArray(R.array.lwe_spinner_grade_1);
            }
            case 2: {
                return resources.getStringArray(R.array.lwe_spinner_grade_2);
            }
            case 3: {
                return resources.getStringArray(R.array.lwe_spinner_grade_3);
            }
            case 4: {
                return resources.getStringArray(R.array.lwe_spinner_grade_4);
            }
            case 5: {
                return resources.getStringArray(R.array.lwe_spinner_grade_5);
            }
            case 6: {
                return resources.getStringArray(R.array.lwe_spinner_grade_6);
            }
            case 7: {
                return resources.getStringArray(R.array.lwe_spinner_grade_7);
            }
            default: {
                return resources.getStringArray(R.array.lwe_spinner_grade_0);
            }
        }
    }
}
