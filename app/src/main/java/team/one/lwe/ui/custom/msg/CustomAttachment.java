package team.one.lwe.ui.custom.msg;

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;

import cn.hutool.json.JSONObject;

public abstract class CustomAttachment implements MsgAttachment {
    protected int type;

    CustomAttachment(int type) {
        this.type = type;
    }

    public void fromJson(JSONObject data) {
        if (data != null) {
            parseData(data);
        }
    }

    @Override
    public String toJson(boolean send) {
        return CustomAttachParser.packData(type, packData());
    }

    public int getType() {
        return type;
    }

    protected abstract void parseData(JSONObject data);

    protected abstract JSONObject packData();
}
