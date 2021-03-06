package team.one.lwe.ui.custom.msg;

import com.google.gson.Gson;

import cn.hutool.json.JSONObject;
import team.one.lwe.bean.RoomInvite;
import team.one.lwe.ui.custom.CustomAttachmentType;

public class InviteAttachment extends CustomAttachment {

    private RoomInvite invite;

    public InviteAttachment() {
        super(CustomAttachmentType.Invite);
    }

    public RoomInvite getInvite() {
        return invite;
    }

    public void setInvite(RoomInvite invite) {
        this.invite = invite;
    }

    @Override
    protected void parseData(JSONObject data) {
        this.invite = new Gson().fromJson(data.toString(), RoomInvite.class);
    }

    @Override
    protected JSONObject packData() {
        return new JSONObject(new Gson().toJson(invite));
    }
}
