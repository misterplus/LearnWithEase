package team.one.lwe.ui.action;

import com.netease.nim.uikit.business.session.actions.BaseAction;

import team.one.lwe.R;

public class InviteAction extends BaseAction {

    protected InviteAction() {
        super(R.drawable.nim_message_plus_photo_selector, R.string.lwe_text_invite);
    }

    @Override
    public void onClick() {
        //TODO: open friend list
        // select friend
        // send custom invite msg, containing a RoomInvite object
    }
}
