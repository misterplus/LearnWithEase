package team.one.lwe.ui.custom.msg;

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;

import cn.hutool.json.JSONObject;
import team.one.lwe.ui.custom.CustomAttachmentType;

public class CustomAttachParser implements MsgAttachmentParser {
    private static final String KEY_TYPE = "type";
    private static final String KEY_DATA = "data";

    public static String packData(int type, JSONObject data) {
        JSONObject object = new JSONObject();
        object.set(KEY_TYPE, type);
        if (data != null) {
            object.set(KEY_DATA, data);
        }
        return object.toString();
    }

    @Override
    public MsgAttachment parse(String json) {
        CustomAttachment attachment = null;
        JSONObject object = new JSONObject(json);
        int type = object.getInt(KEY_TYPE);
        JSONObject data = object.getJSONObject(KEY_DATA);
        switch (type) {
            case CustomAttachmentType.Invite:
                attachment = new InviteAttachment();
                break;
            default:
                break;
        }
        if (attachment != null) {
            attachment.fromJson(data);
        }
        return attachment;
    }
}