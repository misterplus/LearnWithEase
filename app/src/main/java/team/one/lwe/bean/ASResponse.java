package team.one.lwe.bean;

import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class ASResponse {
    private int code;
    private JSONObject info;
    private ChatRoomInfo chatroom;
    //token field is for video room token
    private String desc, token;

    public ASResponse(String body) {
        JSONObject json = new JSONObject(body);
        this.code = json.getInt("code");
        this.info = json.getJSONObject("info");
        this.desc = json.getStr("desc");
        this.chatroom = json.getBean("chatroom", ChatRoomInfo.class);
        this.token = json.getStr("token");
    }

    public boolean isSuccess() {
        return code == 200;
    }
}

