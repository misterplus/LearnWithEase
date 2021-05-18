package team.one.lwe.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class AvatarCache {
    @Unique
    private String account;
    private String avatar;

    @Generated(hash = 1767800498)
    public AvatarCache(String account, String avatar) {
        this.account = account;
        this.avatar = avatar;
    }

    @Generated(hash = 523385401)
    public AvatarCache() {
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
