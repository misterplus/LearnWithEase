package team.one.lwe.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String username, password, name;
    private int gender;
    private UserInfo ex;
    private String icon, sign;
}
