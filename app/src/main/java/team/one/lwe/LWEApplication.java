package team.one.lwe;

import android.app.Application;
import android.text.TextUtils;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.util.NIMUtil;

import java.io.File;

import team.one.lwe.config.Preferences;
import team.one.lwe.crash.CrashHandler;

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

    private void initializeSDK() throws Exception {
        //NERtcEx.getInstance().init(getApplicationContext(), LWEConstants.APP_KEY, new LWENERtcCallback(),null);
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
