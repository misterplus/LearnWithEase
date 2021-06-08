package team.one.lwe;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;

import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.util.NIMUtil;

import java.io.File;

import team.one.lwe.config.Preferences;
import team.one.lwe.crash.CrashHandler;
import team.one.lwe.ui.activity.auth.LoginActivity;
import team.one.lwe.ui.callback.LWENERtcCallback;

public class LWEApplication extends Application {

    private static LWEApplication instance;

    public static LWEApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        NIMClient.init(this, loginInfo(), options());
        if (NIMUtil.isMainProcess(this)) {
            initUiKit();
            observer();
        }
        initCacheDir();
        CrashHandler crashHandler = CrashHandler.getCrashHandler();
        crashHandler.init();
    }

    private void initCacheDir() {
        File avatar = new File(getApplicationContext().getExternalCacheDir() + "/avatar");
        if (!avatar.exists())
            avatar.mkdir();
    }

    private void initUiKit() {
        NimUIKit.init(this);
        //SessionHelper.init();
        //ContactHelper.init();
        //NimUIKit.setOnlineStateContentProvider(new DemoOnlineStateContentProvider());
    }

    private void observer() {
        //登录状态监听
        Observer<StatusCode> observer = new Observer<StatusCode>() {
            @Override
            public void onEvent(StatusCode statusCode) {
                if (statusCode == StatusCode.KICKOUT) {
                    //ToastHelper.showToast(LWEApplication.this, "被踢了........");
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
//                    EasyAlertDialogHelper.createOkCancelDiolag(LWEApplication.this.getBaseContext(), "提示", "您的帐号已在另一台设备登录，是否重新登录？",
//                            "重新登录", "退出登录", false, new EasyAlertDialogHelper.OnDialogActionListener() {
//                                @Override
//                                public void doCancelAction() {
//                                    ToastHelper.showToast(getBaseContext(), "退出登录");
//                                    NIMClient.getService(AuthService.class).logout();
//                                    LWECache.clear();
//                                    Preferences.cleanCache();
//                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(intent);
//                                }
//
//                                @Override
//                                public void doOkAction() {
//                                    ToastHelper.showToast(getBaseContext(), "重新登录");
//                                    NIMClient.getService(AuthService.class).login(new LoginInfo(Preferences.getUserAccount(), Preferences.getUserToken()));
//                                }
//                            }).show();
                }
            }
        };
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(observer, true);
    }

    private void initializeSDK() throws Exception {
        NERtcEx.getInstance().init(getApplicationContext(), LWEConstants.APP_KEY, new LWENERtcCallback(), null);
    }

    private LoginInfo loginInfo() {
        String account = Preferences.getUserAccount();
        String token = Preferences.getUserToken();

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            LWECache.setAccount(account.toLowerCase());
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }

    private SDKOptions options() {
        SDKOptions options = new SDKOptions();
        options.appKey = LWEConstants.APP_KEY;
        options.checkManifestConfig = true;
        options.sdkStorageRootPath = getApplicationContext().getExternalCacheDir().getAbsolutePath();
        return options;
    }
}
