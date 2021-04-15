package team.one.lwe;

import android.app.Application;
import android.text.TextUtils;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.util.NIMUtil;

import team.one.lwe.config.Preferences;

public class LWEApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LWECache.setContext(getApplicationContext());
        NIMClient.init(this, loginInfo(), options());
        if (NIMUtil.isMainProcess(this)) {
            initUiKit();
            // service
        }
    }

    private void initUiKit() {
        NimUIKit.init(this);
        //SessionHelper.init();
        //ContactHelper.init();
        //NimUIKit.setOnlineStateContentProvider(new DemoOnlineStateContentProvider());
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
        options.appKey = "57b4c463e5a9cca9ca364a8d0d8c9f39";
        options.checkManifestConfig = true;
        options.sdkStorageRootPath = LWECache.getContext().getExternalCacheDir().getAbsolutePath();
        return options;
    }
}
