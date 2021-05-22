package team.one.lwe.bean;

import com.netease.nimlib.sdk.auth.LoginInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthInfo {
    private String accid, token;

    public LoginInfo toLoginInfo() {
        return new LoginInfo(accid, token);
    }
}
