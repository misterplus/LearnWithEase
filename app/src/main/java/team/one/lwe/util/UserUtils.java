package team.one.lwe.util;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.nos.model.NosThumbParam;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;

import java.io.File;
import java.io.IOException;
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

    public static Uri getAvatarUri(View view, String account, String url) {
        //TODO: local url caching
        NosThumbParam nosThumbParam = new NosThumbParam();
        nosThumbParam.height = 100;
        nosThumbParam.width = 100;
        File avatar = new File(view.getContext().getExternalCacheDir() + "/avatar", String.format("avatar_%s", account));
        try {
            if (!avatar.exists())
                avatar.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO: callback
        NIMClient.getService(NosService.class).download(url, nosThumbParam, avatar.getAbsolutePath());
        if (Build.VERSION.SDK_INT >= 24)
            return FileProvider.getUriForFile(view.getContext(), "team.one.lwe.ipc.provider.file", avatar);
        else
            return Uri.fromFile(avatar);
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
