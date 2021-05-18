package team.one.lwe;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.util.NIMUtil;

import team.one.lwe.config.Preferences;
import team.one.lwe.db.DaoMaster;
import team.one.lwe.db.DaoSession;

public class LWEApplication extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        NIMClient.init(this, loginInfo(getApplicationContext()), options());
        if (NIMUtil.isMainProcess(this)) {
            initUiKit();
        }
        initGreenDao();
    }

    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "lwe.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    private void initUiKit() {
        NimUIKit.init(this);
        //SessionHelper.init();
        //ContactHelper.init();
        //NimUIKit.setOnlineStateContentProvider(new DemoOnlineStateContentProvider());
    }

    private LoginInfo loginInfo(Context context) {
        String account = Preferences.getUserAccount(context);
        String token = Preferences.getUserToken(context);

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
        options.sdkStorageRootPath = getApplicationContext().getExternalCacheDir().getAbsolutePath();
        return options;
    }
}
