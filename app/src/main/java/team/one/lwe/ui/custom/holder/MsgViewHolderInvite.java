package team.one.lwe.ui.custom.holder;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;

import java.io.Serializable;

import team.one.lwe.LWEConstants;
import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.bean.EnterRoomData;
import team.one.lwe.bean.RoomInvite;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.activity.room.RoomActivity;
import team.one.lwe.ui.callback.LWENERtcCallback;
import team.one.lwe.ui.callback.RegularCallback;
import team.one.lwe.ui.custom.msg.InviteAttachment;
import team.one.lwe.util.APIUtils;
import team.one.lwe.util.ImgUtils;

public class MsgViewHolderInvite extends MsgViewHolderBase {

    protected ImageView roomCover;
    protected View progressCover;
    protected TextView progressLabel, roomName;

    public MsgViewHolderInvite(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public void inflateContentView() {
        roomCover = findViewById(R.id.message_item_room_cover);
        roomName = findViewById(R.id.message_item_room_name);
        progressBar = findViewById(com.netease.nim.uikit.R.id.message_item_thumb_progress_bar); // 覆盖掉
        progressCover = findViewById(com.netease.nim.uikit.R.id.message_item_thumb_progress_cover);
        progressLabel = findViewById(com.netease.nim.uikit.R.id.message_item_thumb_progress_text);
    }

    private void enterRoom(Context context, EnterRoomData enterRoomData) {
        String roomid = enterRoomData.getRoomid();
        EnterChatRoomData enterChatRoomData = new EnterChatRoomData(roomid);
        NIMClient.getService(ChatRoomService.class).enterChatRoom(enterChatRoomData).setCallback(new RegularCallback<EnterChatRoomResultData>(context) {
            @Override
            public void onSuccess(EnterChatRoomResultData data) {
                int maxUsers = (Integer) data.getRoomInfo().getExtension().get("maxUsers");
                if (data.getRoomInfo().getOnlineUserCount() > maxUsers) {
                    NIMClient.getService(ChatRoomService.class).exitChatRoom(enterChatRoomData.getRoomId());
                    ToastHelper.showToast(context, R.string.lwe_error_room_full);
                    return;
                }
                try {
                    NERtcEx.getInstance().init(context.getApplicationContext(), LWEConstants.APP_KEY, LWENERtcCallback.getInstance(), null);
                } catch (Exception e) {
                    Log.e(MsgViewHolderInvite.this.getClass().getSimpleName(), Log.getStackTraceString(e));
                    NIMClient.getService(ChatRoomService.class).exitChatRoom(enterChatRoomData.getRoomId());
                    ToastHelper.showToast(context, R.string.lwe_error_init_nertc);
                    return;
                }
                NERtcEx.getInstance().joinChannel(enterRoomData.getToken(), enterRoomData.getRoomid(), enterRoomData.getUid());
                Intent intent = new Intent(context, RoomActivity.class);
                intent.putExtra("data", (Serializable) data);
                intent.putExtra("creator", false);
                context.startActivity(intent);
            }

            @Override
            public void onFailed(int code) {
                if (code == 404) {
                    ToastHelper.showToast(context, R.string.lwe_error_room_invalid);
                    return;
                }
                ToastHelper.showToast(context, R.string.lwe_error_join_room);
            }
        });
    }

    @Override
    public void bindContentView() {
        InviteAttachment attachment = (InviteAttachment) message.getAttachment();
        RoomInvite invite = attachment.getInvite();
        String name = invite.getName();
        String roomId = invite.getRoomId();
        String coverUrl = invite.getCoverUrl();
        roomName.setText(name);
        view.setOnClickListener(v -> new NetworkThread(view) {
            @Override
            public ASResponse doRequest() {
                return APIUtils.getRoomToken(roomId);
            }

            @Override
            public void onSuccess(ASResponse asp) {
                EnterRoomData enterRoomData = new EnterRoomData(roomId, asp.getToken(), asp.getInfo().getLong("uid"));
                enterRoom(context, enterRoomData);
            }

            @Override
            public void onFailed(int code, String desc) {
                ToastHelper.showToast(context, R.string.lwe_error_join_room);
            }
        }.start());

        ImgUtils.loadRoomCover(view.getContext(), roomCover, coverUrl);
        refreshStatus();
    }

    private void refreshStatus() {
        InviteAttachment attachment = (InviteAttachment) message.getAttachment();
        RoomInvite invite = attachment.getInvite();
        if (TextUtils.isEmpty(invite.getCoverUrl())) {
            if (message.getAttachStatus() == AttachStatusEnum.fail || message.getStatus() == MsgStatusEnum.fail) {
                alertButton.setVisibility(View.VISIBLE);
            } else {
                alertButton.setVisibility(View.GONE);
            }
        }

        if (message.getStatus() == MsgStatusEnum.sending
                || (isReceivedMessage() && message.getAttachStatus() == AttachStatusEnum.transferring)) {
            progressCover.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            progressLabel.setVisibility(View.VISIBLE);
            progressLabel.setText(StringUtil.getPercentString(getMsgAdapter().getProgress(message)));
        } else {
            progressCover.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            progressLabel.setVisibility(View.GONE);
        }
    }

    @Override
    public int getContentResId() {
        return R.layout.item_invite;
    }
}
