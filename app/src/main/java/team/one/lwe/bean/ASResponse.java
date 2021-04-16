package team.one.lwe.bean;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class ASResponse {
    private int code;
    private JSONObject info;
    private String desc;

    public ASResponse(JSONObject body) {
        this.code = body.getInt("code");
        this.info = body.getJSONObject("info");
        this.desc = body.getStr("desc");
    }

    public boolean isSuccess() {
        return code == 200;
    }
}

