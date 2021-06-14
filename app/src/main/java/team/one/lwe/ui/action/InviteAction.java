package team.one.lwe.ui.action;

import android.app.Activity;
import android.content.Intent;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.contact.selector.activity.ContactSelectActivity;
import com.netease.nim.uikit.business.session.actions.BaseAction;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;

import team.one.lwe.R;
import team.one.lwe.bean.RoomInvite;
import team.one.lwe.ui.activity.room.RoomActivity;
import team.one.lwe.ui.callback.LWENERtcCallback;
import team.one.lwe.ui.custom.msg.InviteAttachment;

public class InviteAction extends BaseAction {

    public InviteAction() {
        super(R.drawable.lwe_button_invite, R.string.lwe_text_invite);
    }

    @Override
    public void onClick() {
        RoomActivity room = (RoomActivity) getActivity();
        ContactSelectActivity.Option option = new ContactSelectActivity.Option();
        option.title = "邀请好友";
        option.maxSelectNum = room.getMaxUsers() - LWENERtcCallback.getInstance().getUsers();
        option.maxSelectedTip = "邀请人数不能超过房间人数上限";
        NimUIKit.startContactSelector(room, option, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            ArrayList<String> selectedNames = data.getStringArrayListExtra("result_name");
            if (selectedNames == null)
                return;
            RoomActivity room = (RoomActivity) getActivity();
            RoomInvite invite = new RoomInvite();
            invite.setCoverUrl(room.getCoverUrl());
            invite.setName(room.getName());
            invite.setRoomId(room.getRoomId());
            InviteAttachment attachment = new InviteAttachment(invite);
            IMMessage message;

            for (String name : selectedNames) {
                message = MessageBuilder.createCustomMessage(name, SessionTypeEnum.P2P, attachment.getInvite().getRoomId(), attachment);
                NIMClient.getService(MsgService.class).sendMessage(message, true);
            }
        }
    }
}
