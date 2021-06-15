package team.one.lwe.util;

import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomUpdateInfo;

import java.util.Map;

public class RoomUtils {
    public static InvocationFuture<Void> updateTimeStudy(Map<String, Object> ext, String roomId, int timeStudy) {
        ChatRoomUpdateInfo updateInfo = new ChatRoomUpdateInfo();
        ext.put("timeStudy", timeStudy);
        updateInfo.setExtension(ext);
        return NIMClient.getService(ChatRoomService.class).updateRoomInfo(roomId, updateInfo, false, null);
    }

    public static InvocationFuture<Void> updateTimeRest(Map<String, Object> ext, String roomId, int timeRest) {
        ChatRoomUpdateInfo updateInfo = new ChatRoomUpdateInfo();
        ext.put("timeRest", timeRest);
        updateInfo.setExtension(ext);
        return NIMClient.getService(ChatRoomService.class).updateRoomInfo(roomId, updateInfo, false, null);
    }

    public static InvocationFuture<Void> updateContentStudy(Map<String, Object> ext, String roomId, int contentStudy) {
        ChatRoomUpdateInfo updateInfo = new ChatRoomUpdateInfo();
        ext.put("contentStudy", contentStudy);
        updateInfo.setExtension(ext);
        return NIMClient.getService(ChatRoomService.class).updateRoomInfo(roomId, updateInfo, false, null);
    }

    public static InvocationFuture<Void> updateFriendsOnly(Map<String, Object> ext, String roomId, boolean friendsOnly) {
        ChatRoomUpdateInfo updateInfo = new ChatRoomUpdateInfo();
        ext.put("friendsOnly", friendsOnly);
        updateInfo.setExtension(ext);
        return NIMClient.getService(ChatRoomService.class).updateRoomInfo(roomId, updateInfo, false, null);
    }
}
