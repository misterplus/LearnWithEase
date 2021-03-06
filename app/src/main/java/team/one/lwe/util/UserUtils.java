package team.one.lwe.util;

import android.content.res.Resources;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.json.JSONObject;
import team.one.lwe.R;
import team.one.lwe.bean.UserInfo;

public class UserUtils {

    private static final int[] GRADE_ARRAYS = {R.array.lwe_spinner_grade_0, R.array.lwe_spinner_grade_1, R.array.lwe_spinner_grade_2, R.array.lwe_spinner_grade_3, R.array.lwe_spinner_grade_4, R.array.lwe_spinner_grade_5, R.array.lwe_spinner_grade_6, R.array.lwe_spinner_grade_7};

    public static boolean isUsernameValid(@NotNull String username) {
        return TextUtils.isLegalUsername(username) && username.length() >= 6 && username.length() <= 16;
    }

    public static boolean isPasswordValid(@NotNull String password) {
        return TextUtils.isLegalPassword(password) && TextUtils.getPasswordComplexity(password) > 1 && password.length() >= 6 && password.length() <= 16;
    }

    public static LoginInfo getLoginInfo(JSONObject info) {
        return new LoginInfo(info.getStr("accid"), info.getStr("token"));
    }

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

    public static void updateUserSignature(TextView view, String signature) {
        view.setText(signature);
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

    /*@Deprecated
    public static void setAvatar(RoundedImageView view, String url) {
        File avatar = new File(view.getContext().getExternalCacheDir() + "/avatar", String.format("%s.png", url));
        NosThumbParam nosThumbParam = new NosThumbParam();
        nosThumbParam.height = 100;
        nosThumbParam.width = 100;
        if (avatar.exists()) {
            Uri uri;
            if (Build.VERSION.SDK_INT >= 24)
                uri = FileProvider.getUriForFile(view.getContext(), "team.one.lwe.ipc.provider.file", avatar);
            else
                uri = Uri.fromFile(avatar);
            view.setImageURI(uri);
        } else {
            NIMClient.getService(NosService.class).download(url, nosThumbParam, avatar.getAbsolutePath()).setCallback(new RegularCallback<Void>(view.getContext()) {
                @Override
                public void onSuccess(Void param) {
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= 24)
                        uri = FileProvider.getUriForFile(view.getContext(), "team.one.lwe.ipc.provider.file", avatar);
                    else
                        uri = Uri.fromFile(avatar);
                    view.setImageURI(uri);
                }
            });
        }
    }*/

    public static InvocationFuture<Void> updateUserAvatar(String url) {
        Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
        fields.put(UserInfoFieldEnum.AVATAR, url);
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
        return resources.getStringArray(GRADE_ARRAYS[i]);
    }
}
