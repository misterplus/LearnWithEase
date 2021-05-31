package team.one.lwe.bean;

import com.google.gson.Gson;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class ASResponse {
    private int code;
    private JSONObject info;
    private EnterRoomData chatroom;
    //token field is for video room token
    private String desc, token;

    public ASResponse(String body) {
        JSONObject json = new JSONObject(body);
        this.code = json.getInt("code");
        this.info = json.getJSONObject("info");
        this.desc = json.getStr("desc");
        this.token = json.getStr("token");
        if (json.getStr("chatroom") != null) {
            this.chatroom = new Gson().fromJson(json.getStr("chatroom"), EnterRoomData.class);
        }
    }

    public boolean isSuccess() {
        return code == 200;
    }
}

