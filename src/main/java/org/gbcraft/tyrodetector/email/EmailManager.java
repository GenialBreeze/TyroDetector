package org.gbcraft.tyrodetector.email;

import org.bukkit.entity.HumanEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.help.TimeHelperManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 邮件系统管理器，负责整合所有邮件信息，主要职能有两个，单例模式。
 * 1. 发送到达指定生命周期(email.yml中的age)的紧急邮件
 * 2. 发送周期性的邮件报告
 */
public class EmailManager {
    private static final EmailManager manager = new EmailManager();
    private final TyroDetector plugin = TyroDetector.getPlugin();
    private final Map<HumanEntity, EmailInfo> emails = new ConcurrentHashMap<>();

    public static EmailManager getManager() {
        return manager;
    }

    /**
     * 添加一封有关{@param player}的邮件 并在添加后立刻发送
     *
     * @param player 邮件描述的玩家
     */
    public void urgentAppend(HumanEntity player, EmailInfo info) {
        EmailInfo emailInfo = emails.get(player);
        if (null == emailInfo) {
            emailInfo = info;
            emails.put(player, info);
        }
        else {
            // 如果存在关于该玩家的邮件，则将内容整合进已存在的邮件中
            emailInfo.appendContent(info.getContent());
        }

        String leader = "未获取";

        if (plugin.isTypaAvailable()) {
            leader = TyroDetector.getPlugin().getPartiesConfig().getLeader(player.getUniqueId());
        }

        String title = String.format("来自风险预测模块的紧急邮件 - %s - %d 小时 - 队长:%s", player.getName(), TimeHelperManager.getPlayHours(player.getName()), leader);
        emailInfo.setTitle(title);

        if (plugin.isTypaAvailable()) {
            emailInfo.fixAddContent("队伍信息\n" + TyroDetector.getPlugin().getPartiesConfig().getPartyInfo(player.getUniqueId()));
        }

        send(emailInfo);
        emails.remove(player);
    }

    /**
     * 添加一封有关{@param player}的邮件
     *
     * @param player 邮件描述的玩家
     * @param info   邮件正文内容
     */
    public void append(HumanEntity player, EmailInfo info) {
        EmailInfo emailInfo = emails.get(player);
        if (null == emailInfo) {
            emails.put(player, info);
        }
        else {
            // 如果存在关于该玩家的邮件，则将内容整合进已存在的邮件中
            emailInfo.appendContent(info.getContent());
            // 如果关于该玩家的邮件已到达生命周期，则判定为紧急邮件并优先于周期邮件直接发送
            if (emailInfo.getAge() >= TyroDetector.getPlugin().getEmailConfig().getAge()) {
                String leader = "未获取";

                if (plugin.isTypaAvailable()) {
                    leader = plugin.getPartiesConfig().getLeader(player.getUniqueId());
                }

                String title = String.format("服务器可疑玩家预警 - %s - %d 小时 - 队长:%s", player.getName(), TimeHelperManager.getPlayHours(player.getName()), leader);
                emailInfo.setTitle(title);

                if (plugin.isTypaAvailable()) {
                    emailInfo.fixAddContent("队伍信息\n" + TyroDetector.getPlugin().getPartiesConfig().getPartyInfo(player.getUniqueId()));
                }

                send(emailInfo);
                emails.remove(player);
            }
        }
    }

    /**
     * 发送一封邮件
     *
     * @param info 邮件实例
     */
    private void send(EmailInfo info) {
        new BukkitRunnable() {
            @Override
            public void run() {
                new EmailSender().send(info.getTitle(), info.getContent());
            }
        }.runTaskAsynchronously(TyroDetector.getPlugin());
    }

    /**
     * 发送所有已缓存在邮件，仅在一个邮件周期结尾时调用
     */
    public void sendAll() {
        String title = "服务器周期日志";
        StringBuilder content = new StringBuilder();
        emails.forEach((key, value) -> {
            String leader = "未获取";
            String teamMsg = "未获取";

            if (plugin.isTypaAvailable()) {
                leader = TyroDetector.getPlugin().getPartiesConfig().getLeader(key.getUniqueId());
                teamMsg = TyroDetector.getPlugin().getPartiesConfig().getPartyInfo(key.getUniqueId());
            }

            String format = String.format("%s - %d 小时 队长:%s:\n%s\n队伍信息\n%s\n\n", key.getName(), TimeHelperManager.getPlayHours(key.getName()), leader, value.getContent(), teamMsg);

            content.append(format);
            emails.remove(key);
        });

        if (!"".equalsIgnoreCase(content.toString())) {
            send(new EmailInfo(title, content.toString()));
        }
    }

}
