package team.one.lwe.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import team.one.lwe.enums.Background;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private int age, grade; //grade = 0 for graduated
    private Background bak;
    private String province, city, area, school; // names
}
