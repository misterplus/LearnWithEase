package team.one.lwe.util;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.nos.model.NosThumbParam;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

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

    public static Uri getAvatar(View view, String account) {
        NimUserInfo user = NIMClient.getService(UserService.class).getUserInfo(account);
        NosThumbParam nosThumbParam = new NosThumbParam();
        nosThumbParam.height = 100;
        nosThumbParam.width = 100;
        File head = new File(view.getContext().getExternalCacheDir() + "/avatar", account + user.getAvatar());
        try {
            if (!head.exists())
                head.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        NIMClient.getService(NosService.class).download(user.getAvatar(), nosThumbParam, head.getAbsolutePath());
        if (Build.VERSION.SDK_INT >= 24)
            return FileProvider.getUriForFile(view.getContext(), "team.one.lwe.ipc.provider.file", head);
        else
            return Uri.fromFile(head);
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
