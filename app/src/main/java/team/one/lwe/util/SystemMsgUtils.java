package team.one.lwe.util;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.SystemMessageService;
import com.netease.nimlib.sdk.msg.model.SystemMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemMsgUtils {

    //deduplicate friend requests, keep the latest ones, setRead if removed
    public static void deduplicateFriendRequests(List<SystemMessage> msg) {
        Map<String, SystemMessage> map = new HashMap<>();
        for (SystemMessage m : msg) {
            AddFriendNotify a = (AddFriendNotify) m.getAttachObject();
            SystemMessage old = map.put(a.getAccount(), m);
            if (old != null) {
                NIMClient.getService(SystemMessageService.class).setSystemMessageRead(old.getMessageId());
            }
        }
        msg.clear();
        msg.addAll(map.values());
    }
}
