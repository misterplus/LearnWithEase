package team.one.lwe.ui.action;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.contact.selector.activity.ContactSelectActivity;
import com.netease.nim.uikit.business.session.actions.BaseAction;

import team.one.lwe.R;
import team.one.lwe.ui.activity.room.RoomActivity;
import team.one.lwe.ui.callback.LWENERtcCallback;

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
}
