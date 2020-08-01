package org.gbcraft.tyrodetector.help;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.gbcraft.tyrodetector.TyroDetector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamHelper {
    private static final Map<UUID, UUID> memToLeaRequests = new HashMap<>();
    private static final Map<UUID, BukkitTask> memInviteOutTimeTasks = new HashMap<>();

    public static UUID getTempLeader(UUID member) {
        return memToLeaRequests.get(member);
    }

    public static UUID popTempLeader(UUID member) {
        UUID res = memToLeaRequests.get(member);
        if (null != res) {
            memToLeaRequests.remove(member);
        }
        return res;
    }

    public static void cancelOutTimeTask(UUID member) {
        BukkitTask task = memInviteOutTimeTasks.get(member);
        if (null != task) {
            task.cancel();
        }
    }

    public static void setRequests(Player member, OfflinePlayer leader, Player sender) {
        String inviteMsg = "&4你收到了来自 " + leader + " 的组队邀请. 回复&b/tyro yes&4接受邀请. 将在&c 60s &4后自动过期";
        String sendSucMsg = "&c邀请发送成功";
        sender.sendMessage(ChatMessageHelper.getMsg(sendSucMsg));
        member.sendMessage(ChatMessageHelper.getMsg(inviteMsg));

        memToLeaRequests.put(member.getUniqueId(), leader.getUniqueId());
        BukkitTask outTimeTask = Bukkit.getScheduler().runTaskLaterAsynchronously(TyroDetector.getPlugin(), () -> {
            String timeOutMsg = "&c向玩家" + member + "发送的邀请已过期";
            if (sender.isOnline()) {
                sender.sendMessage(ChatMessageHelper.getMsg(timeOutMsg));
            }
            TeamHelper.popTempLeader(member.getUniqueId());
        }, 20 * 60);
        memInviteOutTimeTasks.put(member.getUniqueId(), outTimeTask);
    }
}
