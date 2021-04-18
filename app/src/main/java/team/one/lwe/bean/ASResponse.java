package team.one.lwe.bean;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class ASResponse {
    private int code;
    private JSONObject info;
    private String desc;

    public ASResponse(String body) {
        JSONObject json = new JSONObject(body);
        this.code = json.getInt("code");
        this.info = json.getJSONObject("info");
        this.desc = json.getStr("desc");
    }

    public boolean isSuccess() {
        return code == 200;
    }
}

