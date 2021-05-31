package team.one.lwe.bean;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomInfo {
    private int maxUsers, timeStudy, timeRest, contentStudy;
    private boolean friendsOnly;
    private String coverUrl;
}
