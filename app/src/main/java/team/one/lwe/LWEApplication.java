package team.one.lwe;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.chatroom.ChatRoomSessionCustomization;
import com.netease.nim.uikit.api.model.contact.ContactEventListener;
import com.netease.nim.uikit.business.session.actions.BaseAction;
import com.netease.nim.uikit.business.session.actions.ImageAction;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.util.NIMUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

import team.one.lwe.config.Preferences;
import team.one.lwe.crash.CrashHandler;
import team.one.lwe.ui.action.InviteAction;
import team.one.lwe.ui.activity.auth.LoginActivity;
import team.one.lwe.ui.activity.friend.FriendInfoActivity;
import team.one.lwe.ui.custom.holder.MsgViewHolderInvite;
import team.one.lwe.ui.custom.msg.CustomAttachParser;
import team.one.lwe.ui.custom.msg.InviteAttachment;

public class LWEApplication extends Application {

    private static LWEApplication instance;

    private Context current;

    public static LWEApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(@NotNull Activity activity, Bundle savedInstanceState) {
                if (activity.getParent() != null) {
                    current = activity.getParent();
                } else
                    current = activity;
            }

            @Override
            public void onActivityStarted(@NotNull Activity activity) {
                if (activity.getParent() != null) {
                    current = activity.getParent();
                } else
                    current = activity;
            }

            @Override
            public void onActivityResumed(@NotNull Activity activity) {
                if (activity.getParent() != null) {
                    current = activity.getParent();
                } else
                    current = activity;
            }

            @Override
            public void onActivityPaused(@NotNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NotNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NotNull Activity activity, @NotNull Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(@NotNull Activity activity) {

            }
        });

        NIMClient.init(this, loginInfo(), options());
        if (NIMUtil.isMainProcess(this)) {
            initUiKit();
            registerObservers();
            registerCustomMsg();
        }
        initCacheDir();
        CrashHandler crashHandler = CrashHandler.getCrashHandler();
        crashHandler.init();
    }

    private void initCacheDir() {
        File cover = new File(getApplicationContext().getExternalCacheDir() + "/cover");
        if (!cover.exists())
            cover.mkdir();
    }

    private void initUiKit() {
        NimUIKit.init(this);
        NimUIKit.setContactEventListener(new ContactEventListener() {
            @Override
            public void onItemClick(Context context, String account) {
                NimUIKit.startP2PSession(context, account);
            }

            @Override
            public void onItemLongClick(Context context, String account) {

            }

            @Override
            public void onAvatarClick(Context context, String account) {
                Intent intent = new Intent(context, FriendInfoActivity.class);
                intent.putExtra("accid", account);
                context.startActivity(intent);
            }
        });
        ChatRoomSessionCustomization crsc = new ChatRoomSessionCustomization();
        ArrayList<BaseAction> actions = new ArrayList<>();
        actions.add(new ImageAction());
        actions.add(new InviteAction());
        crsc.actions = actions;
        NimUIKit.setCommonChatRoomSessionCustomization(crsc);
    }

    private void registerCustomMsg() {
        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new CustomAttachParser());
        NimUIKit.registerMsgItemViewHolder(InviteAttachment.class, MsgViewHolderInvite.class);
    }

    private void registerObservers() {
        //登录状态监听
        Observer<StatusCode> observer = (Observer<StatusCode>) statusCode -> {
            if (statusCode == StatusCode.KICKOUT) {
                ToastHelper.showToast(current, "被踢了........");
                EasyAlertDialogHelper.createOkCancelDiolag(current, "提示", "您的帐号已在另一台设备登录，是否重新登录？",
                        "重新登录", "退出登录", false, new EasyAlertDialogHelper.OnDialogActionListener() {
                            @Override
                            public void doCancelAction() {
                                ToastHelper.showToast(current, "退出登录");
                                NIMClient.getService(AuthService.class).logout();
                                LWECache.clear();
                                Preferences.cleanCache();
                                Intent intent = new Intent(current, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void doOkAction() {
                                ToastHelper.showToast(current, "重新登录");
                                NIMClient.getService(AuthService.class).login(new LoginInfo(Preferences.getUserAccount(), Preferences.getUserToken()));
                            }
                        }).show();
            }
        };
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(observer, true);
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
