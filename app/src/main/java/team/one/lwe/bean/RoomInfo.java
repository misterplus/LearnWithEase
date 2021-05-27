package team.one.lwe.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomInfo {
    private int maxUsers, timeStudy, timeRest, contentStudy;
    private boolean friendsOnly;
}
