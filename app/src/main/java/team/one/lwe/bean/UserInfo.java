package team.one.lwe.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private int age, grade, bak; //grade = 0 for graduated
    private String province, city, area, school; // names
}
