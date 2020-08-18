package org.gbcraft.tyrodetector.help;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.bean.Inviter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamHelper {
    private static final Map<UUID, Inviter> memToInviterRequests = new HashMap<>();
    private static final Map<UUID, BukkitTask> memInviteOutTimeTasks = new HashMap<>();

    public static UUID getTempLeader(UUID member) {
        return memToInviterRequests.get(member).getLeader();
    }

    public static Inviter popInviter(UUID member) {
        Inviter res = memToInviterRequests.get(member);
        if (null != res) {
            memToInviterRequests.remove(member);
        }
        return res;
    }

    public static void cancelOutTimeTask(UUID member) {
        BukkitTask task = memInviteOutTimeTasks.get(member);
        if (null != task) {
            task.cancel();
        }
    }

    public static void setRequests(Player member, OfflinePlayer leader, Player inviter) {
        String inviteMsg = "&4你收到了来自 " + inviter.getName() + " 的组队邀请. 队长是" + leader.getName() + " 回复&b/tyro yes&4接受邀请. 将在&c 60s &4后自动过期";
        String sendSucMsg = "&c邀请发送成功";
        inviter.sendMessage(ChatMessageHelper.getMsg(sendSucMsg));
        member.sendMessage(ChatMessageHelper.getMsg(inviteMsg));

        memToInviterRequests.put(member.getUniqueId(), new Inviter(leader.getUniqueId(), inviter.getUniqueId()));
        BukkitTask outTimeTask = Bukkit.getScheduler().runTaskLaterAsynchronously(TyroDetector.getPlugin(), () -> {
            String timeOutMsg = "&c玩家" + inviter.getName() + "对玩家" + member.getName() + "发送的组队邀请已过期";
            if (inviter.isOnline()) {
                inviter.sendMessage(ChatMessageHelper.getMsg(timeOutMsg));
            }
            if (member.isOnline()) {
                member.sendMessage(ChatMessageHelper.getMsg(timeOutMsg));
            }
            TeamHelper.popInviter(member.getUniqueId());
        }, 20 * 60);
        memInviteOutTimeTasks.put(member.getUniqueId(), outTimeTask);
    }
}
