package org.gbcraft.tyrodetector.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gbcraft.tyrodetector.TyroDetector;
import org.gbcraft.tyrodetector.help.NameUUIDHelper;
import org.gbcraft.tyrodetector.help.TeamHelper;

import java.util.UUID;

public class InviteCommand extends TDCommand {
    public InviteCommand(TyroDetector plugin, CommandSender sender, String[] args) {
        super(plugin, sender, args);
    }

    @Override
    protected void run() {
        //获取sender uuid然后根据args(要绑定玩家的名称)获取要绑定玩家的uuid然后
        //把数据写入PlayersConfig里. 这些数据在邮件发送时作为额外数据附送
        String helpMsg0 = "/tyro bind <member>";
        String helpMsg1 = "/tyro bind <leader> <member>";
        if (args.length < 1) {
            if (sender.hasPermission("tyro.bind")) {
                sender.sendMessage(helpMsg0);
                sender.sendMessage(helpMsg1);
            }
            else {
                sender.sendMessage(helpMsg0);
            }
            return;
        }

        //有权限
        if (sender.hasPermission("tyro.bind")) {
            //管理员
            if (sender instanceof Player) {
                if (args.length >= 2) {
                    bind(args[0], args[1], true);
                }
                else {
                    bind(sender.getName(), args[0], false);
                }
            }
            //控制台
            else {
                if (args.length >= 2) {
                    bind(args[0], args[1], true);
                }
                else {
                    sender.sendMessage(helpMsg1);
                }
            }
        }
        //无权限
        else {
            //普通
            if (sender instanceof Player) {
                bind(sender.getName(), args[0], false);
            }
        }
    }

    private void bind(String leader, String member, Boolean enforce) {
        if (leader.equalsIgnoreCase(member)) {
            sender.sendMessage("禁止自娱自乐!");
            return;
        }
        UUID l = NameUUIDHelper.getUUID(leader);
        UUID m = NameUUIDHelper.getUUID(member);
        if (null == l || null == m) {
            sender.sendMessage("队长/成员未找到, 请稍后再试");
        }
        else {
            String leaderName = plugin.getPlayersConfig().getLeader(l);
            if (null != leaderName) {
                if (member.equalsIgnoreCase(leaderName)) {
                    sender.sendMessage("你并不能谋权篡位");
                }
                else {
                    sender.sendMessage("该玩家已有队伍");
                }

                return;
            }

            if (enforce) {
                plugin.getPlayersConfig().addPlayers(l, m);
            }
            else {
                UUID tempLeader = TeamHelper.getLeader(m);
                if (null != tempLeader && l == tempLeader) {
                    sender.sendMessage("已经发起过同样的邀请了.");
                }
                else {
                    TeamHelper.setRequests(m, l);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        TeamHelper.popLeader(m);
                    }, 20 * 60);
                    Player mem = Bukkit.getPlayer(m);
                    if (null != mem) {
                        mem.sendMessage(ChatColor.translateAlternateColorCodes('&', "你收到了来自 " + leader + " 的组队邀请. 回复&b/tyro yes&f接受邀请. 将在&c60s&f后自动过期"));
                    }
                }
            }
        }

    }
}
