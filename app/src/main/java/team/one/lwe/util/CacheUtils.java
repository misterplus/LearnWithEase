package team.one.lwe.util;

import java.util.List;

import team.one.lwe.LWEApplication;
import team.one.lwe.bean.AvatarCache;

public class CacheUtils {
    public static String getAvatarCache(String account) {
        List<AvatarCache> cache = LWEApplication.getInstance().getDaoSession().queryRaw(AvatarCache.class, " where account = ?", account);
        if (cache.size() == 0)
            return null;
        else
            return cache.get(0).getAvatar();
    }

    public static void saveAvatarCache(String account, String avatar) {
        AvatarCache cache = new AvatarCache(account, avatar);
        LWEApplication.getInstance().getDaoSession().insertOrReplace(cache);
    }
}
