package team.one.lwe.bean;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class ASResponse {
    private int code;
    private JSONObject info;
    private Room chatroom;
    private String desc;

    public ASResponse(String body) {
        JSONObject json = new JSONObject(body);
        this.code = json.getInt("code");
        this.info = json.getJSONObject("info");
        this.desc = json.getStr("desc");
        this.chatroom = json.getBean("chatroom", Room.class);
    }

    public boolean isSuccess() {
        return code == 200;
    }
}

