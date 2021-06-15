package team.one.lwe.ui.action;

import android.content.Intent;

import com.netease.nim.uikit.business.session.actions.BaseAction;
import com.netease.nim.uikit.common.ToastHelper;

import team.one.lwe.R;
import team.one.lwe.config.Preferences;
import team.one.lwe.ui.activity.room.RoomActivity;
import team.one.lwe.ui.activity.room.RoomSettingActivity;

public class SettingAction extends BaseAction {

    public SettingAction() {
        super(R.drawable.lwe_button_setting, R.string.lwe_button_setting);
    }

    @Override
    public void onClick() {
        RoomActivity room = (RoomActivity) getActivity();
        if (!room.isCreator()) {
            ToastHelper.showToast(room, R.string.lwe_error_room_setting_no_perm);
            return;
        }
        Intent intent = new Intent(room, RoomSettingActivity.class);
        intent.putExtra("roomId", room.getRoomId());
        room.startActivity(intent);
    }
}
