package team.one.lwe.crash;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import org.jetbrains.annotations.NotNull;

import team.one.lwe.LWEApplication;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static volatile CrashHandler crashHandler;

    public static CrashHandler getCrashHandler() {
        if (crashHandler == null) {
            synchronized (CrashHandler.class) {
                if (crashHandler == null) {
                    crashHandler = new CrashHandler();
                }
            }
        }
        return crashHandler;
    }

    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(@NotNull Thread t, final @NotNull Throwable e) {
        // 提示信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Context context = LWEApplication.getInstance().getApplicationContext();
                Intent intent = new Intent(context, CrashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("exceptionOfCrash", e.getMessage());
                context.startActivity(intent);
                Looper.loop();
            }
        }.start();
        ActivityCollector.finishAll();
        System.exit(1);
    }
}
