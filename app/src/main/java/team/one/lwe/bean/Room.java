package team.one.lwe.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    private RoomInfo ext;
    private int roomid;
    private boolean valid;
    private String announcement, name, broadcasturl, creator;
}
